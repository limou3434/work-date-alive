package cn.com.edtechhub.workdatealive.service.impl;

import cn.com.edtechhub.workdatealive.manager.ai.AIManager;
import cn.com.edtechhub.workdatealive.service.AIService;
import org.springframework.ai.chat.client.ChatClient;

public class AIServiceImpl implements AIService {

    /**
     * 注入 AI 管理器依赖
     */
    AIManager aiManager;

}
