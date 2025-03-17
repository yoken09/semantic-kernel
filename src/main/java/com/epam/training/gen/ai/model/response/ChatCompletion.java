package com.epam.training.gen.ai.model.response;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ChatCompletion{
    private String id;
    private String object;
    private int created;
    private ArrayList<Choice> choices;
    private Usage usage;
}
