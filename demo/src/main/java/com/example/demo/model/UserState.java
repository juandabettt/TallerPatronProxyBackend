package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class UserState {

    private String userId;
    private String plan = "FREE";
    private int requestsThisMinute = 0;
    private long tokensUsedThisMonth = 0;
    private LocalDateTime resetDate = LocalDateTime.now()
            .withDayOfMonth(1).plusMonths(1)
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
    private List<DailyUsage> history = new ArrayList<>();

    public static final Map<String, UserState> USERS;

    static {
        USERS = new ConcurrentHashMap<>();
        UserState user1 = new UserState();
        user1.setUserId("user-1");
        USERS.put("user-1", user1);
    }

    public synchronized void recordRequest() {
        requestsThisMinute++;
    }

    public synchronized void addTokens(long tokens) {
        tokensUsedThisMonth += tokens;
        updateDailyHistory(tokens);
    }

    public synchronized void resetMinuteCounter() {
        requestsThisMinute = 0;
    }

    public synchronized void resetMonthlyTokens() {
        tokensUsedThisMonth = 0;
        resetDate = LocalDateTime.now()
                .withDayOfMonth(1).plusMonths(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private void updateDailyHistory(long tokens) {
        LocalDate today = LocalDate.now();
        DailyUsage todayUsage = history.stream()
                .filter(d -> d.getDate().equals(today))
                .findFirst()
                .orElse(null);
        if (todayUsage == null) {
            todayUsage = new DailyUsage(today, 0, 0);
            history.add(todayUsage);
        }
        todayUsage.setTokensUsed(todayUsage.getTokensUsed() + tokens);
        todayUsage.setRequestCount(todayUsage.getRequestCount() + 1);
        if (history.size() > 7) {
            history.sort(Comparator.comparing(DailyUsage::getDate));
            history = new ArrayList<>(history.subList(history.size() - 7, history.size()));
        }
    }
}
