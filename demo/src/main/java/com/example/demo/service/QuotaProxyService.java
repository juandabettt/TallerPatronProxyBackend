package com.example.demo.service;

import com.example.demo.dto.GenerationRequest;
import com.example.demo.dto.GenerationResponse;
import com.example.demo.exception.QuotaExceededException;
import com.example.demo.model.UserState;

public class QuotaProxyService implements AIGenerationService {

    private final AIGenerationService next;
    private final String userId;

    public QuotaProxyService(AIGenerationService next, String userId) {
        this.next = next;
        this.userId = userId;
    }

    @Override
    public GenerationResponse generate(GenerationRequest request) {
        UserState state = UserState.USERS.get(userId);
        long limit = getMonthlyLimit(state.getPlan());
        synchronized (state) {
            if (limit != -1 && state.getTokensUsedThisMonth() >= limit) {
                throw new QuotaExceededException("Quota exceeded");
            }
        }
        GenerationResponse response = next.generate(request);
        synchronized (state) {
            state.addTokens(response.getTokensUsed());
        }
        return response;
    }

    private long getMonthlyLimit(String plan) {
        return switch (plan) {
            case "FREE" -> 50_000L;
            case "PRO" -> 500_000L;
            case "ENTERPRISE" -> -1L;
            default -> 50_000L;
        };
    }
}
