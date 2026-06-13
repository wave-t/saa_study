package com.wave.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//配置类
@Configuration
public class LlmConfig {

    //配置Bean 注入ChatClient
    /**
     * ChatClient 无法通过实例化创建对象，它是基于ChatModel创建的。
     * @param dashScopeChatModel 框架根据配置参数自动创建的对象
     * @return ChatClient Bean
     */
    @Bean
    public ChatClient chatClient(ChatModel dashScopeChatModel){
        return ChatClient.builder(dashScopeChatModel).build();
    }
}
