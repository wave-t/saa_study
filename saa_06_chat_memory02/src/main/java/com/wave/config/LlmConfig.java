package com.wave.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

    //注入ChatMemoryBean,将管理自动记忆的对象存入
    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbc){
        //构建消息窗口记忆对象,设定最大记忆条数
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbc) //传入jdbc自动处理对象，操作数据库
                .maxMessages(5) //设定每条会话能保存的最大记忆条数
                .build();
    }

    /**
     * 注入ChatClient对象,将记忆管理对象交给ChatClient的增强器
     * Advisors 顾问 拦截请求，
     * MessageChatMemoryAdvisor 拦截器会在调佣call之前，
     * 自动调用ChatMemory接口的get方法,获取会话历史记录，并拼接到Prompt中
     * 拿到响应之后，自动调用ChatMemory接口的add方法，将本次会话记录保存到数据库中
     **/
    @Bean
    public ChatClient chatClient(ChatModel chatModel,ChatMemory memory){
        return ChatClient.builder(chatModel)
                //设置默认顾问（增强器） 消息记忆顾问，指定会话记忆对象
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }
}
