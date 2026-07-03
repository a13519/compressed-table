package net.zousys.compressedtable.template;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

public class Styles {
    public static XSSFCellStyle redStyle;
    public static XSSFCellStyle red2Style;
    public static XSSFCellStyle lightStyle;
    public static XSSFCellStyle headerStyle;
    public static XSSFCellStyle headerStyle2;
    public static XSSFCellStyle grayStyle;
    public static Map<String, XSSFCellStyle> styles = new HashMap<>();

    public static void init(XSSFWorkbook book) {
        headerStyle = book.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle2 = book.createCellStyle();
        headerStyle2.setFillForegroundColor(IndexedColors.RED.getIndex());
        headerStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        redStyle = book.createCellStyle();
        redStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        red2Style = book.createCellStyle();
        red2Style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        red2Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        lightStyle = book.createCellStyle();
        lightStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        lightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        grayStyle = book.createCellStyle();
        grayStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        grayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styles.put("before", headerStyle);
        styles.put("nullbefore", headerStyle2);
        styles.put("missmatchbefore", redStyle);
        styles.put("nullafter", headerStyle2);
        styles.put("missmatchafter", red2Style);
    }
}
