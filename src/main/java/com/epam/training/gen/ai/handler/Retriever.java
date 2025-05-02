package com.epam.training.gen.ai.handler;

import com.epam.training.gen.ai.model.rag.Article;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Retriever {
    public static List<Article> retrieveRelevantArticles(String query, List<Article> allArticles) {
        Map<String, Double> queryVec = Embedder.embed(query);

        return allArticles.stream()
                .map(article -> Map.entry(article, Embedder.cosineSimilarity(queryVec, Embedder.embed(article.getContent()))))
                .sorted(Map.Entry.<Article, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

