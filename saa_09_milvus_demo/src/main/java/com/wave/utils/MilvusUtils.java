package com.wave.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wave.vo.milvus.DemoMil;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.utility.request.FlushReq;
import io.milvus.v2.service.vector.request.InsertReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

//构建工具类，简化业务代码
@Component
public class MilvusUtils {

    //注入客户端对象，操作Milvus
    @Autowired
    private MilvusClientV2 clientV2;

    //注入Milvus集合名称
    @Value("${milvus.collection}")
    public String collectionName;

    //批量插入数据
    public void insert(List<DemoMil> data) {
        Gson gson = new Gson();
        //将List<DemoMil>  转为 List<JsonObject>
        List<JsonObject> dataJson = data.stream().map(item -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", item.getId());
            jsonObject.add("vector", gson.toJsonTree(item.getVector()));
            jsonObject.addProperty("color", item.getColor());
            return jsonObject;
        }).toList();

        //插入数据到Milvus中
        clientV2.insert(
                InsertReq.builder()
                        .collectionName(collectionName)
                        .data(dataJson)
                        .build()
        );
        //刷新数据,将插入的数据立刻生效
        clientV2.flush(
                FlushReq.builder()
                        .collectionNames(List.of(collectionName))
                        .build()
        );
    }
}
