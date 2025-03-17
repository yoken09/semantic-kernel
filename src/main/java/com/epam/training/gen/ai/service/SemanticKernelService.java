package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.client.SemanticKernelClient;
import com.epam.training.gen.ai.constants.Constants;
import com.epam.training.gen.ai.model.request.ChatContent;
import com.epam.training.gen.ai.model.request.Message;
import com.epam.training.gen.ai.model.response.SemanticKernelResponse;
import com.epam.training.gen.ai.model.response.ChatCompletion;
import com.epam.training.gen.ai.model.response.Messages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service
@Slf4j
public class SemanticKernelService {

    @Autowired
    SemanticKernelClient semanticKernelClient;
    ObjectMapper objectMapper = new ObjectMapper();

    public String generateAIChatContent(String prompt)  {
        String requestPayload;
        try {
            requestPayload = objectMapper.writeValueAsString(mapRequest(prompt));
            log.info("Request payload: {}", requestPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ChatCompletion chatCompletion = mapClientResponse(semanticKernelClient.getResponse(requestPayload));
        return generateApiResponse(chatCompletion, prompt);
    }

    public ChatContent mapRequest(String prompt)  {
        ArrayList<Message> messageList = new ArrayList<>();
        ChatContent chatContent = new ChatContent();
        Message message = Message.builder()
                .role(Constants.INPUT_ROLE)
                .content(prompt)
                .build();
        messageList.add(message);
        chatContent.setMessages(messageList);
        return chatContent;
    }


    private ChatCompletion mapClientResponse(String clientResponse) {
        ChatCompletion chatCompletion;
        try {
            chatCompletion = objectMapper.readValue(clientResponse, ChatCompletion.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return chatCompletion;
    }

    private String generateApiResponse(ChatCompletion chatCompletion, String prompt) {
        Messages messages = Messages.builder()
                .inputRole(Constants.INPUT_ROLE)
                .inputMessage(prompt)
                .outputRole(chatCompletion.getChoices().get(0).getMessage().getRole())
                .outputMessage(chatCompletion.getChoices().get(0).getMessage().getContent())
                .build();

        SemanticKernelResponse apiResponse = SemanticKernelResponse.builder()
                .id(chatCompletion.getId())
                .created(chatCompletion.getCreated())
                .messages(messages)
                .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}