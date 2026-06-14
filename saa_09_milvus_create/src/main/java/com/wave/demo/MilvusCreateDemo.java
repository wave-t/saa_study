package com.wave.demo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.utility.request.FlushReq;
import io.milvus.v2.service.vector.request.DeleteReq;
import io.milvus.v2.service.vector.request.GetReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.GetResp;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.QueryResp;

import java.util.ArrayList;
import java.util.Arrays;
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
        //调用删除方法
//        delete(clientV2, collectionName);
        //调用查询方法
//        query(clientV2, collectionName);
        //调用批量插入方法
//        insertBatch( clientV2, collectionName);
        //调用构建方法创建Collection
//        createCollection(clientV2,collectionName);
        //获取Milvus中的所有集合
//        List<String> collectionNames = clientV2.listCollections().getCollectionNames();
//        System.out.println("Milvus中的所有集合：" + collectionNames);

    }

    //构建删除方法
    public static void delete(MilvusClientV2 clientV2, String collectionName) {
        DeleteResp delete = clientV2.delete(
                DeleteReq.builder() //构建删除请求
                        .collectionName(collectionName) //要删除的集合名称
                        .ids(List.of(1)) //要删除的id
                        .build()
        );
    }
    //构建查询方法
    public static void query(MilvusClientV2 clientV2, String collectionName) {
        GetResp getResp = clientV2.get(
                GetReq.builder() //构建查询请求
                        .collectionName(collectionName) //要查询的集合名称
                        .outputFields(List.of("id", "color")) //指定返回的字段
                        .ids(List.of(1, 2, 3)) //指定查询的id
                        .build()
        );
        for (QueryResp.QueryResult queryResult : getResp.getGetResults()){
            System.out.println(queryResult.toString());
        }
    }

    //构建方法。批量插入测试数据到Milvus中
    public static void insertBatch(MilvusClientV2 clientV2, String collectionName) {
        //构建测试数据，字段有 id：整型,vector：五维向量数据,color：带颜色标记的字符串 构建10条
        Gson gson = new Gson();
        List<JsonObject> data = Arrays.asList(
                gson.fromJson("{\"id\":1,\"vector\":[0.1,0.2,0.3,0.4,0.5],\"color\":\"#FF0000\"}", JsonObject.class),
                gson.fromJson("{\"id\":2,\"vector\":[0.2,0.3,0.4,0.5,0.6],\"color\":\"#00FF00\"}", JsonObject.class),
                gson.fromJson("{\"id\":3,\"vector\":[0.3,0.4,0.5,0.6,0.7],\"color\":\"#0000FF\"}", JsonObject.class),
                gson.fromJson("{\"id\":4,\"vector\":[0.4,0.5,0.6,0.7,0.8],\"color\":\"#FFFF00\"}", JsonObject.class),
                gson.fromJson("{\"id\":5,\"vector\":[0.5,0.6,0.7,0.8,0.9],\"color\":\"#FF00FF\"}", JsonObject.class),
                gson.fromJson("{\"id\":6,\"vector\":[0.6,0.7,0.8,0.9,1.0],\"color\":\"#00FFFF\"}", JsonObject.class),
                gson.fromJson("{\"id\":7,\"vector\":[0.7,0.8,0.9,1.0,1.1],\"color\":\"#FF0000\"}", JsonObject.class),
                gson.fromJson("{\"id\":8,\"vector\":[0.8,0.9,1.0,1.1,1.2],\"color\":\"#00FFFF\"}", JsonObject.class),
                gson.fromJson("{\"id\":9,\"vector\":[0.9,1.0,1.1,1.2,1.3],\"color\":\"#00FFFF\"}", JsonObject.class),
                gson.fromJson("{\"id\":10,\"vector\":[1.0,1.1,1.2,1.3,1.4],\"color\":\"#FF0000\"}", JsonObject.class)
        );
        //插入数据到Milvus中
        InsertResp insert = clientV2.insert(
                InsertReq.builder()
                        .collectionName(collectionName)
                        .data(data)
                        .build()
        );
        //刷新数据,将插入的数据立刻生效
        clientV2.flush(
                FlushReq.builder()
                        .collectionNames(List.of(collectionName))
                        .build()
        );
        System.out.println("数据插入成功！");
    }

    /**
     * 创建Collection
     * @param clientV2
     * @param collectionName
     */
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
                                .dimension(1024) //向量维度
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
