package cn.com.edtechhub.workdatealive.manager.ai.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档加载器
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class DocumentLoader {

    /**
     * 保存一份资源加载器
     */
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * 构造方法
     *
     * @param resourcePatternResolver 资源加载器
     */
    public DocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 Markdown 文档
     *
     * @return 文档列表
     */
    public List<Document> loadMarkdowns() {
        // 创建一个可以保存所有文档的列表
        List<Document> allDocuments = new ArrayList<>();

        // 开始加载所有的 Markdown 文档
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                MarkdownDocumentReaderConfig config = null;
                if (fileName != null) {
                    config = MarkdownDocumentReaderConfig
                            .builder() // 创建一个构造器(构造模式)
                            .withHorizontalRuleCreateDocument(true) // 开启包含 --- 或 ___ 这类 Markdown 分隔线时创建新文档片段
                            .withIncludeCodeBlock(false) // 关闭包含 Markdown 中的 code 内容
                            .withIncludeBlockquote(false) // 关闭包含 > 引用块
                            .withAdditionalMetadata("filename", fileName) // 添加额外的元数据
                            .build(); // 最终将会构建一个配置实例
                }
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config); // 创建一个 Markdown 文档读取器
                allDocuments.addAll(reader.get()); // 将读取器中的所有文档片段添加到列表中
            }
        } catch (IOException e) {
            log.debug("Markdown 文档加载失败 {}", e.getMessage());
        }
        return allDocuments;
    }

}
