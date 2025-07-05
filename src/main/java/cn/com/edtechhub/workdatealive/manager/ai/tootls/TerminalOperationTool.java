package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class TerminalOperationTool {

    /**
     * 执行终端命令
     * @param command 命令
     * @return 命令执行结果
     */
    @Tool(description = "在终端中执行命令")
    public String executeTerminalCommand(@ToolParam(description = "命令在终端上执行") String command) {
        StringBuilder output = new StringBuilder();
        try {
            // TODO: 修改为 Windows 和 Linux 兼容通用的代码
//            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
//            Process process = builder.start();
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("命令执行失败, 存在退出码").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("执行命令错误").append(e.getMessage());
        }
        return output.toString();
    }

}

