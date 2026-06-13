package com.wave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class LlmConfig {

    //通过配置类，注入对象，设置模型参数
    // 注入了重试模版对象后，不会直接抛出异常，而是重试机制完了以后还未获取到内容才会抛出异常
    @Bean
    public RetryTemplate imageRetryTemplate() {
        //创建重试模板,处理文生图的重试策略，等待时间
        RetryTemplate retryTemplate = new RetryTemplate();
        //设置重试策略，最多重试30次
        // 总等待时间= 重试次数 * 每次间隔时间
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(30);
        retryTemplate.setRetryPolicy(retryPolicy);

        //设置退出策略 每次重试间隔2秒
        // 使用Spring Retry 正确的FixedBackOffPolicy 类
        FixedBackOffPolicy backoffPolicy = new FixedBackOffPolicy();
        backoffPolicy.setBackOffPeriod(2000L);
        retryTemplate.setBackOffPolicy(backoffPolicy);
        return retryTemplate;
    }
}
