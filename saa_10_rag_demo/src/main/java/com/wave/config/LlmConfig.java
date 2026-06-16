package com.wave.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.param.R;
import io.milvus.param.collection.FlushParam;
import io.milvus.param.collection.GetCollectionStatisticsParam;
import io.milvus.response.GetCollStatResponseWrapper;
import jakarta.annotation.PostConstruct;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

@Configuration
public class LlmConfig {

    @Autowired
    private MilvusVectorStore milvusVectorStore;

    @Value("${spring.ai.vectorstore.milvus.collection-name}")
    private String collectionName;

    //注入ChatClient对象
    @Bean
    public ChatClient chatClient(ChatModel dashScopChatModel) {
        return ChatClient.builder(dashScopChatModel).build();
    }

    //配置 RetrievalAugmentationAdvisor 增强器
    @Bean
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor() {
        //构建VectorStoreDocumentRetriever对象
        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(milvusVectorStore) //设置操作向量数据库对象
                .similarityThreshold(0.2) //设置相似度阈值,最低阈值为0.2才能取出数据 0-1 0不相关，1完全相同
                .topK(5) //设置返回数据量 最多返回5条
                .build();
        //构建ContextualQueryAugmenter对象
        ContextualQueryAugmenter cqa = ContextualQueryAugmenter.builder()
                .allowEmptyContext(true) //相似度搜索结果为空，允许使用原始问题调用大模型
                .build();
        //构造RetrievalAugmentationAdvisor对象
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(retriever)
                .queryAugmenter(cqa)
                .build();
    }

    //创建初始化方法，将文档内容向量化到向量数据库中
    @PostConstruct //当前类初始化方法 构造函数执行完毕，属性注入完成后执行
    public void initVectorData() throws Exception {
        System.out.println("初始化向量数据...");
        MilvusServiceClient client = (MilvusServiceClient)milvusVectorStore.getNativeClient().get();
        //查询集合信息
        R<GetCollectionStatisticsResponse> statistics = client.getCollectionStatistics(
                GetCollectionStatisticsParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        //获取集合数据总条数
        long rowCount = new GetCollStatResponseWrapper(statistics.getData()).getRowCount();
        System.out.println("集合数据总条数：" + rowCount);
        //判断数量是否为0
        if (rowCount == 0) {
            System.out.println("集合数据为空，开始向量化数据...");
            //加载文档数据到向量数据库
            loadDocumentToVectorStore();
            //刷新向量数据库
            client.flush(
                    FlushParam.newBuilder()
                            .withCollectionNames(List.of(collectionName))
                            .build()
            );
        }
    }

    //加载文档数据到向量数据库
    private void loadDocumentToVectorStore() throws IOException, TikaException {
        //加载文档数据
        //默认读取的路径是项目中的resources目录
        ClassPathResource resource = new ClassPathResource("面试问题.pdf");
        Tika tika = new Tika();
        String text = tika.parseToString(resource.getFile());
        //拆分文档，写入向量数据库
        //使用TokenTextSplitter 拆分文本
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800) //分片大小
                .withMinChunkSizeChars(400) //最小分片字符数
                .withKeepSeparator(true) //是否保留分隔符
                .build();
        List<Document> documentList = splitter.apply(List.of(new Document(text)));
        milvusVectorStore.add(documentList); //添加文档数据
    }
}
