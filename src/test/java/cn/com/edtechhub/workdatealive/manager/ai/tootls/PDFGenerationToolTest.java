package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "test.pdf";
        String content = "limou3434 的个人主页是 https://github.com/limou3434";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
        System.out.println(result);
    }

}
