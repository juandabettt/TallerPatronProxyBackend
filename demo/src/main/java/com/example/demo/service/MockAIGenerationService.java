package com.example.demo.service;

import com.example.demo.dto.GenerationRequest;
import com.example.demo.dto.GenerationResponse;

import java.util.Random;

public class MockAIGenerationService implements AIGenerationService {

    private static final String[] RESPONSES = {
        "The quick brown fox jumps over the lazy dog near the riverbank.",
        "Artificial intelligence is transforming how we interact with technology every day.",
        "Spring Boot simplifies the creation of production-ready applications with minimal configuration.",
        "Design patterns provide reusable solutions to commonly occurring problems in software design.",
        "The proxy pattern adds a level of indirection to support distributed, controlled, or intelligent access.",
        "Machine learning models require large datasets and significant computational resources to train effectively.",
        "Cloud computing enables on-demand access to a shared pool of configurable computing resources."
    };

    private final Random random = new Random();

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String generatedText = RESPONSES[random.nextInt(RESPONSES.length)];
        int tokensUsed = request.getPrompt().length() / 4 + generatedText.length();
        return new GenerationResponse(generatedText, tokensUsed);
    }
}
