package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.service.PostalPluginService;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostalPlugin {

    @Resource
    PostalPluginService postcodeService;

    @DefineKernelFunction(name = "get_postcode", description = "retrieve information based on postcode")
    public String getPostcodeInformation(@KernelFunctionParameter(name = "postcode") String postcode) {
        return postcodeService.fetchPostcodeDetails(postcode);
    }
}