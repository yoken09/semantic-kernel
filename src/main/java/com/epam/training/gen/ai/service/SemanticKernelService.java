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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@PropertySource("classpath:/config/application.properties")
public class SemanticKernelService {
    public static final String GPT_35_TURBO = "gpt-35-turbo";
    private final Kernel kernel;
    private final ChatCompletionService chatCompletionService;
    private final ChatHistory history;
    @Value("${client-openai-deployment-name}")
    private String deploymentName;

    @Value("${client-openai-multi-model-name}")
    private List<String> multiModelNames;

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
         PromptExecutionSettings settings = getPromptExecutionSettings(promptRequest, deploymentName);
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

         chatResponse = handleChatResponse(response, chatResponse);

         return buildChatResponse(promptRequest.getPrompt(), chatResponse);
    }

    public String getChatBotResponseUsingSingleModel(PromptRequest promptRequest) {
        List<ChatMessageContent<?>> response = null;
        String chatResponse = StringUtils.EMPTY;
        setInitialDeploymentName(promptRequest.getModel());
        PromptExecutionSettings settings = getPromptExecutionSettings(promptRequest, deploymentName);
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

        return handleChatResponse(response, chatResponse);
    }

    public String getChatBotResponseUsingMultiModel(PromptRequest promptRequest) {
        AtomicReference<List<ChatMessageContent<?>>> response = new AtomicReference<>();
        String chatResponse = StringUtils.EMPTY;
        history.addUserMessage(promptRequest.getPrompt());
        Map<String, String> chatResponseMap = new HashMap<>();
        try {
            multiModelNames.forEach(model -> {
                PromptExecutionSettings settings = getPromptExecutionSettings(promptRequest, model);
                List<ChatMessageContent<?>> list = chatCompletionService.getChatMessageContentsAsync(history,
                                kernel, InvocationContext.builder().withPromptExecutionSettings(settings).build())
                        .onErrorMap(ex -> new Exception(ex.getMessage()))
                        .block();
                chatResponseMap.put(model, list.get(0).toString());
            });

        } catch (Exception ex) {
            log.error("Exception while retrieving chat response : {} ", ex.getMessage());
            throw new RuntimeException("Error occurred while retrieving chat response {} ", ex);
        }

        return handleChatResponseMap(chatResponseMap);
    }


    public static <K, V> String handleChatResponseMap(Map<K, V> chatMessageContents) {
        return chatMessageContents.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " ::: \n  " + entry.getValue() +"\n\n")
                .collect(Collectors.joining("\n \n"));
    }

    private static String handleChatResponse(List<ChatMessageContent<?>> response, String chatResponse) {
        if (ObjectUtils.isNotEmpty(response)) {
            chatResponse = response.stream()
                    .map(ChatMessageContent::getContent)
                    .collect(Collectors.joining(" "));
            log.info("Received chat response: {} ", response);
        } else {
            log.warn(DEFAULT_MESSAGE);
            throw new RuntimeException("No response received for the request from the chat service");
        }
        return chatResponse;
    }

    @Value("${client-openai-deployment-name}")
    public void setInitialDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    private PromptExecutionSettings getPromptExecutionSettings(PromptRequest promptRequest, String modelName) {
        return PromptExecutionSettings.builder()
                .withModelId(modelName)
                .withMaxTokens(promptRequest.getMaxTokens())
                .withTemperature(promptRequest.getTemperature())
                .build();
    }

    private String getDeploymentName(String model, String modelId) {
        if (StringUtils.isNotEmpty(model) && multiModelNames.contains(model))  {
            return model;
        }
        return GPT_35_TURBO;
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