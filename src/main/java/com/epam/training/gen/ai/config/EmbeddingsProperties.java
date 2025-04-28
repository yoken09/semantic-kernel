package com.epam.training.gen.ai.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "embeddings")
@Getter
@Setter
@ToString
public class EmbeddingsProperties {
    private String collectionName;
    private String deploymentName;
    private int vectorSize;
    private int searchLimit;
}