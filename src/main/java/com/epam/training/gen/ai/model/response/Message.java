package com.epam.training.gen.ai.model.response;

import lombok.Data;

@Data
public class Message{
    private String role;
    private String content;
    private CustomContent custom_content;
}