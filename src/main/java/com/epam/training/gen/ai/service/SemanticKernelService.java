package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.response.Messages;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
@PropertySource("classpath:/config/application.properties")
public class SemanticKernelService {
    private static final String DEFAULT_MESSAGE = "No response from Chat service";
    private final Kernel kernel;
    private final ChatCompletionService chatCompletionService;

    public Messages generateAIChatContents(String prompt)  {
        return chatCompletionService.getChatMessageContentsAsync(prompt, kernel, null)
                .map(chatContent -> chatContent.stream()
                        .filter(chatMessage -> chatMessage.getAuthorRole() == AuthorRole.ASSISTANT)
                        .map(chat -> buildChatResposne(prompt, formatMessage(chat.getContent())))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(buildChatResposne(prompt, DEFAULT_MESSAGE))
                )
                .block();
    }

   private Messages buildChatResposne(String prompt, String outputMessage) {
        return Messages.builder()
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