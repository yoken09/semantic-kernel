package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.app.RAGApp;
import com.epam.training.gen.ai.model.response.ChatResponse;
import com.epam.training.gen.ai.service.SemanticKernelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class RAGController {
    private final RAGApp ragApp;

    @GetMapping("/rag")
    String  getRAG(@RequestParam("prompt") String prompt) throws Exception {
        return ragApp.generateResponse(prompt);
    }
}