package com.wave.controller;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class EmbeddingController {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Value("${spring.ai.dashscope.embedding.options.model}")
    private String model;

    /**
     * 文本嵌入向量
     * @param text
     * @return
     */
    @RequestMapping("/embedding")
    public String embedding(
            @RequestParam("text") String text
    ) {
        EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(List.of(text),
                        DashScopeEmbeddingOptions.builder()
                                .model(model)
                                .build()
                )
        );
        return Arrays.toString(embeddingResponse.getResult().getOutput());
    }
}
