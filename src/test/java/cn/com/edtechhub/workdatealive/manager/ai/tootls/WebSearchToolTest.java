package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WebSearchToolTest {

    @Test
    public void testSearchWeb() {
        WebSearchTool tool = new WebSearchTool();
        String apiKey = System.getenv("WDA_SEARCH_API_KEY");
        tool.setApiKey(apiKey);
        String query = "程序员 limou3434";
        String result = tool.searchWeb(query);
        assertNotNull(result);
        System.out.println(result);
    }

}
