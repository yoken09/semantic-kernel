package com.epam.training.gen.ai.handler;

import com.epam.training.gen.ai.model.rag.Article;

import java.util.List;

public class Verifier {
    public static boolean isRelevant(String response, List<Article> context) {
        for (Article article : context) {
            if (response.toLowerCase().contains(article.getContent().substring(0, Math.min(50, article.getContent().length())).toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
