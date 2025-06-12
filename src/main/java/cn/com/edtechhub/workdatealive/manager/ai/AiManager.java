package cn.com.edtechhub.workdatealive.manager.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class AiManager {

    private final ChatClient chatClient;

    /**
     * 公有构造方法
     *
     * @param aiConfig          自动引入配置类依赖
     * @param chatClientBuilder 自动引入构造类依赖
     */
    public AiManager(AiConfig aiConfig, ChatClient.Builder chatClientBuilder) {
        // 构建一个客户端
        this.chatClient = chatClientBuilder
                .defaultSystem(aiConfig.getSystemPrompt()) // 设置默认系统提示词
                .defaultAdvisors( // 设置默认顾问
                        new SimpleLoggerAdvisor(), // 设置"简易日志记录"顾问
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()) // 设置"存储件中的记忆"顾问
                )
                // 这里中间其实会自动引入配置文件中所配置的 AI 相关信息
                .build(); // 开始构建
    }

    /**
     * 对话方法
     *
     * @param message 用户消息
     * @param chatId  会话标识
     * @return 回答消息
     */
    public String doChat(String message, String chatId) {
        // 实际对话过程
        ChatResponse response = chatClient
                .prompt() // 开始链式构造提示词
                .user(message) // 设置用户提示词
                .advisors( // 设置顾问
                        spec -> spec
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 设置会话标识
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 设置记忆大小
                .call() // 开始调用
                .chatResponse(); // 获得响应

        // 返回对话结果
        String content = null;
        if (response != null) {
            content = response.getResult().getOutput().getText();
        }
        log.info("content: {}", content);
        return content;
    }

}