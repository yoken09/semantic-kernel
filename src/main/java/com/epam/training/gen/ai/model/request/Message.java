package com.epam.training.gen.ai.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message{
    private String role;
    private String content;
}