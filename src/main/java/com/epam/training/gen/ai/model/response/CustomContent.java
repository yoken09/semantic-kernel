package com.epam.training.gen.ai.model.response;

import lombok.Data;
import java.util.ArrayList;

@Data
public class CustomContent{
    private ArrayList<Attachment> attachments;
}