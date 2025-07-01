package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    // TODO: 制作一个邮箱发送工具

    /**
     * 搜索 API 密钥
     */
    @Value("${search-api.api-key}")
    private String searchApiKey;

    /**
     * 注册所有工具
     *
     * @return 所有工具
     */
    @Bean
    public ToolCallback[] allTools() {
        /*
        本代码暗含好几种设计模式:

        (1) 工厂模式: allTools() 方法作为一个工厂方法, 负责创建和配置多个工具实例, 然后将它们包装成统一的数组返回, 这符合工厂模式的核心思想 - 集中创建对象并隐藏创建细节
        (2) 依赖注入模式: 通过 @Value 注解注入配置值, 以及将创建好的工具通过 Spring 容器注入到需要它们的组件中
        (3) 注册模式: 该类作为一个中央注册点, 集中管理和注册所有可用的工具, 使它们能够被系统其他部分统一访问
        (4) 适配器模式的应用: ToolCallbacks.from 方法可以看作是一种适配器, 它将各种不同的工具类转换为统一的 ToolCallback 数组, 使系统能够以一致的方式处理它们

        有了这个注册类，如果需要添加或移除工具，只需修改这一个类即可，更利‌于维护。
        */

        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool
        );
    }

}
