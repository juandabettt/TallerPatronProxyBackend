package com.example.demo.controller;

import com.example.demo.dto.GenerationRequest;
import com.example.demo.dto.GenerationResponse;
import com.example.demo.service.AIGenerationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIGenerationService aiGenerationService;

    public AIController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    @PostMapping("/generate")
    public GenerationResponse generate(@RequestBody GenerationRequest request) {
        return aiGenerationService.generate(request);
    }
}
