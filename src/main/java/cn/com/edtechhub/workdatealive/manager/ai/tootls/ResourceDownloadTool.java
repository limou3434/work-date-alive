package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * 资源下载工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public class ResourceDownloadTool {

    /**
     * 文件保存目录
     */
    private final String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";

    /**
     * 下载资源
     *
     * @param url 资源地址
     * @param fileName 文件名
     * @return 下载结果
     */
    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
        String fileDir = this.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(filePath));
            return "Resource downloaded successfully to: " + filePath;
        } catch (Exception e) {
            return "Error downloading resource: " + e.getMessage();
        }
    }

}
