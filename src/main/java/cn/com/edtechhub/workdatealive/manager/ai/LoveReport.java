package cn.com.edtechhub.workdatealive.manager.ai;

import java.util.List;

/**
 * 爱情报告数据类
 *
 * @param title 标题
 * @param suggestions 建议
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
public record LoveReport(String title, List<String> suggestions) { }
