package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页抓取工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public class WebScrapingTool {

    /**
     * 抓取网页
     *
     * @param url 网页地址
     * @return 网页内容
     */
    @Tool(description = "Scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}

