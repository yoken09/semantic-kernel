package com.epam.training.gen.ai.model.request;

import lombok.Data;

import java.util.List;

@Data
public class PromptRequest {
    private String prompt;
    private Double temperature;
    private Integer maxTokens;
    private String model;

}

