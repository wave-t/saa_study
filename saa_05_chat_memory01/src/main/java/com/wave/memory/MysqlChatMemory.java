package com.wave.memory;

import com.wave.mapper.ChatMemoryMapper;
import com.wave.vo.AIChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 自定义实现ChatMemory
 * 基于mysql + 原生mybatis 实现
 *
 * SpringAI 提供 ChatMemory 接口 管理对话记忆
 */
@Component
public class MysqlChatMemory implements ChatMemory {

    @Autowired
    private ChatMemoryMapper chatMemoryMapper;
    @Override
    public void add(String conversationId, List<Message> messages) {
        //参数校验
        //验证conversationId参数
        if (conversationId == null || conversationId.isBlank()){
            throw new IllegalArgumentException("会话ID不能为空！");
        }
        if(messages ==null || messages.isEmpty()){
            return;
        }
        //批量转换message到po对象
        List<AIChatMemory> poList = messages.stream()
                .filter(Objects::nonNull)
                .map(message -> {
                    AIChatMemory po = new AIChatMemory();
                    po.setConversationId(conversationId);
                    po.setMessageType(message.getMessageType().name());
                    po.setContent(message.getText());
                    po.setCreateTime(LocalDateTime.now());
                    po.setUserId(conversationId);
                    return po;
                }).toList();
        //批量插入
        if (!poList.isEmpty()){
            chatMemoryMapper.insertBatch(poList);
        }
    }

    /**
     * 获取会话记录
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> get(String conversationId) {
        if (conversationId == null || conversationId.isBlank()){
            throw new IllegalArgumentException("会话ID不能为空！");
        }
        System.out.println("conversationId:"+conversationId);
        List<AIChatMemory> poList = chatMemoryMapper.queryByConversationId(conversationId);
        System.out.println(poList.size());
        if (poList.isEmpty()){
            return List.of();
        }
        List<Message> messageList = poList.stream()
                .map(this::listConvertToMessage)
                .filter(Objects::nonNull)
                .toList();
        System.out.println("历史会话条数:" + messageList.size());
        return messageList;

    }

    @Override
    public void clear(String conversationId) {
        if (conversationId == null || conversationId.isBlank()){
            throw new IllegalArgumentException("会话ID不能为空！");
        }
        chatMemoryMapper.deleteByConversationId(conversationId);

    }

    private Message listConvertToMessage(AIChatMemory po){
        System.out.println("查看历史会话对象类型" + po.getMessageType());
        if (po == null || po.getMessageType() == null){
            return null;
        }
        return switch (po.getMessageType()){
            case "USER" -> new UserMessage(po.getContent());
            case "ASSISTANT" -> new AssistantMessage(po.getContent());
            case "SYSTEM" -> new SystemMessage(po.getContent());
            default -> null;
        };
    }

    //兼容单个消息添加的重载方法
    public void add(String conversationId, Message message) {
        this.add(conversationId, List.of(message));
    }
}
