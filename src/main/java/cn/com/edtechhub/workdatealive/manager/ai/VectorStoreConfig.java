package cn.com.edtechhub.workdatealive.manager.ai;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 配置向量存储库依赖并且导出
 */
@Configuration
public class VectorStoreConfig {

    /**
     * 应如文档加载器依赖
     */
    @Resource
    private DocumentLoader documentLoader;

    /**
     * 创建一个向量存储
     *
     * @param dashscopeEmbeddingModel 大模型
     * @return 向量存储
     */
    @Bean
    VectorStore vectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 加载 Spring AI 实现的基于内存的简易向量数据库
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        // 加载获取文档列表
        List<Document> documents = documentLoader.loadMarkdowns();

        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }

}
