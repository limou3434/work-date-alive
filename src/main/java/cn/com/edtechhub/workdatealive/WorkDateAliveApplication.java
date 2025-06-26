package cn.com.edtechhub.workdatealive;

import cn.com.edtechhub.workdatealive.manager.ai.AIManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动程序
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@SpringBootApplication
@Slf4j
public class WorkDateAliveApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(WorkDateAliveApplication.class, args);
        String baseUrl = "http://127.0.0.1:8000";
        log.debug("访问 {} 即可得到在线文档, 访问 {} 即可得到文档配置", baseUrl + "/doc.html", baseUrl + "/v3/api-docs");
        log.debug("Spring Boot running...");

        AIManager aiManager = context.getBean(AIManager.class);

        // 测试对话
        String chatId = "1";
        String message;

//        // 第一轮
//        log.debug("[TEST] 第一轮");
//        message = "你好，我是程序员 limou3434";
//        String answer = aiManager.doChatReturnString(message, chatId);
//        log.debug("[TEST] 回答 {}", answer);
//
//        // 第二轮
//        log.debug("[TEST] 第二轮");
//        message = "我想让另一半（克鲁鲁）更爱我";
//        answer = aiManager.doChatReturnString(message, chatId);
//        log.debug("[TEST] 回答 {}", answer);
//
//        // 第三轮
//        log.debug("[TEST] 第三轮");
//        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
//        answer = aiManager.doChatReturnString(message, chatId);
//        log.debug("[TEST] 回答 {}", answer);
//
//        // 恋爱报告
//        log.debug("[TEST] 测试生成恋爱报告");
//        message = "为我生成一份恋爱报告";
//        LoveReport loveReport = aiManager.doChatReturnLoveReport(message, chatId);
//        log.debug("[TEST] 回答 {}", loveReport);

//        message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
//        log.debug("[TEST] 回答 {}", aiManager.doChatWithLocalRag(message, chatId));

    }

}
