package com.epam.training.gen.ai.handler;

import java.util.HashMap;
import java.util.Map;

public class Embedder {
    public static Map<String, Double> embed(String text) {
        Map<String, Double> vector = new HashMap<>();
        for (String word : text.toLowerCase().split("\\W+")) {
            vector.put(word, vector.getOrDefault(word, 0.0) + 1.0);
        }
        return vector;
    }

    public static double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;

        for (String key : vec1.keySet()) {
            dot += vec1.getOrDefault(key, 0.0) * vec2.getOrDefault(key, 0.0);
            norm1 += Math.pow(vec1.get(key), 2);
        }

        for (double val : vec2.values()) {
            norm2 += Math.pow(val, 2);
        }

        return (norm1 == 0 || norm2 == 0) ? 0 : dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
