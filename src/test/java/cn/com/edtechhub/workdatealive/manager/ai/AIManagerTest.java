package cn.com.edtechhub.workdatealive.manager.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
@Slf4j
class AIManagerTest {

    @Resource
    private AIManager aiManager;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        log.debug("[AIManagerTest] 第一轮");
        String message = "你好，我是程序员 limou3434";
        String answer = aiManager.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第二轮
        log.debug("[AIManagerTest] 第二轮");
        message = "我想让另一半（克鲁鲁）更爱我";
        answer = aiManager.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        log.debug("[AIManagerTest] 第三轮");
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = aiManager.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }
}