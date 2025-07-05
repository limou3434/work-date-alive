package cn.com.edtechhub.workdatealive.manager.ai.tootls;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PDF 生成工具类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
public class PDFGenerationTool {

    /**
     * 文件保存目录
     */
    private final String fileSaveDir = System.getProperty("user.dir") + "/temp"; // 很多工具类都用这个目录, 建议加入 KFC 豪华套餐...

    /**
     * 生成 PDF
     *
     * @param fileName 文件名
     * @param content  写入的内容
     * @return 生成结果
     */
    @Tool(description = "生成具有给定内容的 PDF 文件")
    public String generatePDF(
            @ToolParam(description = "保存生成的 PDF 文件的文件名") String fileName,
            @ToolParam(description = "要包含在 PDF 中的内容") String content) {
        String fileDir = this.fileSaveDir + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 自定义字体（需要人工下载字体文件到特定目录）
//                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
//                        .toAbsolutePath().toString();
//                PdfFont font = PdfFontFactory.createFont(fontPath,
//                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                // 使用内置中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF 已成功生成 " + filePath;
        } catch (IOException e) {
            return "生成 PDF 时出错 " + e.getMessage();
        }
    }

}
