package com.epam.training.gen.ai.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SemanticKernelResponse {
    private String id;
    private float created;
    private Messages messages;
}
