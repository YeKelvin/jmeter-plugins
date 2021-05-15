package org.apache.jmeter.common.office;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author KelvinYe
 */
public class ExcelUtil {
    public static final String XLS = "xls";
    public static final String XLSX = "xlsx";

    /**
     * 创建Excel文件
     *
     * @param filepath  filepath 文件全路径
     * @param sheetName 新Sheet页的名字
     * @param titles    表头
     * @param values    每行的单元格
     */
    public static boolean write(String filepath,
                                String sheetName,
                                List<String> titles,
                                List<Map<String, Object>> values) throws IOException {
        boolean success;

        if (StringUtils.isBlank(filepath)) {
            throw new IllegalArgumentException("文件路径不能为空");
        }

        String suffiex = getSuffiex(filepath);
        if (StringUtils.isBlank(suffiex)) {
            throw new IllegalArgumentException("文件后缀不能为空");
        }

        Workbook workbook;
        if ("xls".equals(suffiex.toLowerCase())) {
            workbook = new HSSFWorkbook();
        } else {
            workbook = new XSSFWorkbook();
        }

        // 生成一个表格
        Sheet sheet;
        if (StringUtils.isBlank(sheetName)) {
            // name 为空则使用默认值
            sheet = workbook.createSheet();
        } else {
            sheet = workbook.createSheet(sheetName);
        }

        // 设置表格默认列宽度为15个字节
        sheet.setDefaultColumnWidth((short) 15);
        // 生成样式
        Map<String, CellStyle> styles = createStyles(workbook);
        // 创建标题行
        Row row = sheet.createRow(0);
        // 存储标题在Excel文件中的序号
        Map<String, Integer> titleOrder = new HashMap();
        for (int i = 0; i < titles.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String title = titles.get(i);
            cell.setCellValue(title);
            titleOrder.put(title, i);
        }
        // 写入正文
        Iterator<Map<String, Object>> iterator = values.iterator();
        // 行号
        int index = 1;
        while (iterator.hasNext()) {
            row = sheet.createRow(index);
            Map<String, Object> value = iterator.next();
            for (Map.Entry<String, Object> map : value.entrySet()) {
                // 获取列名
                String title = map.getKey();
                // 根据列名获取序号
                int i = titleOrder.get(title);
                // 在指定序号处创建cell
                Cell cell = row.createCell(i);
                // 设置cell的样式
                cell.setCellStyle(styles.get("cell"));
                // 获取列的值
                Object object = map.getValue();
                // 判断object的类型
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (object instanceof Double) {
                    cell.setCellValue((Double) object);
                } else if (object instanceof Date) {
                    String time = simpleDateFormat.format((Date) object);
                    cell.setCellValue(time);
                } else if (object instanceof Calendar) {
                    Calendar calendar = (Calendar) object;
                    String time = simpleDateFormat.format(calendar.getTime());
                    cell.setCellValue(time);
                } else if (object instanceof Boolean) {
                    cell.setCellValue((Boolean) object);
                } else {
                    if (object != null) {
                        cell.setCellValue(object.toString());
                    }
                }
            }
            index++;
        }

        // 输出excel文件
        try (OutputStream outputStream = new FileOutputStream(filepath)) {
            workbook.write(outputStream);
            success = true;
        } finally {
            workbook.close();
        }
        return success;
    }

    /**
     * 获取后缀
     *
     * @param filepath filepath 文件全路径
     */
    private static String getSuffiex(String filepath) {
        if (StringUtils.isBlank(filepath)) {
            return "";
        }
        int index = filepath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filepath.substring(index + 1);
    }

    /**
     * 设置格式
     */
    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap();

        Font fontSize16 = wb.createFont();
        fontSize16.setFontHeightInPoints((short) 16);
        fontSize16.setBold(true);
        fontSize16.setFontName("微软雅黑");

        Font fontSize12 = wb.createFont();
        fontSize12.setFontHeightInPoints((short) 12);
        fontSize12.setFontName("微软雅黑");

        // 标题样式
        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER); // 水平对齐
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐
        titleStyle.setLocked(true); // 样式锁定
        titleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        titleStyle.setFont(fontSize16);
        styles.put("title", titleStyle);

        // 文件头样式
        XSSFCellStyle headerStyle = (XSSFCellStyle) wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);
        headerStyle.setBorderRight(BorderStyle.THIN); // 设置边界
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setFont(fontSize12);
        styles.put("header", headerStyle);

        XSSFCellStyle cellStyleA = (XSSFCellStyle) wb.createCellStyle();
        cellStyleA.setAlignment(HorizontalAlignment.CENTER); // 居中设置
        cellStyleA.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleA.setWrapText(true);
        cellStyleA.setBorderRight(BorderStyle.THIN);
        cellStyleA.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setBorderLeft(BorderStyle.THIN);
        cellStyleA.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setBorderTop(BorderStyle.THIN);
        cellStyleA.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setBorderBottom(BorderStyle.THIN);
        cellStyleA.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyleA.setFont(fontSize12);
        styles.put("cell", cellStyleA);

        return styles;
    }

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\Kaiwen.Ye\\Desktop\\test.xlsx";
        List<String> titles = new ArrayList<>();
        titles.add("testTitle");
        List<Map<String, Object>> values = new ArrayList<>();
        Map<String, Object> value = new HashMap<>();
        value.put("testTitle", "vvvvv");
        values.add(value);

        boolean isSuccess = ExcelUtil.write(filePath, "test", titles, values);
        System.out.println(isSuccess);
    }
}
