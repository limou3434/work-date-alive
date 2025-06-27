
package cn.com.edtechhub.workdatealive.manager.ai.rag.remote;

import cn.com.edtechhub.workdatealive.manager.ai.AiConfig;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置远端向量存储库依赖并且导出
 */
@Configuration
public class RemoteVectorStoreConfig {

    /**
     * 注入 AI 配置类
     */
    @Resource
    private AiConfig aiConfig;

    /**
     * 创建一个文档加载器
     *
     * @param dashscopeEmbeddingModel 大模型
     * @return 向量存储
     */
    @Bean
    DocumentRetriever documentRetriever(EmbeddingModel dashscopeEmbeddingModel) {
        // 加载向量数据库 和 装载文档列表到向量数据库中(阿里云百炼平台已经实现, 我们只需要读取)

        // 配置文档加载器
        DashScopeApi dashScopeApi = new DashScopeApi(aiConfig.getApiKey()); // API 密钥
        final String KNOWLEDGE_INDEX = "工作室恋爱智体知识库"; // 远端的知识库名称
        return new DashScopeDocumentRetriever(
                dashScopeApi,
                DashScopeDocumentRetrieverOptions
                        .builder()
                        .withIndexName(KNOWLEDGE_INDEX)
                        .build()
        );
    }

}
