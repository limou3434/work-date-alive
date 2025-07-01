package cn.com.edtechhub.workdatealive.manager.ai;

import cn.com.edtechhub.workdatealive.manager.ai.advisors.LoggerAdvisor;
import cn.com.edtechhub.workdatealive.manager.ai.advisors.ReReadingAdvisor;
import cn.com.edtechhub.workdatealive.manager.ai.memorys.InFileMemory;
import cn.com.edtechhub.workdatealive.manager.ai.models.LoveReport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * AI 管理器
 */
@Component
@Slf4j
public class AIManager {

    // TODO: 编写一套包含变量的 Prompt 模板, 并保存为资源文件, 从文件加载模板

    /**
     * 注入配置依赖
     */
    @Resource
    AiConfig aiConfig;

    /**
     * 注入向量存储依赖
     */
    @Resource
    VectorStore vectorStore;

    /**
     * 注入文档加载器依赖
     */
    @Resource
    DocumentRetriever documentRetriever;

    /**
     * 注入工具列表依赖
     */
    @Resource
    private ToolCallback[] allTools;

    /**
     * 等待构造引用的聊天客户端
     */
    private final ChatClient chatClient;

    /**
     * 公有构造方法
     *
     * @param aiConfig          自动注入配置类依赖
     * @param chatClientBuilder 自动注入构造类依赖
     */
    public AIManager(AiConfig aiConfig, ChatClient.Builder chatClientBuilder) {
        // 注意这里的配置类还无法直接注入, 需要在构造函数处编写可以被自动注入的参数

        /* 关闭自动引入大模型后自定义模型引入
          // 如果需要关闭自动导入则需要先设置 spring.ai.chat.client.enabled: false, 并且补充配置关于其他大模型的配置
          ChatModel myChatModel = 根据官方文档针对不同大模型来进行创建...;
          ChatClient.Builder builder = ChatClient
                  .builder(myChatModel)
                  .build();
        */

        /* 构建一个聊天客户端交给类引用成员
        ChatClient chatClient = chatClientBuilder
                // 设置特定模型的选项
                .defaultOptions(ChatOptions chatOptions)
                // 设置默认可调用函数(注册一个函数实例 | 注册多个函数实例), 不过这个东西要被启用了, 转而使用支持注解的 .defaultTools(), 具体可以查看 https://spring.io/blog/2025/02/14/spring-ai-1-0-0-m6-released?utm_source=chatgpt.com
                .defaultFunction(String name, String description, java.util.function.Function<I, O> function) | .defaultFunctions(String... functionBeanNames) | defaultTools(包含使用特殊注解编写的工具函数的工具类实例)
                // 设置默认系统提示词, 注意这个支持使用模板文本(文本 | 资源 | lambda)
                .defaultSystem(String text | Resource text | Consumer<PromptSystemSpec> consumer)
                // 设置默认用户提示词(使用纯粹文本 | 使用资源文件 | lambda 语句)
                .defaultUser(String text) | .defaultUser(Resource text) | .defaultUser(Consumer<UserSpec> userSpecConsumer)
                // 设置默认顾问中间件(顾问实例或 lambda 语句)
                .defaultAdvisors(Advisor... advisor) | defaultAdvisors(Consumer<AdvisorSpec> advisorSpecConsumer)
                // 开始构建(在这里之前其实会自动引入配置文件中所配置的 AI 相关信息)
                .build();
        */

        // 构建一个聊天客户端交给引用成员
        this.chatClient = chatClientBuilder
                .defaultSystem(aiConfig.getSystemPrompt())
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InFileMemory(aiConfig.getDataSaveFileDir())), // new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // 设置"记忆"顾问(内存模式)
                        new LoggerAdvisor() // new SimpleLoggerAdvisor(), // 设置"日志"顾问(便于调试)
                )
                .build()
        ;
    }

    /**
     * 简单的对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 回答消息
     */
    public String doChat(String message, String chatId) {
        /*
        // 实际对话过程(非流结果和流式结果)
        ChatResponse response | Flux<String> = chatClient
                // 设置为链式构造
                .prompt(包含使用特殊注解编写的工具函数的工具类实例)
                // 设置可调用函数
                .tools()
                // 设置系统的模板, 如果有设置消息模板的话
                .system(
                        sp -> sp
                                .param("模板文本1", "填充的文本值1")
                                .param("模板文本2", "填充的文本值2")
                                .param ...
                )
                // 设置用户提示词(文本 | 资源 | lambda)
                .user(String text | Resource text | Consumer<PromptUserSpec> consumer)
                // 设置顾问中间件
                .advisors(Advisor... advisor)
                // 设置开始本对话(同步 | 异步)
                .call() | .stream()
                // 设置获得本响应(同步 | 异步 | 结构), 设置结构化数据(大模型需要支持严格 json, 并且使用了这一句后就可以直接作为实例进行返回)
                .chatResponse() | .content() | .entity(类名.class);

                // 非流读取
                response.getResult().getOutput().getText();

                // 流式读取
                response.doOnNext(line -> {
                    log.debug("[limou] 收到一段响应: {}", line); // 可以进一步考虑使用 WebSocket
                }).blockLast(); // 等到最后一个事件发出
        */

        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisorSpec -> advisorSpec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, aiConfig.getChatMemoryRetrieveSize()) // 设置记忆长度
                ) // 设置会话记忆顾问
                .call()
                .chatResponse();

        if (response != null) {
            return response.getResult().getOutput().getText();
        }
        return null;
    }

    /**
     * 支持重读的对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 回答消息
     */
    public String doReReadChat(String message, String chatId) {
        /*
        // 实际对话过程(非流结果和流式结果)
        ChatResponse response | Flux<String> = chatClient
                // 设置为链式构造
                .prompt(包含使用特殊注解编写的工具函数的工具类实例)
                // 设置可调用函数
                .tools()
                // 设置系统的模板, 如果有设置消息模板的话
                .system(
                        sp -> sp
                                .param("模板文本1", "填充的文本值1")
                                .param("模板文本2", "填充的文本值2")
                                .param ...
                )
                // 设置用户提示词(文本 | 资源 | lambda)
                .user(String text | Resource text | Consumer<PromptUserSpec> consumer)
                // 设置顾问中间件
                .advisors(Advisor... advisor)
                // 设置开始本对话(同步 | 异步)
                .call() | .stream()
                // 设置获得本响应(同步 | 异步 | 结构), 设置结构化数据(大模型需要支持严格 json, 并且使用了这一句后就可以直接作为实例进行返回)
                .chatResponse() | .content() | .entity(类名.class);

                // 非流读取
                response.getResult().getOutput().getText();

                // 流式读取
                response.doOnNext(line -> {
                    log.debug("[limou] 收到一段响应: {}", line); // 可以进一步考虑使用 WebSocket
                }).blockLast(); // 等到最后一个事件发出
        */

        // 实际对话过程
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisorSpec -> advisorSpec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, aiConfig.getChatMemoryRetrieveSize()) // 设置记忆长度
                ) // 设置会话记忆顾问
                .advisors(new ReReadingAdvisor(true)) // 设置"重读"顾问
                .call()
                .chatResponse();

        if (response != null) {
            return response.getResult().getOutput().getText();
        }
        return null;
    }

    /**
     * 返回恋爱报告的对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 恋爱报告
     */
    public LoveReport doChat2LoveReport(String message, String chatId) {
        return chatClient
                .prompt()
                .system(aiConfig.getSystemPrompt() + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, aiConfig.getChatMemoryRetrieveSize()))
                .call()
                .entity(LoveReport.class);
        // 如果观察 Advisor 下的请求体就会发现内部的 formatParam 中对象被转换为了 JSON Schema 描述语言
    }

    /**
     * 携带本地向量数据的对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 结合本地知识库回答消息
     */
    public String doChatWithLocalRag(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisorSpec -> advisorSpec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, aiConfig.getChatMemoryRetrieveSize()) // 设置记忆长度
                ) // 设置"记忆"顾问
                .advisors(new QuestionAnswerAdvisor(vectorStore)) // 设置"本地知识"顾问
                .call()
                .chatResponse();

        if (response != null) {
            return response.getResult().getOutput().getText();
        }
        return null;
    }

    /**
     * 携带远端向量数据的对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 结合远端知识库回答消息
     */
    public String doChatWithRemoteRag(String message, String chatId) {
        // 需要在 https://bailian.console.aliyun.com/?tab=app#/data-center 中提前上传资料
        // 然后在 https://bailian.console.aliyun.com/?tab=app#/knowledge-base 中创建知识库
        // 最后在 https://java2ai.com/docs/1.0.0-M6.1/tutorials/retriever/#%E7%A4%BA%E4%BE%8B%E7%94%A8%E6%B3%95 参考阿里巴巴关于 Spring AI Alibaba 关于文档检索的描述
        // 注意我们需要使用 Spring AI 提供的另一个 RAG Advisor —— RetrievalAugmentationAdvisor 检索增强顾问, 可以绑定文档检索器、查询转换器、查询增强器，更灵活地构造查询

        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisorSpec -> advisorSpec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, aiConfig.getChatMemoryRetrieveSize()) // 设置记忆长度
                ) // 设置"记忆"顾问
                .advisors(
                        RetrievalAugmentationAdvisor
                                .builder()
                                .documentRetriever(documentRetriever)
                                .build()
                ) // 设置"远端知识"顾问
                .call()
                .chatResponse();

        if (response != null) {
            return response.getResult().getOutput().getText();
        }
        return null;
    }

    /**
     * 携带工具的对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 回答消息
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisorSpec -> advisorSpec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, aiConfig.getChatMemoryRetrieveSize()) // 设置记忆长度
                )
                .tools(allTools) // 设置工具列表
                .call()
                .chatResponse();

        if (response != null) {
            return response.getResult().getOutput().getText();
        }
        return null;
    }

}