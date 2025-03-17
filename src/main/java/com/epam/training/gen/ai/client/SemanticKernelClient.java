package com.epam.training.gen.ai.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class SemanticKernelClient {
    @Value( "${client-semantic-kernel-key}" )
    String clientKey;

    @Value( "${client-semantic-kernel-endpoint}" )
    String clientEndpoint;

    @Value( "${client-semantic-kernel-deployment-name}" )
    String clientDeploymentName;

    public String getResponse(String requestPayload)  {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(clientEndpoint))
                    .headers("Content-Type", "application/json")
                    .header("Api-Key", clientKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestPayload))
                    .build();

        HttpResponse<String> response =  HttpClient.newBuilder()
                .build()
                .send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Chat Response : {} ", response);
        return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
