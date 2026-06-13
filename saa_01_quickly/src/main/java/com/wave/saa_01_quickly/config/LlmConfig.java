package com.wave.saa_01_quickly.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//多模型配置类
@Configuration
public class LlmConfig {

    //前景说明：这里的技术栈是SAA，所以其它模型的接入，
    //需要到百炼平台上去找对应的写法，不是去模型自己的平台找写法
    //DashScope API Key
    private static final String DASHSCOPE_API_KEY = System.getenv("AI_DASHSCOPE_API_KEY");
    //DeepSeek Api Key
//    private static final String DEEPSEEK_API_KEY = System.getenv("AI_DEEPSEEK_API_KEY");
    //多模型配置类，给不同的大模型注入对象

    @Bean
    public ChatModel deepSeek(){
        return DashScopeChatModel.builder()
                .dashScopeApi(
                        DashScopeApi.builder()
                                .apiKey(DASHSCOPE_API_KEY)
                                .build()
                )
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .model("deepseek-v4-pro")
                                .build()
                ).build();
    }

    @Bean
    public ChatModel qwen(){
        return DashScopeChatModel.builder()
                .dashScopeApi(
                        DashScopeApi.builder()
                                .apiKey(DASHSCOPE_API_KEY)
                                .build()
                )
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .model("qwen3.7-max")
                                .build()
                ).build();
    }
}
