package com.example.demo.scheduler;

import com.example.demo.model.UserState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Scheduled(fixedRate = 60000)
    public void resetRequestCounters() {
        UserState.USERS.values().forEach(UserState::resetMinuteCounter);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyTokens() {
        UserState.USERS.values().forEach(UserState::resetMonthlyTokens);
    }
}
