package cn.com.edtechhub.workdatealive.manager.ai.agent;

import cn.com.edtechhub.workdatealive.manager.ai.enums.AgentState;
import com.itextpdf.styledxmlparser.jsoup.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础代理, 提供 "循环(While)" 的概念, 提供状态转换、内存管理和基于步骤的执行循环的基础功能, 且子类必须实现step方法
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Slf4j
public abstract class BaseAgent {

    /**
     * 代理名称
     */
    private String name;

    /**
     * 系统的提示词
     */
    private String systemPrompt;

    /**
     * 下一步提示词
     */
    private String nextStepPrompt;

    /**
     * 代理状态
     */
    private AgentState state = AgentState.IDLE; // 默认为空闲状态

    /**
     * 控制执行循环的最大步骤
     */
    private int maxSteps = 10;

    /**
     * 当前执行步骤序号
     */
    private int currentStep = 0;

    /**
     * 引入 LLM 大模型
     */
    private ChatClient chatClient;

    /**
     * 消息列表(消息上下文)
     */
    private List<Message> messageList = new ArrayList<>();

    /**
     * 运行代理
     *
     * @param userPrompt 用户提示词
     * @return 执行结果
     */
    public String run(String userPrompt) {
        // TODO: 等后期实现通用的 Maven 工具后再进行优化
        // 检查运行状态必须为空闲状态
        if (this.state != AgentState.IDLE) {
            throw new RuntimeException("当前状态为 " + this.state + "即非空闲, 无法继续执行");
        }

        // 检查用户提示词不能为空
        if (StringUtil.isBlank(userPrompt)) {
            throw new RuntimeException("无法运行带有空用户提示符的代理");
        }

        // 更改状态
        this.state = AgentState.RUNNING;

        // 记录消息上下文
        this.messageList.add(new UserMessage(userPrompt));

        // 提前创建需要保存结果的列表
        List<String> results = new ArrayList<>();

        // 循环运行代理
        try {
            // 只要不是超出步骤限制且状态不是已完成状态, 就一直循环执行
            for (int i = 0; i < this.maxSteps && this.state != AgentState.FINISHED; i++) {
                // 更新当前执行的步骤
                int stepNumber = i + 1;
                currentStep = stepNumber;

                // 执行步骤, 将结果保存到上下文列表中
                String stepResult = this.step();
                String result = "步骤 " + stepNumber + ": " + stepResult;
                results.add(result);
                log.debug("步骤 {} / 总计 {} 的执行结果 -> {}", stepNumber, maxSteps, result);
            }

            // 超出步骤限制则需要手动设置状态为已完成状态
            if (currentStep >= maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }

            // 把 results 这个字符串列表里的所有元素用换行符 \n 连接起来，然后作为一个整体字符串返
            return String.join("\n", results);
        } catch (Exception e) {
            // 只要出现任何异常, 就把状态设置为错误状态
            state = AgentState.ERROR;
            log.debug("执行出错 {}", e.getMessage());
            return "执行错误" + e.getMessage();
        } finally {
            // 清理资源
            this.cleanup();
        }
    }

    /**
     * 执行单个步骤
     *
     * @return 步骤执行结果
     */
    protected abstract String step();

    /**
     * 清理资源, 子类可以重写此方法来清理资源
     */
    protected void cleanup() {
    }

}

