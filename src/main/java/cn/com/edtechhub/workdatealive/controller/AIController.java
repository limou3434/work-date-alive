package cn.com.edtechhub.workdatealive.controller;

import cn.com.edtechhub.workdatealive.manager.ai.AIManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 接口层
 */
@RestController
@RequestMapping("/ai")
public class AIController {

//    /**
//     * 注入 AI 管理器依赖
//     */
//    AIManager aiManager;
//
//    @GetMapping
//    public String healthCheck() {
//        return "ok";
//    }
//
//    @GetMapping("/generate")
//    public String generate(
//            @RequestParam(value = "message", defaultValue = "你是谁")
//            String message
//    ) {
//        return aiManager.doChat(message, "1");
//    }

}
