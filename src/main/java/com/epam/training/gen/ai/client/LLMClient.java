package com.epam.training.gen.ai.client;

import com.epam.training.gen.ai.model.rag.Article;
import com.epam.training.gen.ai.service.SemanticKernelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LLMClient {
    private final SemanticKernelService semanticKernelService;


    public String generateText(String query, List<Article> context) throws Exception {
        StringBuilder prompt = new StringBuilder("Based on the following articles:\n\n");
        for (Article a : context) {
            prompt.append(a.getContent()).append("\n\n");
        }
        prompt.append("Answer this: ").append(query);
        return semanticKernelService.generateAIChatContents(prompt.toString()).toString();
    }
}
