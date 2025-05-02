package com.epam.training.gen.ai.app;

import com.epam.training.gen.ai.client.LLMClient;
import com.epam.training.gen.ai.handler.DataLoader;
import com.epam.training.gen.ai.handler.Retriever;
import com.epam.training.gen.ai.handler.Verifier;
import com.epam.training.gen.ai.model.rag.Article;
import com.epam.training.gen.ai.service.SemanticKernelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RAGApp {
    private final SemanticKernelService semanticKernelService;


    public String generateResponse(String prompt) throws Exception {
        LLMClient llmClient = new LLMClient(semanticKernelService);

        List<Article> articles = DataLoader.loadArticles();
        List<Article> relevant = Retriever.retrieveRelevantArticles(prompt, articles);
        String response = llmClient.generateText(prompt, relevant);

        System.out.println("Generated Response:\n" + response);
        if (Verifier.isRelevant(response, relevant)) {
            System.out.println("\n✅ Verified: Response is relevant to source content.");
        } else {
            System.out.println("\n⚠️ Warning: Response may not be fully supported by sources.");
        }

        return response;
    }
}