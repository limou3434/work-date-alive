package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.springframework.ai.tool.annotation.Tool;

/**
 * 终止工具
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public class TerminateTool {

    @Tool(description = """  
            Terminate the interaction when the request is met OR if the assistant cannot proceed further with the task.  
            "When you have finished all the tasks, call this tool to end the work.  
            """)
    public String doTerminate() {
        return "任务结束";
    }

}
