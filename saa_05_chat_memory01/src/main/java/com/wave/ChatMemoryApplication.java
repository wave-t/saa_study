package com.wave;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class ChatMemoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatMemoryApplication.class, args);
        //创建容器对象，调用close方法 关闭容器
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.yml");

    }
}