package com.epam.training.gen.ai.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
    private String inputRole;
    private String inputMessage;
    private String outputRole;
    private String outputMessage;
}
