package cn.com.edtechhub.workdatealive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class WorkDateAliveApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkDateAliveApplication.class, args);
        String baseUrl = "http://127.0.0.1:8000";
        log.debug("访问 {} 即可得到在线文档, 访问 {} 即可得到文档配置", baseUrl + "/doc.html", baseUrl + "/v3/api-docs");
        log.debug("Spring Boot running...");
    }

}
