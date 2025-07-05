package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileOperationToolTest {

    @Test
    public void testReadFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "test.txt";
        String result = tool.readFile(fileName);
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    public void testWriteFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "test.txt";
        String content = "https://github.com/limou3434 是 limou 的 GitHub 账号";
        String result = tool.writeFile(fileName, content);
        assertNotNull(result);
        System.out.println(result);
    }

}
