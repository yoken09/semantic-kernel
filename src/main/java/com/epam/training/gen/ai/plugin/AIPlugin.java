package com.epam.training.gen.ai.plugin;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.random.RandomGenerator;

@Slf4j
public class AIPlugin {

    @DefineKernelFunction(
            name = "getRandomNumber",
            description = "function generates a random number from zero to the upper bound",
            returnType = "java.lang.Integer")
    public Mono<Integer> getRandomNumber(
            @KernelFunctionParameter(name = "upperBound", description = "The upper bound  for the random number",
                    type = Integer.class) Integer upperBound) {
        RandomGenerator random = RandomGenerator.getDefault();
        int randomInt = random.nextInt(upperBound);
        log.info("Random number between 0 and {} : {} ", upperBound, randomInt);
        return Mono.just(randomInt);
    }

    @DefineKernelFunction(
            name = "multiply",
            description = "function multiplies two numbers",
            returnType = "java.lang.Integer")
    public Mono<Integer> multiply(
            @KernelFunctionParameter(name = "a", description = "The first number to multiply",
                    type = Integer.class) Integer a,
            @KernelFunctionParameter(name = "b", description = "The second number to multiply",
                    type = Integer.class) Integer b) {
        int result = a * b;
        log.info("Multiplication of {} and {} : {} ", a, b, result);
        return Mono.just(result);
    }


    @DefineKernelFunction(
            name = "add",
            description = "function sums two numbers",
            returnType = "java.lang.Integer")
    public Mono<Integer> sum(
            @KernelFunctionParameter(name = "a", description = "The first number",
                    type = Integer.class) Integer a,
            @KernelFunctionParameter(name = "b", description = "The second number",
                    type = Integer.class) Integer b) {
        int result = a + b;
        log.info("Sum of {} and {} : {} ", a, b, result);
        return Mono.just(result);
    }

    @DefineKernelFunction(
            name = "subtract",
            description = "function subtracts two numbers",
            returnType = "java.lang.Integer")
    public Mono<Integer> subtract(
            @KernelFunctionParameter(name = "a", description = "The first number",
                    type = Integer.class) Integer a,
            @KernelFunctionParameter(name = "b", description = "The second number",
                    type = Integer.class) Integer b) {
        int result = a - b;
        log.info("Subtraction of {} and {} : {} ", a, b, result);
        return Mono.just(result);
    }

    @DefineKernelFunction(
            name = "divide",
            description = "function divides the first number by the second number",
            returnType = "java.lang.Integer")
    public Mono<Double> divide(
            @KernelFunctionParameter(name = "a", description = "dividend",
                    type = Integer.class) Integer a,
            @KernelFunctionParameter(name = "b", description = "divisor",
                    type = Integer.class) Integer b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        double result = (double) a / b;
        log.info("Division of {} by {} : {} ", a, b, result);
        return Mono.just(result);
    }
}