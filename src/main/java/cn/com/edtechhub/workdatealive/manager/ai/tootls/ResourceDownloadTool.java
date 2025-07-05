package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 资源下载工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class ResourceDownloadTool {

    /**
     * 文件保存目录
     */
    private final String FILE_SAVE_DIR = System.getProperty("user.dir") + "/temp";

    /**
     * 下载资源
     *
     * @param url 资源地址
     * @param fileName 文件名
     * @return 下载结果
     */
    @Tool(description = "从给定的 URL 下载资源")
    public String downloadResource(@ToolParam(description = "要下载的资源的 URL") String url, @ToolParam(description = "保存下载资源的文件名") String fileName) {
        String fileDir = this.FILE_SAVE_DIR + "/download";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 使用 Hutool 的 downloadFile 方法下载资源
            HttpUtil.downloadFile(url, new File(filePath));
            return "资源下载成功 " + filePath;
        } catch (Exception e) {
            return "下载资源错误 " + e.getMessage();
        }
    }

}
