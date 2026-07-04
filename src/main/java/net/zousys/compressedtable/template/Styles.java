package net.zousys.compressedtable.template;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Styles {
    public static final String HEADERS = "HEADERS";
    public static final String HEADERSAONLY = "HEADERS2";
    public static final String HEADERSBONLY = "HEADERS3";
    public static final String NULLBEFORE = "NULLBEFORE";
    public static final String NULLAFTER = "NULLAFTER";
    public static final String BLANKBEFORE = "BLANKBEFORE";
    public static final String BLANKAFTER = "BLANKAFTER";
    public static final String BEFORE = "BEFORE";
    public static final String AFTER = "AFTER";
    public static final String MISMATCHBEFORE = "MISMATCHBEFORE";
    public static final String MISMATCHAFTER = "MISMATCHAFTER";
    public static final String MARKER = "MARKER";

    public static XSSFCellStyle bluegrayStyle;
    public static XSSFCellStyle lightOrangeStyle;
    public static XSSFCellStyle yellowStyle;
    public static XSSFCellStyle yellowlightStyle;
    public static XSSFCellStyle yellowtouquiseStyle;
    public static XSSFCellStyle gray25Style;
    public static XSSFCellStyle gray40Style;
    public static XSSFCellStyle gray50Style;
    public static XSSFCellStyle blueStyle;
    public static XSSFCellStyle darkblueStyle;
    public static XSSFCellStyle nullOrangeStyle;
    public static XSSFCellStyle nullBlueStyle;
    public static XSSFCellStyle markerRedStyle;

    public static Map<String, XSSFCellStyle> styles = new HashMap<>();

    public static void init(XSSFWorkbook book) {

        Color pinkColor = new Color(255,182,193);
        XSSFColor customColor = new XSSFColor(pinkColor, new DefaultIndexedColorMap());
        Color skyblueColor = new Color(135,206,235);
        XSSFColor headerColor = new XSSFColor(skyblueColor, new DefaultIndexedColorMap());

        bluegrayStyle = book.createCellStyle();
        bluegrayStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        bluegrayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        lightOrangeStyle = book.createCellStyle();
        lightOrangeStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        lightOrangeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        yellowStyle = book.createCellStyle();
        yellowStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        yellowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        yellowlightStyle = book.createCellStyle();
        yellowlightStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        yellowlightStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        yellowtouquiseStyle = book.createCellStyle();
        yellowtouquiseStyle.setFillForegroundColor(headerColor);
        yellowtouquiseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        gray25Style = book.createCellStyle();
        gray25Style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        gray25Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        gray40Style = book.createCellStyle();
        gray40Style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        gray40Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        gray50Style = book.createCellStyle();
        gray50Style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        gray50Style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        blueStyle = book.createCellStyle();
        blueStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        darkblueStyle = book.createCellStyle();
        darkblueStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        darkblueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = book.createFont();
        font.setColor(IndexedColors.LIGHT_ORANGE.getIndex());
        nullOrangeStyle = book.createCellStyle();
        nullOrangeStyle.setFont(font);

        XSSFFont font2 = book.createFont();
        font2.setColor(IndexedColors.LIGHT_BLUE.getIndex());
        nullBlueStyle = book.createCellStyle();
        nullBlueStyle.setFont(font2);

        XSSFFont fontmarker = book.createFont();
        fontmarker.setColor(IndexedColors.DARK_RED.getIndex());
        fontmarker.setBold(true);
        markerRedStyle = book.createCellStyle();
        markerRedStyle.setFont(fontmarker);
        markerRedStyle.setFillForegroundColor(customColor);
        markerRedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styles.put(HEADERS, yellowtouquiseStyle);
        styles.put(HEADERSAONLY, lightOrangeStyle);
        styles.put(HEADERSBONLY, blueStyle);

        styles.put(BEFORE, gray25Style);

        styles.put(NULLAFTER, nullBlueStyle);
        styles.put(NULLBEFORE, nullOrangeStyle);
        styles.put(MARKER, markerRedStyle);
        styles.put(MISMATCHBEFORE, yellowStyle);
        styles.put(MISMATCHAFTER, yellowlightStyle);
    }
}
