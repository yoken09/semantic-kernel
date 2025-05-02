package com.epam.training.gen.ai.client;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.config.EmbeddingsProperties;
import com.google.common.util.concurrent.ListenableFuture;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingClient {
    private final OpenAIAsyncClient openAiClient;
    private final QdrantClient qdrantClient;
    private final EmbeddingsProperties embeddingsProperties;

    public Flux<List<Float>> getEmbeddings(EmbeddingsOptions options) {
        return openAiClient.getEmbeddings(embeddingsProperties.getDeploymentName(), options)
                .flatMapIterable(Embeddings::getData)
                .map(EmbeddingItem::getEmbedding)
                .doOnError(ex -> log.error("Exception occurred while building embeddings", ex));
    }

    public ListenableFuture<Points.UpdateResult> getUpsertedAsync(List<Points.PointStruct> points) {
        return qdrantClient.upsertAsync(embeddingsProperties.getCollectionName(), points);
    }
}
