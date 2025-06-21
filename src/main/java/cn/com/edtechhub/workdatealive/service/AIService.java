package cn.com.edtechhub.workdatealive.service;

import org.springframework.ai.chat.client.ChatClient;

/**
 * AI 服务层
 */
public interface AIService {

    /**
     * 与 AI 进行文本对话
     *
     * @return 对话结果
     */
    String textDialogue();

}
