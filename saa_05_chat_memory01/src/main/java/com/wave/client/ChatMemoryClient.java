package com.wave.client;

import com.wave.memory.MysqlChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 大模型对话记忆客户端 —— 装饰器模式包装 ChatClient
 * 自动完成：加载历史上下文 → 调用大模型 → 保存对话记录
 * 无需 AOP，无需 ThreadLocal，纯方法调用，调试直观
 */
@Component
public class ChatMemoryClient {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private MysqlChatMemory mysqlChatMemory;

    /**
     * 基础对话（带记忆管理）
     */
    public String ask(String conversationId, String question) {
        List<Message> history = this.getHistory(conversationId);
        String answer = chatClient.prompt()
                .messages(history)
                .user(question)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
        saveConversation(conversationId, question, answer);
        return String.format("会话ID: %s\n回答:%s", conversationId, answer);
    }

    /**
     * 带系统提示词的对话（带记忆管理）
     */
    public String askWithSystem(String conversationId, String question, String systemPrompt) {
        List<Message> history = this.getHistory(conversationId);

        List<Message> allMessages = new ArrayList<>();
        allMessages.add(new SystemMessage(systemPrompt));
        allMessages.addAll(history);

        String answer = chatClient.prompt()
                .messages(allMessages)
                .user(question)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();

        saveConversation(conversationId, question, answer);
        return String.format("会话ID: %s\n回答:%s", conversationId, answer);
    }

    /**
     * 获取会话历史
     */
    public List<Message> getHistory(String conversationId) {
        return mysqlChatMemory.get(conversationId);
    }

    /**
     * 清除会话记忆
     */
    public void clear(String conversationId) {
        mysqlChatMemory.clear(conversationId);
    }

    private void saveConversation(String conversationId, String question, String answer) {
        mysqlChatMemory.add(conversationId,
                List.of(new UserMessage(question), new AssistantMessage(answer)));
    }
}
