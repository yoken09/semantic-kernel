package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.embedding.EmbeddingRequest;
import com.epam.training.gen.ai.model.embedding.EmbeddingResponse;
import com.epam.training.gen.ai.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EmbeddingController {
    private final EmbeddingService embeddingService;

    @PostMapping(path = "/embeddings/build")
    public Flux<List<Float>> buildEmbeddings(@RequestParam String embeddingRequest) {
        return embeddingService.buildEmbeddings(embeddingRequest);
    }

    @PostMapping(path = "/embeddings/store")
    public Flux<String> storeEmbeddings(@RequestBody EmbeddingRequest embeddingRequest) {
        return embeddingService.storeEmbeddings(embeddingRequest);
    }

    @PostMapping(path = "/embeddings/search")
    public Flux<EmbeddingResponse> searchEmbeddings(@RequestParam String embeddingRequest) {
        return embeddingService.searchEmbeddings(embeddingRequest);
    }
}
