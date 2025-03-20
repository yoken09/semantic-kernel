package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.response.Messages;
import com.epam.training.gen.ai.service.SemanticKernelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SemanticKerneliController {
    @Autowired
    SemanticKernelService semanticKernelService;

    @GetMapping("/generateContent")
    public Messages generateContent(@RequestParam String prompt)  {
        return semanticKernelService.generateAIChatContents(prompt);
    }
}
