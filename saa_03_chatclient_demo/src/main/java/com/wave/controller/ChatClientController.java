package com.wave.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatClientController {


    @Autowired
    private ChatClient chatClient;

    @RequestMapping("/doChatStream")
    public String doChatStream(@RequestParam(name = "question",defaultValue = "你是谁？") String question) {
        return chatClient.prompt().user(question).call().content();
    }
}
