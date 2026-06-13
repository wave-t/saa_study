package com.wave.controller;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class OllamaController {

    @Autowired
    private ChatModel ollama; //根据名称注入本地大模型Bean

    /**
     * 测试接口 流式输出 避免用户等待，可以边想边输出
     * @param question 问题
     * @return Flux<String> 返回响应式流，一个持续输出多个字符串的流式接口。
     */
    @RequestMapping("/doChatStream")
    public Flux<String> doChatStream(@RequestParam(name = "question",defaultValue = "你是谁？") String question) {
        // Flux 需要配合 stream方法使用。
        return ollama.stream(question);
    }
}
