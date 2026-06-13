package com.wave.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class PromptController {

    @Autowired
    private ChatClient chatClient; //需要通过配置类注入

    /**
     * 创建一个prompt，并返回一个流
     * @param question
     * @return
     */
    @RequestMapping("/prompt")
    public Flux<String> prompt(@RequestParam(name = "question",defaultValue = "你是谁？") String question ) {
        return chatClient.prompt(question).stream().content();
    }

    /**
     * 创建一个prompt，并返回一个流
     * @param question
     * @return
     */
    @RequestMapping("/prompt2")
    public Flux<String> prompt2(@RequestParam(name = "question",defaultValue = "你是谁？") String question ) {
        return chatClient.prompt()
                .system("你是一位行政总厨，只能回答烹饪相关问题，其它问题都说不知道")
                .user(question)
                .stream()
                .content();
    }


    /**
     * 提示词模版方法，创建系统提示词模版对象 和 用户提示词模版对象
     * @return
     */
    @RequestMapping("/promptTemplate")
    public Flux<String> promptTemplate(@RequestParam(name = "companyName") String companyName,@RequestParam(name = "type")String type
    ,@RequestParam(name = "name") String name,@RequestParam(name = "job") String job
            ,@RequestParam(name = "entryDate") String entryDate,@RequestParam(name = "range") String range
            ,@RequestParam(name = "fare") String fare) {
        //系统提示词模版
        //1.构建提醒提示词模版内容 """是文本块符号，表示多行文本，可以包含换行符。
        String systemTemplate = """
                你是{companyName}的资深HR，精通{type}入职Offer的撰写规范。
                请根据用户提供的消息，生产一份符合{companyName}企业规范的{type}offer，规范如下：
                1.语言正式且温馨，符合{companyName}的官方文书风格；
                2.包含核心要素：入职岗位、入职日期、税前薪资、核心福利、欢迎语；
                3.以html格式输出
                4.结尾必须带上{companyName}的名称和HR联系方式
                """;
        //2.创建提示词模版对象
        PromptTemplate systemPromptTemplate = new PromptTemplate(systemTemplate);
        //3.设置模版参数
        Map<String, Object> systemMap = Map.of(
                "companyName", companyName,
                "type", type
        );
        //4.设置模版参数
        String systemContent = systemPromptTemplate.render(systemMap);
        //5.生成SystemMessage 对象
        SystemMessage systemMessage = new SystemMessage(systemContent);

        //用户提示词模版
        //1.构建提醒提示词模版内容 """是文本块符号，表示多行文本，可以包含换行符。
        String userTemplate = """
                请生成一份入职Offer，具体信息如下：
                1.候选人姓名：{name}
                2.入职岗位：{job}
                3.入职日期：{entryDate}
                4.税前薪资：{range}
                5.核心福利：{fare}
                """;
        //2.创建提示词模版对象
        PromptTemplate userPromptTemplate = new PromptTemplate(userTemplate);
        //3.设置模版参数
        Map<String, Object> userMap = Map.of(
                "name", name,
                "job", job,
                "entryDate", entryDate,
                "range", range,
                "fare", fare
        );
        //4.设置模版参数
        String userContent = userPromptTemplate.render(userMap);
        //5.生成UserMessage 对象
        UserMessage userMessage = new UserMessage(userContent);

        //创建Prompt 组装提示词,使用systemMessage, userMessage是为了告诉AI 什么是系统提示词什么是用户提示词
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        //http://localhost:8084/promptTemplate?companyName=腾达&type=游戏推广&name=yaya&job=游戏推广&entryDate=2026-7-12&range=20k&fare=六险二金,年终7薪
        return chatClient.prompt(prompt).stream().content();
    }

    /**
     * 提示词模版方法，创建系统提示词模版对象 和 用户提示词模版对象
     * 从文件中读取模版信息
     * @return
     */
    @RequestMapping("/promptTemplate2")
    public Flux<String> promptTemplate2(@RequestParam(name = "companyName") String companyName,@RequestParam(name = "type")String type
            ,@RequestParam(name = "name") String name,@RequestParam(name = "job") String job
            ,@RequestParam(name = "entryDate") String entryDate,@RequestParam(name = "range") String range
            ,@RequestParam(name = "fare") String fare) throws IOException {
        //系统提示词模版
        //1.从文件中读取模版内容
        ClassPathResource systemResource = new ClassPathResource("prompts/system.st");
        String systemTemplate = new String(systemResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        //2.创建提示词模版对象
        PromptTemplate systemPromptTemplate = new PromptTemplate(systemTemplate);
        //3.设置模版参数
        Map<String, Object> systemMap = Map.of(
                "companyName", companyName,
                "type", type
        );
        //4.设置模版参数
        String systemContent = systemPromptTemplate.render(systemMap);
        //5.生成SystemMessage 对象
        SystemMessage systemMessage = new SystemMessage(systemContent);

        //用户提示词模版
        //1.从文件中读取模版内容
        ClassPathResource userResource = new ClassPathResource("prompts/user.st");
        String userTemplate = new String(userResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        //2.创建提示词模版对象
        PromptTemplate userPromptTemplate = new PromptTemplate(userTemplate);
        //3.设置模版参数
        Map<String, Object> userMap = Map.of(
                "name", name,
                "job", job,
                "entryDate", entryDate,
                "range", range,
                "fare", fare
        );
        //4.设置模版参数
        String userContent = userPromptTemplate.render(userMap);
        //5.生成UserMessage 对象
        UserMessage userMessage = new UserMessage(userContent);

        //创建Prompt 组装提示词,使用systemMessage, userMessage是为了告诉AI 什么是系统提示词什么是用户提示词
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        //http://localhost:8084/promptTemplate?companyName=腾达&type=游戏推广&name=yaya&job=游戏推广&entryDate=2026-7-12&range=20k&fare=六险二金,年终7薪
        return chatClient.prompt(prompt).stream().content();
    }
}

