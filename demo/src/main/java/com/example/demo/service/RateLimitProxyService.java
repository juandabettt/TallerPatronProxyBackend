package com.example.demo.service;

import com.example.demo.dto.GenerationRequest;
import com.example.demo.dto.GenerationResponse;
import com.example.demo.exception.RateLimitException;
import com.example.demo.model.UserState;

public class RateLimitProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final String userId;

    public RateLimitProxyService(AIGenerationService next, String userId) {
        this.next = next;
        this.userId = userId;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserState state = UserState.USERS.get(userId);
        int limit = getLimit(state.getPlan());
        synchronized (state) {
            if (limit != -1 && state.getRequestsThisMinute() >= limit) {
                throw new RateLimitException("Rate limit exceeded for user: " + userId);
            }
            state.recordRequest();
        }
        return next.generate(request);
    }

    private int getLimit(String plan) {
        return switch (plan) {
            case "FREE" -> 10;
            case "PRO" -> 60;
            case "ENTERPRISE" -> -1;
            default -> 10;
        };
    }
}
