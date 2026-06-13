package com.wave.advice;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * 基于 ThreadLocal 的对话记忆上下文持有者
 * 在 Advice 与 Controller 之间安全传递历史消息，避免污染方法签名
 */
public final class ChatMemoryHolder {

    private static final ThreadLocal<List<Message>> HISTORY_HOLDER = new ThreadLocal<>();


    private ChatMemoryHolder() {}

    public static void init(List<Message> history) {
        HISTORY_HOLDER.set(history);
    }

    public static List<Message> getHistory() {
        return HISTORY_HOLDER.get();
    }


    public static void clear() {
        HISTORY_HOLDER.remove();
    }
}
