package cn.com.edtechhub.workdatealive.manager.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * AI 管理器
 */
@Component
@Slf4j
public class AIManager {

    /**
     * 等待构造引用的聊天客户端
     */
    private final ChatClient chatClient;

    /**
     * 公有构造方法
     *
     * @param aiConfig          自动引入配置类依赖
     * @param chatClientBuilder 自动引入构造类依赖
     */
    public AIManager(AiConfig aiConfig, ChatClient.Builder chatClientBuilder) {
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
                // 设置默认系统提示词, 注意这个支持使用模板文本
                .defaultSystem(String text | "{}")
                // 设置默认可调用函数(注册一个函数实例 | 注册多个函数实例), 不过这个东西要被启用了, 转而使用支持注解的 .defaultTools(), 具体可以查看 https://spring.io/blog/2025/02/14/spring-ai-1-0-0-m6-released?utm_source=chatgpt.com
                .defaultFunction(String name, String description, java.util.function.Function<I, O> function) | .defaultFunctions(String... functionBeanNames) | defaultTools(包含使用特殊注解编写的工具函数的工具类实例)
                // 设置默认用户提示词(使用纯粹文本 | 使用资源文件 | lambda 语句)
                .defaultUser(String text) | .defaultUser(Resource text) | .defaultUser(Consumer<UserSpec> userSpecConsumer)
                // 设置默认顾问中间件(顾问实例或 lambda 语句)
                .defaultAdvisors(Advisor... advisor) | defaultAdvisors(Consumer<AdvisorSpec> advisorSpecConsumer)
                // 开始构建(在这里之前其实会自动引入配置文件中所配置的 AI 相关信息)
                .build()
                ;
        */

        // 构建一个聊天客户端交给引用成员
        this.chatClient = chatClientBuilder
                .defaultSystem(aiConfig.getSystemPrompt())
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()), // 设置"记忆"顾问(内存模式)
                        new LoggerAdvisor(), // new SimpleLoggerAdvisor(), // 设置"日志"顾问
                        new ReReadingAdvisor(true) // 设置"重读"顾问(启用状态)
                )
                .build()
        ;
    }

    /**
     * 对话方法
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
                ) // 填写
                // 设置用户提示词
                .user(String text)
                // 设置顾问中间件
                .advisors(Advisor... advisor)
                // 设置开始本对话(同步 | 异步)
                .call() | .stream()
                // 设置获得本响应(同步 | 异步 | 结构), 设置结构化数据(大模型需要支持严格 json, 并且使用了这一句后就可以直接作为实例进行返回)
                .chatResponse() | .content() | .entity(类名.class)
                ;

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
                        spec -> spec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置记忆长度
                .call()
                .chatResponse();

        // 返回对话结果
        if (response != null) {
            return response.getResult().getOutput().getText();
        }
        return null;
    }

}