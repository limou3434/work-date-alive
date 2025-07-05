package cn.com.edtechhub.workdatealive.controller;

import cn.com.edtechhub.workdatealive.manager.ai.AIManager;
import cn.com.edtechhub.workdatealive.manager.ai.agent.Manus;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * AI 接口层
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    /**
     * 注入 AI 管理器依赖
     */
    @Resource
    AIManager aiManager;

    @GetMapping
    public String test() {
        return "ok";
    }

    // 同步接口
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return aiManager.doChat(message, chatId);
    }

    // 流式接口, 可以用 curl 'http://127.0.0.1:8000/ai/love_app/chat/sse?message=hello&chatId=1' 来测试
    @GetMapping(value = "/love_app/chat/sse")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSE(String message, String chatId) {
        return aiManager.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
        //    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        //    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        //        return aiManager.doChatByStream(message, chatId);
        //    }
    }

    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        aiManager.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回 emitter
        return emitter;
    }

    // 流式调用 Manus 超级智能体
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) { // TODO: 可以添加用户的 id
        Manus manus = aiManager.createManus();
        return manus.runStream(message);
    }


}
