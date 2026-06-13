package com.wave.controller;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Text2ImageController {

    @Autowired
    private ImageModel imageModel;

    @Value("${spring.ai.dashscope.image.options.model}")
    private String model;

    /**
     * 文生图，根据文本内容生成图片
     * @param text
     * @return
     */
    @RequestMapping("/text2image")
    public String text2image(
            @RequestParam("text") String text) {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model(model)
                .build();
        try {
            return imageModel.call(new ImagePrompt(text,options)).getResult().getOutput().getUrl();
        } catch (NonTransientAiException e) {
            //参数非法
            return "输入文本非法，请重新输入！";
        }

    }
}
