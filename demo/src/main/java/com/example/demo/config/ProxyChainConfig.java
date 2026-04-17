package com.example.demo.config;

import com.example.demo.service.AIGenerationService;
import com.example.demo.service.MockAIGenerationService;
import com.example.demo.service.QuotaProxyService;
import com.example.demo.service.RateLimitProxyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyChainConfig {

    @Bean
    public AIGenerationService aiGenerationService() {
        MockAIGenerationService mock = new MockAIGenerationService();
        QuotaProxyService quota = new QuotaProxyService(mock, "user-1");
        RateLimitProxyService rateLimit = new RateLimitProxyService(quota, "user-1");
        return rateLimit;
    }
}
