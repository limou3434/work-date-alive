package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TerminateToolTest {

    @Test
    public void testDoTerminate() {
        TerminateTool tool = new TerminateTool();
        String result = tool.doTerminate();
        assertNotNull(result);
        System.out.println(result);
    }

}