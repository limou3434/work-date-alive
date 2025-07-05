package cn.com.edtechhub.workdatealive.manager.ai.agent;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manus 代理, 集合所有功能对外提供的代理方案
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class Manus extends ToolCallAgent {

    public Manus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
        // 初始化所有工具
        super(allTools);

        // 初始化代理信息
        this.setName("Manus");

        String SYSTEM_PROMPT = """  
                你就是 Manus，一个全能的人工智能助手，旨在解决用户提出的任何任务。
                您可以使用各种工具来有效地完成复杂的请求。
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);

        String NEXT_STEP_PROMPT = """  
                根据用户需求，主动选择最合适的工具或工具组合。
                对于复杂的任务，您可以分解问题并逐步使用不同的工具来解决它。
                在使用每个工具后，清楚地解释执行结果并建议下一步。
                如果您想在任何时候停止交互，请使用 `terminate` tool/function call。
                并且需要在最大的步骤内完成任务。
                尽可能减少步骤次数，因为 API 很贵，我很穷。
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);

        this.setMaxSteps(10);

        ChatClient chatClient = ChatClient.builder(dashscopeChatModel).build();
        this.setChatClient(chatClient);
    }

}
