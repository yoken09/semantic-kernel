package com.epam.training.gen.ai.controller;

import com.azure.core.exception.HttpResponseException;
import com.epam.training.gen.ai.model.request.PromptRequest;
import com.epam.training.gen.ai.model.request.UserRequest;
import com.epam.training.gen.ai.model.response.ChatResponse;
import com.epam.training.gen.ai.service.NavigationService;
import com.epam.training.gen.ai.service.PostalPluginService;
import com.epam.training.gen.ai.service.SemanticKernelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class SemanticKerneliController {
    @Autowired
    SemanticKernelService semanticKernelService;

    @Autowired
    PostalPluginService postalPluginService;

    private static final String INPUT_KEY = "input";
    private static final String TEMPERATURE_KEY = "temperature";

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

    @PostMapping(path = "/chat/multi-model")
    public ResponseEntity<String> multiModelResponse(@RequestBody PromptRequest promptRequest){
        try {
            if (promptRequest.isEvaluate()) {
                return ResponseEntity.ok(semanticKernelService.getChatBotResponseUsingMultiModel(promptRequest));
            } else {
                return ResponseEntity.ok(semanticKernelService.getChatBotResponseUsingSingleModel(promptRequest));
            }
        } catch (HttpResponseException exception) {
            throw new RuntimeException();
        }
    }

    @PostMapping("/postal/plugin")
    public Mono<ResponseEntity<Map<String, Object>>> processWithPlugin(@RequestParam String request) {
        if (request == null || request.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Input cannot be empty")));
        }
        return Mono.fromCallable(() -> {
            List<String> result = postalPluginService.processWithModels(request);
            return ResponseEntity.ok(Map.of("response:", Objects.requireNonNull(result)));
        });
    }
}
