package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ResourceDownloadToolTest {

    @Test
    public void testDownloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://avatars.githubusercontent.com/u/113878415?s=400&u=9f10b63e033c9504615bc475581441478424e04b&v=4";
        String fileName = "self.png";
        String result = tool.downloadResource(url, fileName);
        assertNotNull(result);
        System.out.println(result);
    }

}