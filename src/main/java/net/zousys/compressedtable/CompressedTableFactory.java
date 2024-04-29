package net.zousys.compressedtable;

import lombok.Getter;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.sterotype.CSVParser;
import net.zousys.compressedtable.sterotype.ExcelParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompressedTableFactory {
    public static enum Type {
        CSV, EXCEL
    }

    private int ignoredLines = 0;
    private String[] keyHeaders = new String[]{};
    private char delimeter = ',';
    @Getter
    private Type type;
    /**
     * This is for Excel Table, to constrain a dynamic row cell number or has fixed columns as headers
     */
    private boolean dynamicWidth = true;
    /**
     * the header row number
     */
    private int headerPosition = 0;

    /**
     *
     * @param type
     */
    private CompressedTableFactory(Type type) {
        this.type = type;
    }

    /**
     *
     * @param dynamicWidth
     * @return
     */
    public CompressedTableFactory dynamicWidth(boolean dynamicWidth) {
        this.dynamicWidth = dynamicWidth;
        return this;
    }

    /**
     *
     * @param headerPosition
     * @return
     */
    public CompressedTableFactory headerPosition(int headerPosition) {
        this.headerPosition = headerPosition;
        return this;
    }

    /**
     * @param delimeter
     * @return
     */
    public CompressedTableFactory delimeter(char delimeter) {
        this.delimeter = delimeter;
        return this;
    }

    /**
     * @param ignoredLines
     * @return
     */
    public CompressedTableFactory ignoredLines(int ignoredLines) {
        this.ignoredLines = ignoredLines;
        return this;
    }

    /**
     * @param keyHeaders
     * @return
     */
    public CompressedTableFactory keyHeaders(String[] keyHeaders) {
        this.keyHeaders = keyHeaders;
        return this;
    }

    /**
     * @param type
     * @return
     */
    public static CompressedTableFactory build(String type) {
        if (type == null) {
            return null;
        }
        switch (type.toLowerCase()) {
            case "csv": {
                return new CompressedTableFactory(Type.CSV);
            }
            case "excel": {
                return new CompressedTableFactory(Type.EXCEL);
            }
        }

        return null;
    }

    /**
     * @param filename
     * @return
     * @throws IOException
     */
    public CompressedTable parse(String filename) throws IOException {
        if (filename != null) {
            return parse(new File(filename));
        } else {
            return null;
        }
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    public CompressedTable parse(File file) throws IOException {
        if (file != null && file.exists()) {
            return parse(new FileInputStream(file));
        } else {
            return null;
        }
    }

    /**
     * @param inputSteam
     * @return
     */
    public CompressedTable parse(InputStream inputSteam) throws IOException {
        switch (this.type) {
            case CSV: {
                return CSVParser.builder()
                        .delimeter(delimeter)
                        .ignoredLines(ignoredLines)
                        .keyHeaders(keyHeaders).build()
                        .parse(inputSteam);
            }
            case EXCEL: {
                return ExcelParser.builder()
                        .dynamicWidth(dynamicWidth)
                        .headerPosiction(headerPosition)
                        .build().parse(inputSteam);
            }
        }
        return null;
    }


}
