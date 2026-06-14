package com.wave.demo;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;

import java.util.ArrayList;
import java.util.List;

//编写Milvus相关demo
public class MilvusCreateDemo {

    //设定常量存放Milvus的连接信息
    public static final String MILVUS_HOST = "http://192.168.80.129:19530";
    //设定常量存放Token信息
    public static final String TOKEN = "root:Milvus";

    //使用main进行功能测试
    public static void main(String[] args) {
        //通过构建方法，构建Milvus配置信息
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(MILVUS_HOST) //Milvus的连接地址
                .token(TOKEN) //Milvus的Token信息
                .build();
        //通过连接配置信息构建Milvus客户端对象
        MilvusClientV2 clientV2 = new MilvusClientV2(connectConfig);
        //collection 名称
        String collectionName = "demo_collection";
        //调用构建方法创建Collection
        createCollection(clientV2,collectionName);
        //获取Milvus中的所有集合
        List<String> collectionNames = clientV2.listCollections().getCollectionNames();
        System.out.println("Milvus中的所有集合：" + collectionNames);

    }

    private static void createCollection(MilvusClientV2 clientV2, String collectionName) {
        //构建Schema,collection的结构，
        CreateCollectionReq.CollectionSchema collectionSchema = MilvusClientV2.CreateSchema()
                .addField( //构建字段
                        AddFieldReq.builder() //通过构建方法，构建字段信息
                                .fieldName("id") //字段名称
                                .dataType(DataType.Int64) //字段类型
                                .isPrimaryKey(true) //设置为主键字段
                                .autoID(false) //设置为非自增字段,需要手动赋值
                                .build()
                )
                .addField(
                        AddFieldReq.builder()
                                .fieldName("vector") //字段名称
                                .dataType(DataType.FloatVector) //字段类型，向量类型
                                .dimension(5) //向量维度
                                .build()
                )
                .addField(
                        AddFieldReq.builder()
                                .fieldName("color") //字段名称
                                .dataType(DataType.VarChar) //字段类型
                                .maxLength(512) //字段最大长度
                                .build()
                );
        //构建索引
        ArrayList<IndexParam> indexParams = new ArrayList<>();
        IndexParam indexParam = IndexParam.builder()
                .fieldName("vector") //指定索引字段
                .indexName("vector_index") //索引名称
                .indexType(IndexParam.IndexType.IVF_FLAT) //索引类型
                .metricType(IndexParam.MetricType.COSINE) //指定索引的度量类型
                .build();
        indexParams.add(indexParam);

        //构建Collection
        clientV2.createCollection(
                CreateCollectionReq.builder()
                        .collectionName(collectionName) //集合名称
                        .collectionSchema(collectionSchema) //集合结构
                        .indexParams(indexParams) //索引参数
                        .build()
        );
    }
}
