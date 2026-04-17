package com.example.demo.service;

import com.example.demo.dto.GenerationRequest;
import com.example.demo.dto.GenerationResponse;

public interface AIGenerationService {
    GenerationResponse generate(GenerationRequest request);
}
