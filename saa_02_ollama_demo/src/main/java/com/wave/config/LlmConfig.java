package com.wave.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {
    //本地大模型访问地址
    private static final String OLLAMA_URL = "http://localhost:11434";
    //本地大模型名称
    private static final String OLLAMA_MODEL = "qwen3:4b";

    @Bean
    public ChatModel ollama(){
        return OllamaChatModel.builder()
                .ollamaApi(
                        OllamaApi.builder()
                                .baseUrl(OLLAMA_URL)
                                .build()
                )
                .defaultOptions(
                        OllamaChatOptions.builder()
                                .model(OLLAMA_MODEL)
                                .build()
                ).build();
    }

}
