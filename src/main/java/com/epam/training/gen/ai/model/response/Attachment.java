package com.epam.training.gen.ai.model.response;

import lombok.Data;

@Data
public class Attachment{
    private String title;
    private String data;
    private String type;
    private String url;
}