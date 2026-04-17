package com.example.demo.controller;

import com.example.demo.model.DailyUsage;
import com.example.demo.model.UserState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {

    private static final String USER_ID = "user-1";

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        UserState state = UserState.USERS.get(USER_ID);
        long limit = getMonthlyLimit(state.getPlan());
        long remaining = limit == -1 ? Long.MAX_VALUE : limit - state.getTokensUsedThisMonth();

        Map<String, Object> response = new HashMap<>();
        response.put("tokensUsed", state.getTokensUsedThisMonth());
        response.put("tokensRemaining", remaining);
        response.put("resetDate", state.getResetDate());
        response.put("plan", state.getPlan());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<DailyUsage>> getHistory() {
        UserState state = UserState.USERS.get(USER_ID);
        return ResponseEntity.ok(state.getHistory());
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Map<String, String>> upgrade() {
        UserState state = UserState.USERS.get(USER_ID);
        state.setPlan("PRO");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Plan upgraded successfully");
        response.put("newPlan", state.getPlan());
        return ResponseEntity.ok(response);
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
