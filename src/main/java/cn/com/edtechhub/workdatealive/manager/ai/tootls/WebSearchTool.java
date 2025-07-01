package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网络搜索工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public class WebSearchTool {

    // SearchAPI 的搜索接口地址(详细可以阅读 https://www.searchapi.io/baidu)
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    /**
     * API 密钥
     */
    private final String apiKey; // TODO: 这个 API 需要从 SearchAPI 网站上获取

    /**
     * 构造方法
     *
     * @param apiKey API 密钥
     */
    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * 搜索网络
     *
     * @param query 搜索关键词
     * @return 搜索结果
     */
    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query
    ) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "bing");

        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, 5);
            // 拼接搜索结果为字符串
            return objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }

}
