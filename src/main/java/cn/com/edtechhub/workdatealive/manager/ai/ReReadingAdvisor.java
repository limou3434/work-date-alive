package cn.com.edtechhub.workdatealive.manager.ai;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 重读顾问, 可以手动调整是否启用
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 是否启用
     */
    private final boolean enabled; // final 确保一旦赋值就不能再被修改

    /**
     * 构造方法
     *
     * @param enabled 是否启用
     */
    public ReReadingAdvisor(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 实现非流顾问接口中的核心方法
     *
     * @param advisedRequest 请求体
     * @param chain          调用链
     * @return 响应体
     */
    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 处理请求(前置处理)
        AdvisedRequest modifiedRequest = this.before(advisedRequest);
        // 通过顾问管理器把处理好的请求 modifiedRequest 交给下一个 Advisor 或最终的模型进行处理
        AdvisedResponse response = chain.nextAroundCall(modifiedRequest);
        // 处理响应(后置处理)
        return this.after(response);
    }

    /**
     * 实现流式顾问接口中的核心方法
     *
     * @param advisedRequest 请求体
     * @param chain          调用链
     * @return 响应体
     */
    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        // 处理请求(前置处理)
        AdvisedRequest modifiedRequest = this.before(advisedRequest);
        return chain
                // 通过顾问管理器把处理好的请求 modifiedRequest 交给下一个 Advisor 或最终的模型进行处理
                .nextAroundStream(modifiedRequest)
                // 处理响应(后置处理)
                .map(this::after);

        // TODO: 对于需要更复杂处理的流式场景，可以使用 Reactor 的操‌作符
        // TODO: 可以使用 adviseContext 在 Advisor 链中共享状态
    }

    /**
     * 唯一标识符
     *
     * @return 标识符
     */
    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 调用优先级
     *
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 前置处理
     *
     * @param advisedRequest 请求体
     * @return 处理后的请求体
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {
        log.debug("[ReReadingAdvisor] 调用一次重读顾问");

        // 如果未启用则直接跳过处理
        if (!this.enabled) {
            return advisedRequest;
        }

        // 复制原本用户所有的参数键值对
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());

        // 在原本的用会参数中添加新的参数键值对
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        // 重新构建新的请求
        return AdvisedRequest
                .from(advisedRequest) // 复制旧的请求
                .userText("{re2_input_query} Read the question again: {re2_input_query}") // 重新填写提示词文本(让其阅读两次)
                .userParams(advisedUserParams) // 重新填写模板参数
                .build(); // 构建新的请求
    }

    /**
     * 后置处理
     *
     * @param advisedResponse 响应体
     * @return 处理后的响应体
     */
    private AdvisedResponse after(AdvisedResponse advisedResponse) {
        return advisedResponse;
    }

}
