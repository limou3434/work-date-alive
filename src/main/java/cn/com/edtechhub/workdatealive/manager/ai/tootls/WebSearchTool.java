package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网页搜索工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Data
public class WebSearchTool {

    // SearchAPI 的搜索接口地址(详细可以阅读 https://www.searchapi.io/bing)
    private static final String searchApiUrl = "https://www.searchapi.io/api/v1/search";

    /**
     * 搜索 API 密钥
     */
    @Value("${search-api.api-key}")
    private String apiKey;

    /**
     * 搜索网络
     *
     * @param query 搜索关键词
     * @return 搜索结果
     */
    @Tool(description = "从搜索引擎搜索信息")
    public String searchWeb(
            @ToolParam(description = "搜索查询关键字") String query
    ) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "bing");
        try {
//            String response = HttpUtil
//                    .get(searchApiUrl, paramMap)
//                    ;
            String response = HttpRequest.get(searchApiUrl)
                    .form(paramMap)
                    .timeout(30000)  // 设置超时为 30 秒
                    .execute()
                    .body();
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, 5);
            // 拼接搜索结果为字符串
            return objects
                    .stream()
                    .map(obj -> {
                        JSONObject tmpJSONObject = (JSONObject) obj;
                        return tmpJSONObject.toString();
                    })
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            return "搜索失败，出现错误 " + e.getMessage() + " 但是可以继续执行别的步骤";
        }
    }

}
