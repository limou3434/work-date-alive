package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 网页抓取工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class WebScrapingTool {

    /**
     * 抓取网页
     *
     * @param url 网页地址
     * @return 网页内容
     */
    @Tool(description = "抓取网页的内容")
    public String scrapeWebPage(@ToolParam(description = "要抓取的网页的 URL") String url) {
        try {
            Document doc = Jsoup
                    .connect(url)
                    .get();
            return doc.html();
        } catch (IOException e) {
            return "抓取网页遇到错误, 可能是该网站有反爬取的机制, 最好换一个地址 " + e.getMessage();
        }
    }

}

