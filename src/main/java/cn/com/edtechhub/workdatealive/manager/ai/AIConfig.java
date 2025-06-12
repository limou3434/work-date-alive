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
public class AIConfig {

    /**
     * 密钥
     */
    private String apiKey;

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[AIConfig] 当前项目 AI 密钥已加载为 {}", apiKey);
    }

}