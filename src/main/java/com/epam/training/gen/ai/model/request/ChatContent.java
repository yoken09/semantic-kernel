package com.epam.training.gen.ai.model.request;

import lombok.Data;
import java.util.ArrayList;

@Data
public class ChatContent{
    private ArrayList<Message> messages;
}
