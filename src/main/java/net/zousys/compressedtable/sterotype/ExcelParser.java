package net.zousys.compressedtable.sterotype;

import lombok.Builder;
import net.zousys.compressedtable.impl.CompressedTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

@Builder
/**
 *
 */
public class ExcelParser {
    /**
     *
     */
    public ExcelParser() {
    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    public CompressedTable parse(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int from = sheet.getFirstRowNum();
            int to = sheet.getLastRowNum();
            CompressedTable compressedTable = new CompressedTable();

            for (int i = from; i <= to; i++) {
                org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                Iterator<Cell> cellIterator = row.cellIterator();
                ArrayList<String> arowarray = new ArrayList<>();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    arowarray.add(stringvalue(cell));
                }
                compressedTable.appendRow(arowarray);
            }
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
        if (type == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            }
            return "" + cell.getNumericCellValue();
        } else if (type == CellType.FORMULA) {
            return cell.getCellFormula();
        } else if (type == CellType.STRING) {
            return cell.getStringCellValue();
        } else {
            return cell.getStringCellValue();
        }
    }

}
