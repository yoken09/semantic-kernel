package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.request.PromptRequest;
import com.epam.training.gen.ai.model.response.ChatResponse;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@PropertySource("classpath:/config/application.properties")
public class SemanticKernelService {
    private final Kernel kernel;
    private final ChatCompletionService chatCompletionService;
    private final ChatHistory history;
    @Value("${client-openai-deployment-name}")
    private String modelId;

    private static final String DEFAULT_MESSAGE = "No response received from Chat service";

    public ChatResponse generateAIChatContents(String prompt)  {
        ChatHistory history = new ChatHistory();
        history.addUserMessage(prompt);
        return chatCompletionService.getChatMessageContentsAsync(history, kernel, null)
                .map(chatContent -> chatContent.stream()
                        .filter(chatMessage -> chatMessage.getAuthorRole() == AuthorRole.ASSISTANT)
                        .map(chat -> buildChatResponse(prompt, formatMessage(chat.getContent())))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(buildChatResponse(prompt, DEFAULT_MESSAGE))
                )
                .block();
    }

     public ChatResponse getChatBotResponseUsingPrompt(PromptRequest promptRequest) {
        List<ChatMessageContent<?>> response = null;
         String chatResponse = StringUtils.EMPTY;
         PromptExecutionSettings settings = getPromptExecutionSettings(promptRequest);
         history.addUserMessage(promptRequest.getPrompt());

         try {
            response = chatCompletionService.getChatMessageContentsAsync(history,
                            kernel, InvocationContext.builder().withPromptExecutionSettings(settings).build())
                    .onErrorMap(ex -> new Exception(ex.getMessage()))
                    .block();
        } catch (Exception ex) {
            log.error("Exception while retrieving chat response : {} ", ex.getMessage());
            throw new RuntimeException("Error occurred while retrieving chat response {} ", ex);
        }

        if (ObjectUtils.isNotEmpty(response)) {
            chatResponse = response.stream()
                    .map(ChatMessageContent::getContent)
                    .collect(Collectors.joining(" "));
            log.info("Received chat response: {} ", response);
        } else {
            log.warn(DEFAULT_MESSAGE);
            throw new RuntimeException("No response received for the request from the chat service");
        }

        return buildChatResponse(promptRequest.getPrompt(), chatResponse);
    }

    private PromptExecutionSettings getPromptExecutionSettings(PromptRequest promptRequest) {
        PromptExecutionSettings settings = PromptExecutionSettings.builder()
                .withModelId(modelId)
                .withMaxTokens(promptRequest.getMaxTokens())
                .withTemperature(promptRequest.getTemperature())
                .build();
        return settings;
    }

    private ChatResponse buildChatResponse(String prompt, String outputMessage) {
        return ChatResponse.builder()
                .inputRole(AuthorRole.USER.toString())
                .inputMessage(formatMessage(prompt))
                .outputRole(AuthorRole.ASSISTANT.toString())
                .outputMessage(outputMessage != null ? outputMessage : DEFAULT_MESSAGE)
                .build();
    }

    public String formatMessage(String message) {
        return Objects.nonNull(message) ? message.trim().replace("\n"," ").replace("\"","") : StringUtils.EMPTY;
    }
}