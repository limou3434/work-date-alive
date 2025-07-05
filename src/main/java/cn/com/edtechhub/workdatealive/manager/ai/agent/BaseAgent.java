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
    private String name = "AI"; // 默认为 "AI"

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
     * 引入 LLM 大模型的客户端
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
     * @return 返回思考的整个步骤执行结果
     */
    public String run(String userPrompt) {
        // 检查运行状态必须为空闲状态
        if (this.state != AgentState.IDLE) {
            log.debug("当前状态为 {} 即非空闲, 无法继续执行", this.state);
            throw new RuntimeException("当前状态为 " + this.state + "即非空闲, 无法继续执行");
        }

        // 检查用户提示词不能为空传递
        if (StringUtil.isBlank(userPrompt)) {
            log.debug("无法运行带有空用户提示符的代理");
            throw new RuntimeException("无法运行带有空用户提示符的代理");
        }

        // 更改智能体的状态为运行状态
        this.state = AgentState.RUNNING;
        log.debug("模型此时的状态为 {}", this.state);

        // 新建立一个用户消息记录添加到消息上下文中
        this.messageList.add(new UserMessage(userPrompt));

        // 循环运行代理, 得到整个循环过程中的结果列表
        List<String> results = new ArrayList<>();
        try {
            // 只要不是超出步骤限制且状态不是已完成状态, 就一直循环执行
            for (int i = 0; i < this.maxSteps && this.state != AgentState.FINISHED; i++) {
                // 先更新当前执行的步骤
                int stepNumber = i + 1;
                this.currentStep = stepNumber;

                // 执行步骤, 将结果保存到结果列表中
                String stepResult = this.step();
                String result = "步骤 " + stepNumber + " 的执行结果为 " + stepResult;
                log.debug("步骤 {} / 总计 {} 的执行结果 -> {}", stepNumber, maxSteps, result);
                results.add(result);
            }

            // 超出步骤限制则需要手动设置状态为已完成状态
            if (this.currentStep >= this.maxSteps) {
                this.state = AgentState.FINISHED;
                log.debug("模型此时的状态为 {}", this.state);
                results.add("终止, 达到最大步数 (" + maxSteps + ")");
            }

            // 把 results 这个字符串列表里的所有元素用换行符 \n 连接起来, 然后再作为一个整体字符串返
            return String.join("\n", results);
        } catch (Exception e) {
            // 只要出现任何异常, 就把状态设置为错误状态
            state = AgentState.ERROR;
            log.debug("执行出错 {}", e.getMessage());
            return "执行错误" + e.getMessage();
        } finally {
            this.cleanup(); // 清理资源
        }
    }

    /**
     * 子类必须要实现的单个执行步骤抽象方法
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
