package com.epam.training.gen.ai.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserRequest{

    @JsonProperty("input")
    private String userRequest;

    private String id;
}

