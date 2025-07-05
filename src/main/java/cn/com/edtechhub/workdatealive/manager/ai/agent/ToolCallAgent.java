package cn.com.edtechhub.workdatealive.manager.ai.agent;

import cn.com.edtechhub.workdatealive.manager.ai.enums.AgentState;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具代理, 提供 "观察(Observe)" 的概念
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    /**
     * 引入可用的所有工具依赖
     */
    @Resource
    private final ToolCallback[] availableTools;

    /**
     * 临时保存需要调用的工具信息
     */
    private ChatResponse toolCallChatResponse;

    /**
     * 工具调用管理者, 需要开发者手动调用
     */
    private final ToolCallingManager toolCallingManager;

    /**
     * 禁用内置的工具调用机制, 自己维护上下文
     */
    private final ChatOptions chatOptions;

    /**
     * 构造方法
     *
     * @param availableTools 闯入可用的工具列表
     */
    public ToolCallAgent(ToolCallback[] availableTools) {
        super();

        // 禁用 Spring AI 内置的工具调用机制, 自己维护选项和消息上下文
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions
                .builder()
                .withProxyToolCalls(true) // 这里用来禁止 Spring AI 托管工具调用, 让我们自己手动实现, 虽然官方提供的示例代码是设置 internalToolExecutionEnbled 为 false 来禁用 Spring AI 托管工具调用, 但由于我们使用的是阿里的 DashScopeChatModel 大模型客户端, 如果使用会直接导致工具调用失效!
                .build();
    }

    /**
     * 处理当前状态并决定下一步行动
     *
     * @return 是否需要执行行动
     */
    @Override
    public boolean think() {
        // 若是存在下一步提示词, 则有则以用户消息的形式添加到消息上下文中 // TODO: 实际上每次思考都会重新添加这个下一步的提示词, 不过可以考虑后续拓展
        if (this.getNextStepPrompt() != null && !this.getNextStepPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(this.getNextStepPrompt());
            this.getMessageList().add(userMessage);
        }

        // 利用全部消息上下文重新构造自己控制工具调用的提示词
        List<Message> messageList = this.getMessageList();
        Prompt prompt = new Prompt(messageList, this.chatOptions);
        try {
            // 禁止之前注册的工具被 Spring AI 自动调用, 然后获取携带工具选项的响应
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .system(this.getSystemPrompt())
                    .tools(this.availableTools) // 相当于只把工具的代码发送给了大模型, 让其判断需要使用哪些工具, 但是调度现在不依赖 Spring AI 了, 需要我们自己后续在 act 中手动执行
                    .call()
                    .chatResponse();
            // TODO: 如果有实现登陆逻辑, 就可以在这里考虑加入关于用户的记忆

            // 临时记录下携带工具后的响应
            this.toolCallChatResponse = chatResponse;

            // 分析思考结果
            AssistantMessage assistantMessage = null; // 获取顾问消息
            if (chatResponse != null) {
                assistantMessage = chatResponse.getResult().getOutput();
            }

            String result = null; // 获取顾问消息中的文本消息
            if (assistantMessage != null) {
                result = assistantMessage.getText();
            }

            List<AssistantMessage.ToolCall> toolCallList = null; // 获取顾问消息中的工具列表
            if (assistantMessage != null) {
                toolCallList = assistantMessage.getToolCalls();
            }

            String toolCallInfo = null; // 获取顾问消息中的工具调用信息
            if (toolCallList != null) {
                toolCallInfo = toolCallList
                        .stream()
                        .map(
                                toolCall -> String.format(
                                        "工具名称 - %s, 调用参数 %s",
                                        toolCall.name(),
                                        toolCall.arguments()
                                )
                        )
                        .collect(Collectors.joining("\n"));
            }

            log.debug("{} 的思考决定选择 {} 个工具来使用", this.getName(), toolCallList != null ? toolCallList.size() : 0);

            // 只有没有需要调用工具时, 才记录助手消息; 而有需要调用工具时, 无需记录助手消息, 因为调用工具时会自动记录
            if (toolCallList != null && toolCallList.isEmpty()) {
                this.getMessageList().add(assistantMessage);
                log.debug("{} 认为不需要使用工具, 应此只能添加助手消息到消息上下文中", this.getName());
                return false;
            } else {
                log.debug("{} 使用工具后的预调用信息为 {}", this.getName(), toolCallInfo);
            }
        } catch (Exception e) {
            this.getMessageList().add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
            log.debug("{} 的思考过程遇到了问题 {}", getName(), e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * 执行工具调用并处理结果
     *
     * @return 执行结果
     */
    @Override
    public String act() {
        // 判断是否有需要调用的工具
        if (!this.toolCallChatResponse.hasToolCalls()) {
            return "本次思考认为没有需要调用的工具调用";
        }

        // 调用工具, 并且重新设置消息上下文
        Prompt prompt = new Prompt(this.getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = this.toolCallingManager.executeToolCalls(prompt, this.toolCallChatResponse);
        this.setMessageList(toolExecutionResult.conversationHistory()); // 记录消息上下文, conversationHistory 已经包含了助手消息和工具调用返回的结果, 所以直接设置就可以

        // 获取当前工具调用的结果, 判断是否调用了终止工具
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory()); // 只获取工具调用之后生成的最后一条消息
        String results = toolResponseMessage
                .getResponses()
                .stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务, 结果为 " + response.responseData())
                .collect(Collectors.joining("\n"));

        boolean terminateToolCalled = toolResponseMessage
                .getResponses()
                .stream()
                .anyMatch(response -> "doTerminate".equals(response.name()));
        if (terminateToolCalled) {
            setState(AgentState.FINISHED);
        }

        log.debug("{} 在本次执行的结果为 {}", this.getName(), results);
        return results;
    }

}

