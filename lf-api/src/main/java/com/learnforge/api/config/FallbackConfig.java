package com.learnforge.api.config;

import com.learnforge.api.client.learning.fallback.LearningClientFallback;
import com.learnforge.api.client.promotion.PromotionClient;
import com.learnforge.api.client.promotion.fallback.PromotionClientFallback;
import com.learnforge.api.client.remark.fallback.RemarkClientFallback;
import com.learnforge.api.client.trade.fallback.TradeClientFallback;
import com.learnforge.api.client.user.fallback.UserClientFallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackConfig {
    @Bean
    public LearningClientFallback learningClientFallback(){
        return new LearningClientFallback();
    }

    @Bean
    public TradeClientFallback tradeClientFallback(){
        return new TradeClientFallback();
    }

    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }


    @Bean
    public RemarkClientFallback remarkClientFallback(){
        return new RemarkClientFallback();
    }

    @Bean
    public PromotionClientFallback promotionClientFallback(){
        return new PromotionClientFallback();
    }
}
