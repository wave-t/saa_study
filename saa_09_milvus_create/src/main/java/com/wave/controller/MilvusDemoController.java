package com.wave.controller;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.google.gson.JsonArray;
import com.wave.utils.MilvusUtils;
import com.wave.vo.milvus.DemoMil;
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
public class MilvusDemoController {

    @Autowired
    private EmbeddingModel embeddingModel;
    @Value("${spring.ai.dashscope.embedding.options.model}")
    private String model;
    @Autowired
    private MilvusUtils milvusUtils;
    /**
     * 文本嵌入向量
     * @param text
     * @return
     */
    @RequestMapping("/embedding")
    public String embedding(
            @RequestParam("text") String text
    ) {
        //调用模型,将文本处理为向量
        EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(List.of(text),
                        DashScopeEmbeddingOptions.builder()
                                .model(model)
                                .build()
                )
        );
        //获取向量
        float[] output = embeddingResponse.getResult().getOutput();
        JsonArray jsonArray = new JsonArray();

        milvusUtils.insert(List.of(new DemoMil(12,output,"#FFF999")));
        return "转换成功！";
    }
}
