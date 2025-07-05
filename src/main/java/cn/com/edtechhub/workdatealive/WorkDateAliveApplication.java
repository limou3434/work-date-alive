package cn.com.edtechhub.workdatealive;

import cn.com.edtechhub.workdatealive.manager.ai.AIManager;
import cn.com.edtechhub.workdatealive.manager.ai.agent.Manus;
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
//        String chatId = "1";
//        String message;

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

//        message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
//        log.debug("[TEST] 回答 {}", aiManager.doChatWithRemoteRag(message, chatId));

//        message = "帮我搜索一些哄另一半开心的图片";
//        log.debug("[TEST] 回答 {}", aiManager.doChatWithMcp(message, chatId));

//        Manus manus = new Manus();
//
//        String userPrompt = """
//                我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点。
//                并结合一些网络图片，这些图片必须真实存在，并且注意图片的格式和原来下载的一致，制定一份详细的约会计划。
//                最后一个步骤需要把所有收集到的结果和资源整理为 Markdown 格式的文件进行输出，文件名字为 my_live.md, 再把 Markdown 文件转化为纯文本后再转为 PDF 格式输出，文件名字为 my_live.pdf。
//                你可以一次选择多个工具进行调用。
//                """;
//        String answer = manus.run(userPrompt);
//
//        log.debug("[TEST] 回答 {}", answer);

    }

}

// TODO: 把整个项目的单元测试代码也加入项目自己, 让 AI 自己检查自己的代码也是非常好的创新点
// TODO: 可以优化一些关于 if 判断是否抛出异常的代码, 引入通用工具包
// TODO: 给智能体添加循环检测和处理机制，防止智能体陷入无限循环
// TODO: 尝试给每一个类都加上单元测试
// TODO: 智能体可以支持交互式执行, 可以向用户询问信息或获取反馈, 从而优化‌任务的完成效果
