package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * 终止工具
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class TerminateTool {

    @Tool(description = """  
            当请求得到满足或助理因为出错导致无法继续执行任务时，终止交互。
            当你完成了所有的任务，调用这个工具来结束工作。
            """)
    public String doTerminate() {
        return "任务结束";
    }

}
