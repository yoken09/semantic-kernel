package com.epam.training.gen.ai.handler;

import com.epam.training.gen.ai.model.rag.Article;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;

public class DataLoader {
    public static List<Article> loadArticles() {
        try (InputStream is = DataLoader.class.getResourceAsStream("/articles.json")) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(is, new TypeReference<List<Article>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load articles", e);
        }
    }
}
