package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.request.UserRequest;
import com.epam.training.gen.ai.plugin.AIPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.AuthorRole;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/plugin")
@Slf4j
public class PluginController {

    ChatCompletionService chatCompletionService;
    Kernel kernel;
    PromptExecutionSettings defaultPromptExecutionSettings;
    Map<String, ChatHistory> usersChatHistory = new HashMap<>();


    @PostMapping
    public List<String> getAiResponse(@RequestBody UserRequest userRequest) {
        var input = userRequest.getUserRequest();
        var id = userRequest.getId();
        var invocationContext = createInvocationContext();
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.addUserMessage(userRequest.getUserRequest());

        return chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                .map(response -> response
                        .stream()
                        .map(ChatMessageContent::getContent)
                        .peek(content -> {
                            if (Objects.nonNull(content)) {
                                chatHistory.addMessage(AuthorRole.ASSISTANT, content);
                            }
                        })
                        .toList()
                )
                .block();
    }

    @GetMapping("/{id}")
    public ChatHistory getChatHistory(@PathVariable String id) {
        var chatHistory = usersChatHistory.get(id);
        if (chatHistory != null) {
            chatHistory.getMessages().forEach(message ->
                    log.info("Role: {}, Message: {}", message.getAuthorRole(), message.getContent())
            );
        }

        return chatHistory;
    }

    private InvocationContext createInvocationContext() {
        return InvocationContext.builder()
                .withPromptExecutionSettings(defaultPromptExecutionSettings)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();
    }


}