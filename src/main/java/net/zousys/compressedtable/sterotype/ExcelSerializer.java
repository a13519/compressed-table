package net.zousys.compressedtable.sterotype;

import lombok.Setter;
import net.zousys.compressedtable.GeneralTable;
import net.zousys.compressedtable.Row;
import net.zousys.compressedtable.TableSerialier;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.DataFormatException;

/**
 *
 */
public class ExcelSerializer implements TableSerialier {
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<GeneralTable> tables = new ArrayList<>();
    private XSSFWorkbook workbook;
    @Setter
    private HeaderStyler headerStyle;
    private CellStyle cellStyle;
    /**
     *
     */
    public ExcelSerializer() {
        workbook = new XSSFWorkbook();
    }

    /**
     *
     * @param name
     * @param table
     */
    public void add(String name, GeneralTable table) {
        names.add(name);
        tables.add(table);
    }

    /**
     *
     * @return
     */
    private void serialize(String name, GeneralTable table) throws DataFormatException, IOException {
        XSSFSheet sheet = workbook.createSheet(name);
        List<Row> rows = table.getContents();

        for (int no = 0 ; no < rows.size() ; no ++) {
            writeRow(sheet, no, rows.get(no), table.getHeaderRowNumber());
        }

    }

    /**
     *
     * @param sheet
     * @param no
     * @param row
     * @throws DataFormatException
     * @throws IOException
     */
    private void writeRow(Sheet sheet, int no, Row row, int headerRowNumber) throws DataFormatException, IOException {
        org.apache.poi.ss.usermodel.Row arow = sheet.createRow(no);
        List<String> cells = row.getContent().form();
        for (int i = 0 ; i < cells.size() ; i ++) {
            Cell cell = arow.createCell(i);
            cell.setCellValue(cells.get(i));
            if (no== headerRowNumber && cellStyle!=null) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    /**
     *
     * @param outputStream
     */
    @Override
    public void serialize(OutputStream outputStream) throws DataFormatException, IOException{
        if (headerStyle!=null){
            cellStyle = headerStyle.createStyler(workbook);
        }
        for (int i = 0 ; i < names.size() ; i ++ ){
            serialize(names.get(i), tables.get(i));
        }
        workbook.write(outputStream);
        outputStream.flush();
    }
}
