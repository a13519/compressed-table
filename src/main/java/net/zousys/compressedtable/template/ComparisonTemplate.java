package net.zousys.compressedtable.template;

import lombok.Data;
import net.zousys.compressedtable.ComparisonResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Data
public class ComparisonTemplate {
    private String resultfile;
    private XSSFSheet spreadsheet;
    private XSSFWorkbook book;
    private int ignrn = 0;
    private int rowid = 0;
    private int coln = 0;
    private ComparisonResult comparisonResult;
    /**
     * nullafter
     * nullbefore
     * before
     * missmatchbefore
     * missmatchafter
     * header
     */
    private Map<String, XSSFCellStyle> styles;
    private static int cellid = 0;

    public ComparisonTemplate(ComparisonResult comparisonResult, String sheet, String resultfile) {
        this.comparisonResult = comparisonResult;
        this.resultfile = resultfile;
        book = new XSSFWorkbook();
        spreadsheet = book.createSheet(sheet == null ? "Data" : sheet);
        DataFormat format = book.createDataFormat();
        populate();
    }

    public void save() throws Exception {
        List<ComparisonResult.RowResult> rows = comparisonResult.getMismatches();
        for (ComparisonResult.RowResult rowResult : rows) {
            append(rowResult, comparisonResult);
        }
        FileOutputStream out = new FileOutputStream(new File(resultfile));
        book.write(out);
        out.close();
    }

    private void populate() {
        XSSFRow row = spreadsheet.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue("KeySet");
        comparisonResult.getUnitedHeaders().stream().forEach(a -> {
            Cell cell = row.createCell(cellid++);
            cell.setCellValue(a);
            cell.setCellStyle(styles.get("header"));
        });
    }

    /**
     *
     * @param rowResult
     * @param comparisonResult
     * @throws Exception
     */
    private void append(ComparisonResult.RowResult rowResult, ComparisonResult comparisonResult) throws Exception {
        XSSFRow row = spreadsheet.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue(rowResult.getStringkey().getMainKey());
        List<ComparisonResult.ResultField> fields = rowResult.getFields();
        int x = 0;
        for (String header : comparisonResult.getUnitedHeaders()) {
            Cell cell = row.createCell(cellid++);
            Integer bheaderInd = comparisonResult.getBefore().getHeaderMapping().get(header);
            if (bheaderInd == null) {
                cell.setCellValue("");
                cell.setCellStyle(styles.get("nullbefore"));
            } else {
                ComparisonResult.ResultField rf = fields.get(x++);
                cell.setCellValue(rf.getBeforeField());
                if (fields.get(bheaderInd).isMissmatched()) {
                    cell.setCellStyle(styles.get("missmatchbefore"));
                } else {
                    cell.setCellStyle(styles.get("before"));
                }
            }
        }
        row = spreadsheet.createRow(rowid++);
        cellid = 0;
        keyCell = row.createCell(cellid++);
        keyCell.setCellValue("");

        for (String header : comparisonResult.getUnitedHeaders()) {
            Cell cell = row.createCell(cellid++);
            Integer aheaderInd = comparisonResult.getAfter().getHeaderMapping().get(header);
            if (aheaderInd == null) {
                cell.setCellValue("");
                cell.setCellStyle(styles.get("nullafter"));
            } else {
                ComparisonResult.ResultField rf = fields.get(x++);
                cell.setCellValue(rf.getAfterField());
                if (fields.get(aheaderInd).isMissmatched()) {
                    cell.setCellStyle(styles.get("missmatchafter"));
                }
            }
        }
    }

    public void removeRows(int a) {
        if (a > 0) {
            spreadsheet.removeRow(spreadsheet.getRow(a));
        } else {
            int b = spreadsheet.getLastRowNum() + a + 1;
            spreadsheet.removeRow(spreadsheet.getRow(b));
        }
    }
}

