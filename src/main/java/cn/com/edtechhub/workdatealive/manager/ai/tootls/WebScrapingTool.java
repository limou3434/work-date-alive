package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
                    .timeout(30000) // 设置 30 秒超时
                    .get();
            return doc.html();
        } catch (SocketTimeoutException e) {
            return "请求超时, 服务器响应太慢或无响应, 没有在 30 秒内返回结果, 可以尝试重新调用";
        } catch (UnknownHostException e) {
            return "解析域名失败, 请检查网址是否正确";
        } catch (SSLException e) {
            return "SSL 握手失败, HTTPS 证书异常或网站不安全";
        } catch (IOException e) {
            return "抓取网页遇到错误, 可能是该网站有反爬取的机制, 最好换一个地址 " + e.getMessage();
        }
    }

}

