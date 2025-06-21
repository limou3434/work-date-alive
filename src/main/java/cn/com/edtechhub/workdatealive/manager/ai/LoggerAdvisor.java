package cn.com.edtechhub.workdatealive.manager.ai;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * 日志顾问
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Slf4j
public class LoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 实现非流顾问接口中的核心方法
     *
     * @param advisedRequest 请求体
     * @param chain          调用链
     * @return 响应体
     */
    @NotNull
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
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        // 处理请求(前置处理)
        AdvisedRequest modifiedRequest = this.before(advisedRequest);

        /* 这种写法会让每次流式访问的日志都被重复打印出来
        return chain
                // 通过顾问管理器把处理好的请求 modifiedRequest 交给下一个 Advisor 或最终的模型进行处理
                .nextAroundStream(modifiedRequest)
                // 处理响应(后置处理)
                .map(this::after);
        */

        // 通过顾问管理器把处理好的请求 modifiedRequest 交给下一个 Advisor 或最终的模型进行处理
        Flux<AdvisedResponse> responses = chain.nextAroundStream(modifiedRequest);
        // 处理响应(后置处理)
        return (new MessageAggregator()).aggregateAdvisedResponse(responses, this::after); // 不能在 MessageAggregator 中修改响应, 因为它是一个只读操
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
        log.debug("[LoggerAdvisor] 顾问调用前: {}", advisedRequest);
        return advisedRequest;
    }

    /**
     * 后置处理
     *
     * @param advisedResponse 响应体
     * @return 处理后的响应体
     */
    private AdvisedResponse after(AdvisedResponse advisedResponse) {
        log.debug("[LoggerAdvisor] 顾问调用后: {}", advisedResponse);
        return advisedResponse;
    }

}
