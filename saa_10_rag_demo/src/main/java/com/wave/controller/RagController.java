package com.wave.controller;

import org.checkerframework.checker.units.qual.A;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private RetrievalAugmentationAdvisor retrievalAugmentationAdvisor;

    @RequestMapping("/rag")
    public String rag(
            @RequestParam("question") String question
    ) {
        return chatClient.prompt()
                .user(question)
                .advisors(retrievalAugmentationAdvisor)
                .call()
                .content();
    }
}
