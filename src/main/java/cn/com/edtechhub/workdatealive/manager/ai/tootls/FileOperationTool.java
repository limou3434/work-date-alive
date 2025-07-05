package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.core.io.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 文件操作工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class FileOperationTool {

    /**
     * 文件保存目录
     */
    private final String fileSaveDir = System.getProperty("user.dir") + "/temp";

    /**
     * 文件保存路径
     */
    private final String fileDir = this.fileSaveDir + "/file";

    /**
     * 读取文件
     * @param fileName 文件名
     * @return 文件内容
     */
    @Tool(description = "从文件中读取内容")
    public String readFile(@ToolParam(description = "要读取的文件名") String fileName) {
        String filePath = fileDir + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "读取文件错误 " + e.getMessage();
        }
    }

    /**
     * 写入文件
     *
     * @param fileName 文件名
     * @param content 写入内容
     * @return 写入结果
     */
    @Tool(description = "将内容写入文件")
    public String writeFile(
            @ToolParam(description = "要写入的文件名") String fileName,
            @ToolParam(description = "要写入文件的内容") String content) {
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            FileUtil.writeUtf8String(content, filePath);
            return "写入成功的文件路径为 " + filePath;
        } catch (Exception e) {
            return "写入文件错误 " + e.getMessage();
        }
    }

}
