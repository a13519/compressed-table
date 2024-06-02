package net.zousys.compressedtable;

import lombok.Getter;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeaders;
import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.sterotype.CSVParser;
import net.zousys.compressedtable.sterotype.ExcelParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class CompressedTableFactory {
    public static enum Type {
        CSV, EXCEL
    }
    public static enum Mode {
        SINGLE_KEY, MULTI_KEYS
    }
    private int ignoredLines = 0;
    private KeyHeadersList keyHeaderList = new KeyHeadersList();
    private char delimeter = ',';
    @Getter
    private Type type = Type.CSV;

    /**
     * the header row number, if this is not explictly set then there will be no header at all
     * the first row of source file is starts from 0
     */
    private int headerPosition = -1;
    private boolean compressed = true;
    /**
     *
     * @param type
     */
    private CompressedTableFactory(Type type) {
        this.type = type;
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
     *
     * @param compressed
     * @return
     */
    public CompressedTableFactory compressed(boolean compressed) {
        this.compressed = compressed;
        return this;
    }

    /**
     * @param keyHeaderList
     * @return
     */
    public CompressedTableFactory keyHeaderList(KeyHeadersList keyHeaderList) {
        this.keyHeaderList = keyHeaderList;
        return this;
    }

    /**
     *
     * @param headers
     * @return
     */
    public CompressedTableFactory addKeyHeaders(KeyHeaders headers) {
        keyHeaderList.addHeaders(headers);
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
            throw new IOException("file is not exist");
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
            throw new IOException("file is not exist");
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
                        .headerPosition(headerPosition)
                        .keyHeaderList(keyHeaderList)
                        .compressed(compressed)
                        .build()
                        .parse(inputSteam);
            }
            case EXCEL: {
                return ExcelParser.builder()
                        .ignoredLines(ignoredLines)
                        .headerPosition(headerPosition)
                        .keyHeaderList(keyHeaderList)
                        .compressed(compressed)
                        .build()
                        .parse(inputSteam);
            }
        }
        return null;
    }


}
