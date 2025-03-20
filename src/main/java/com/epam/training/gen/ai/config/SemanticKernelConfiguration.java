package com.epam.training.gen.ai.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SemanticKernelConfiguration {

    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient client, @Value("${client-openai-deployment-name}") String deploymentModel)  {
        return OpenAIChatCompletion.builder()
                .withOpenAIAsyncClient(client)
                .withModelId(deploymentModel)
                .build();
    }

    @Bean
    public Kernel kernel(ChatCompletionService openAIChatCompletion) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, openAIChatCompletion)
                .build();
    }
}