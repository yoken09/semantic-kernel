package com.epam.training.gen.ai.service;

import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.EmbeddingClient;
import com.epam.training.gen.ai.config.EmbeddingsProperties;
import com.epam.training.gen.ai.handler.EmbeddingResponseHandler;
import com.epam.training.gen.ai.model.embedding.EmbeddingRequest;
import com.epam.training.gen.ai.model.embedding.EmbeddingResponse;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.WithPayloadSelectorFactory;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.SearchPoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final QdrantClient qdrantClient;
    private final EmbeddingClient embeddingClient;
    private final EmbeddingsProperties embeddingsProperties;
    private final EmbeddingResponseHandler embeddingResponseHandler;

    public Flux<List<Float>> buildEmbeddings(String embeddingRequest) {
        var options = new EmbeddingsOptions(List.of(embeddingRequest));
        options.setDimensions(embeddingsProperties.getVectorSize());
        return embeddingClient.getEmbeddings(options);
    }

    public Flux<String> storeEmbeddings(EmbeddingRequest embeddingRequest) {
        return buildEmbeddings(embeddingRequest.getText())
                .map(vector -> PointStruct.newBuilder().setId(id(UUID.nameUUIDFromBytes(embeddingRequest.getText().getBytes())))
                        .setVectors(vectors(vector))
                        .putAllPayload(Map.of("text", value(embeddingRequest.getText()),
                                "category", value(embeddingRequest.getCategory())))
                        .build()).buffer()
                .map(embeddingClient::getUpsertedAsync)
                .flatMap(embeddingResponseHandler::handleMonoResponse)
                .map(result -> result.getStatus().name());
    }

    public Flux<EmbeddingResponse> searchEmbeddings(String embeddingRequest) {
        embeddingRequest = embeddingRequest.replace("\"", "");
        return buildEmbeddings(embeddingRequest)
                .map(this::generateSearchPoints).map(qdrantClient::searchAsync)
                .flatMap(embeddingResponseHandler::handleMonoResponse).flatMapIterable(points -> points)
                .map(point -> new EmbeddingResponse(
                        point.getPayloadOrDefault("category", value("(no data)")).getStringValue(),
                        point.getScore()));
    }

    private SearchPoints generateSearchPoints(List<Float> vector) {
        return SearchPoints.newBuilder().setCollectionName(embeddingsProperties.getCollectionName())
                .addAllVector(vector)
                .setLimit(embeddingsProperties.getSearchLimit())
                .setWithPayload(WithPayloadSelectorFactory.enable(true)).build();
    }
}