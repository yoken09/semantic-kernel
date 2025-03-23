package com.epam.training.gen.ai.controller;

import com.azure.core.exception.HttpResponseException;
import com.epam.training.gen.ai.model.request.PromptRequest;
import com.epam.training.gen.ai.model.response.ChatResponse;
import com.epam.training.gen.ai.service.SemanticKernelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SemanticKerneliController {
    @Autowired
    SemanticKernelService semanticKernelService;

    @GetMapping("/generateContent")
    public ChatResponse generateContent(@RequestParam String prompt)  {
        return semanticKernelService.generateAIChatContents(prompt);
    }

    @PostMapping(path = "/chat/prompt")
    public ResponseEntity<ChatResponse> getChatResponseWithPrompt(@RequestBody PromptRequest promptRequest) throws RuntimeException {
        try {
            return ResponseEntity.ok(semanticKernelService.getChatBotResponseUsingPrompt(promptRequest));
        } catch (HttpResponseException exception) {
            throw new RuntimeException();
        }
    }

}
