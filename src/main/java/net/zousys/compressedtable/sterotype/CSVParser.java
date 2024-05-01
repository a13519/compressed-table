package net.zousys.compressedtable.sterotype;

import lombok.Builder;
import net.zousys.compressedtable.impl.CompressedTable;
import org.apache.commons.csv.CSVFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.io.*;

@Builder
/**
 *
 */
public class CSVParser {
    @Builder.Default
    private int ignoredLines = 0;
    @Builder.Default
    private String[] keyHeaders = new String[]{};
    @Builder.Default
    private char delimeter = ',';
    private int headerPosiction;
    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    public CompressedTable parse(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Reader in = new BufferedReader(new InputStreamReader(inputStream));
            CSVFormat format = CSVFormat.RFC4180.builder()
                    .setDelimiter(delimeter)
                    .setIgnoreEmptyLines(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setTrim(true)
                    .build();
            CompressedTable compressedTable = new CompressedTable();
            compressedTable.setHeaderRowNumber(headerPosiction);
            if (keyHeaders != null) {
                compressedTable.setKeyHeaders(keyHeaders);
            }
            format.parse(in).stream().skip(ignoredLines).forEach(re -> {
                try {
                    compressedTable.appendRow(re.values(), true);
                } catch (IOException e) {
                    //
                }
            });

            return compressedTable;
        }
        return null;
    }

    /**
     * @param cell
     * @return
     */
    private String stringvalue(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case NUMERIC: {
                return "" + cell.getNumericCellValue();
            }
            case FORMULA: {
                return cell.getCellFormula();
            }
            default: {
                return cell.getStringCellValue();
            }
        }
    }
}
