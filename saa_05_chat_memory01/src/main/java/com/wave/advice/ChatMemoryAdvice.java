package com.wave.advice;

import com.wave.memory.MysqlChatMemory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Aspect
public class ChatMemoryAdvice {

    @Autowired
    private MysqlChatMemory mysqlChatMemory;
    @Autowired
    private ChatMemoryContext chatMemoryContext;

    @Pointcut("execution(String com.wave.controller.ChatMemoryContorller.chat1())")
    private void pt() {}

    @Around("pt()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("进入切面.................");
        Object[] args = pjp.getArgs();
        String conversationId = (String) args[0];
        String question = (String) args[1];
        List<Message> historyMessages = mysqlChatMemory.get(conversationId);
//        ChatMemoryHolder.init(historyMessages);
        chatMemoryContext.setHistory(historyMessages);
        try {
            Object result = pjp.proceed();
            UserMessage userMessage = new UserMessage(question);
            AssistantMessage assistantMessage = new AssistantMessage(result.toString());
            mysqlChatMemory.add(conversationId, List.of(userMessage, assistantMessage));
            return String.format("会话ID: %s\n回答:%s", conversationId, result);
        } finally {
            ChatMemoryHolder.clear();
        }
    }
}
