package com.wave.advice;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Component
@RequestScope
public class ChatMemoryContext {

    private List<Message> history;

    public List<Message> getHistory() {
        return history;
    }

    public void setHistory(List<Message> history) {
        this.history = history;
    }
}
