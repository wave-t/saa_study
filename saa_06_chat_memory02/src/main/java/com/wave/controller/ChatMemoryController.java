package com.wave.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatMemoryController {

    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/ask")
    public String ask(
            @RequestParam(name = "userId")String userId, //用户ID
            @RequestParam(name = "conversationId")String conversationId, //会话ID
            @RequestParam(name = "question") String question //用户问题
    ){
        //获取会话ID 保证唯一性
        String uniqueConversationId = userId + "_" +conversationId;
        return chatClient.prompt()
                //添加增强器，设置会话记忆ID参数
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, uniqueConversationId))
                .user(question)
                .call()
                .content();
    }
}
