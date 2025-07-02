package cn.com.edtechhub.workdatealive.manager.ai;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Configuration
@ConfigurationProperties(prefix = "spring.ai.dashscope")
@Data
@Slf4j
public class AiConfig {

    /**
     * 密钥
     */
    private String apiKey;

    /**
     * 系统提示词
     */
    private String systemPrompt =
            "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。"
    ;

    /**
     * 对话长度
     */
    private Integer chatMemoryRetrieveSize = 10;

    /**
     * 保存数据持久化文件的目录
     */
    private String dataSaveFileDir = "./log/message/";

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[{}] {}", this.getClass().getSimpleName(), this);
    }

}