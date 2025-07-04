package cn.com.edtechhub.workdatealive.manager.ai.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 思行代理, 提供 "推理(Reason)、行动(Act)" 的概念
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent {

    /**
     * 执行思考
     *
     * @return 是否需要执行行动, true 表示需要执行, false 表示无需执行
     */
    public abstract boolean think();

    /**
     * 执行行动
     *
     * @return 行动执行结果
     */
    public abstract String act();

    /**
     * 单个步骤的实现, 每次步骤实际上都是一次 "思考-行动", 符合 ReAct 的思想
     *
     * @return 步骤执行结果
     */
    @Override
    public String step() {
        try {
            // 执行一次 "思考-行动"
            boolean shouldAct = think();
            if (!shouldAct) {
                String result = "思考完成 - 无需行动";
                log.debug(result);
                return result;
            }
            return act();
        } catch (Exception e) {
            // 捕获所有异常
            return "本次步骤执行失败, 错误原因为: " + e.getMessage();
        }
    }

}
