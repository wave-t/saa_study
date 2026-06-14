package com.wave.config;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Milvus配置类
@Configuration
public class MilvusConfig {
    //从配置文件中加载参数
    @Value("${milvus.host}")
    private String MILVUS_HOST = "http://192.168.80.129:19530";
    @Value("${milvus.token}")
    private String TOKEN = "root:Milvus";

    //注入Milvus连接配置信息对象
    @Bean
    public ConnectConfig connectConfig() {
        return ConnectConfig.builder()
                .uri(MILVUS_HOST)
                .token(TOKEN)
                .build();
    }
    //注入Milvus客户端对象
    @Bean
    public MilvusClientV2 milvusClientV2(ConnectConfig connectConfig) {
        return new MilvusClientV2(connectConfig);
    }
}
