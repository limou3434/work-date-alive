package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.core.io.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public class FileOperationTool {

    /**
     * 文件保存目录
     */
    private final String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";

    /**
     * 文件保存路径
     */
    private final String FILE_DIR = this.FILE_SAVE_DIR + "/file";

    /**
     * 读取文件
     * @param fileName 文件名
     * @return 文件内容
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    /**
     * 写入文件
     *
     * @param fileName 文件名
     * @param content 写入内容
     * @return 写入结果
     */
    @Tool(description = "Write content to a file")
    public String writeFile(
            @ToolParam(description = "Name of the file to write") String fileName,
            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }

}
