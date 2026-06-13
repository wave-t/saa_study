package com.wave.controller;

import com.wave.advice.ChatMemoryContext;
import com.wave.advice.ChatMemoryHolder;
import com.wave.client.ChatMemoryClient;
import com.wave.memory.MysqlChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatMemoryContorller {

    @Autowired
    private ChatMemoryClient chatMemoryClient;

    @RequestMapping("/ask")
    public String ask(
            @RequestParam(name = "conversationId")String conversationId, //会话ID
            @RequestParam(name = "question") String question //用户问题
    ){
        return chatMemoryClient.ask(conversationId, question);
    }

    @RequestMapping("/chat1")
    public String chat1(
            @RequestParam(name = "conversationId")String conversationId, //会话ID
            @RequestParam(name = "question") String question //用户问题
    ){
        return chatMemoryClient.ask(conversationId, question);
    }
}
