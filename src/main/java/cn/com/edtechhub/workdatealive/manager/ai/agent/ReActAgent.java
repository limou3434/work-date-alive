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
     * 子类必须要实现的执行思考抽象方法
     *
     * @return 是否需要执行行动, true 表示需要执行, false 表示无需执行
     */
    public abstract boolean think();

    /**
     * 子类必须要实现的执行行动抽象方法
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
            boolean shouldAct = this.think();
            if (!shouldAct) {
                String result = "思考完成 - 无需行动";
                log.debug(result);
                return result;
            }
            return this.act();
        } catch (Exception e) { // 捕获所有异常
            log.debug("执行出错, 错误原因为 {}", e.getMessage());
            return "本次步骤执行失败, 错误原因为 " + e.getMessage();
        }
        // 无论是思考完毕还是执行出错, 都需要终止循环, 这个可以交给中止工具来实现
    }

}
