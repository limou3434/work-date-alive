package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://tttrip.online.sh.cn/gb/content/2025-02/17/content_10284752.htm";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
        System.out.println("抓取结果为 " + result);
    }

}