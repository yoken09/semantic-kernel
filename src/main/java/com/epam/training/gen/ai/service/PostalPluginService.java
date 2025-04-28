package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.postcode.response.PostCodeResponse;
import com.epam.training.gen.ai.plugin.GeoLocationPlugin;
import com.epam.training.gen.ai.plugin.PostalPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


@RequiredArgsConstructor
@Service
@Slf4j
@PropertySource("classpath:/config/application.properties")
public class PostalPluginService {
    private static final String DEFAULT_CITY = "Mumbai";
    private final ChatCompletionService chatCompletionService;

    private static final Map<String, String> POSTAL_CODES = new HashMap<>();

    static {
        POSTAL_CODES.put("110001","Delhi");
        POSTAL_CODES.put("400001","Mumbai");
        POSTAL_CODES.put("600001","Chennai");
        POSTAL_CODES.put("500081","Hyderabad");
        POSTAL_CODES.put("560001","Bangalore");
        POSTAL_CODES.put("700001","Kolkata");
        POSTAL_CODES.put("411001","Pune");
    }

    private static final String PROMPT = """
                Provide the best restaurants form
                this locations %s.
                Provide response as a list""";


    public List<String> processWithModels(String request) {
        Kernel kernel = getKernel();
        KernelFunction<String> prompt = KernelFunction.<String>createFromPrompt(
                        String.format(PROMPT, fetchPostcodeDetails(request)))
                .build();

        var response = prompt.invokeAsync(kernel).block();

        return splitString(response.getResult());
    }

    private KernelPlugin getPlugin() {
        return KernelPluginFactory.createFromObject(new PostalPlugin(), "PostalPlugin");
    }

    private Kernel getKernel() {
        return Kernel.builder()
                .withPlugin(getPlugin())
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    private List<String> splitString(String initialString) {
        return initialString == null || initialString.isEmpty()
                ? Collections.emptyList()
                : Arrays.stream(initialString.split("\n")).toList();
    }

    public String fetchPostcodeDetails(String pincode) {
       if (StringUtils.isNotEmpty(pincode) && POSTAL_CODES.containsKey(pincode)) {
           return POSTAL_CODES.get(pincode);
       }
       return DEFAULT_CITY; // Added default city for avoiding failures
    }
}