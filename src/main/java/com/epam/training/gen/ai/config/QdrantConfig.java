package com.epam.training.gen.ai.config;


import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Configuration
public class QdrantConfig {

    @Bean
    public QdrantClient qdrantClients(EmbeddingsProperties embeddingsProperties) throws InterruptedException, ExecutionException {
        var client = new QdrantClient(QdrantGrpcClient.newBuilder("localhost", 6334, false).build());
        collectionExists(client, embeddingsProperties.getCollectionName());
        return client;
    }

    private void collectionExists(QdrantClient qdrantClient, String collectionName) throws InterruptedException, ExecutionException {
        var isPresent = qdrantClient.collectionExistsAsync(collectionName).get();
        if (!isPresent) {
            qdrantClient.createCollectionAsync(collectionName, VectorParams.newBuilder().setDistance(Distance.Cosine).setSize(4).build()).get();
        }
    }
}