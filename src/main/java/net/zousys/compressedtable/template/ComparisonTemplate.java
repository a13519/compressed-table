package net.zousys.compressedtable.template;

import lombok.Data;
import lombok.Setter;
import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.impl.CompressedTable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.*;

/**
 *
 */
@Data
public class ComparisonTemplate {

    private OutputStream outputStream;

    private XSSFSheet mismatchshit;
    private XSSFSheet bmissedshit;
    private XSSFSheet amissedshit;
    private XSSFSheet matchedshit;
    private XSSFWorkbook book;

    private int ignrn = 0;
    private int rowid = 0;
    private int coln = 0;
    @Setter
    private int details = 2;

    private CompressedTable beforetable;
    private CompressedTable aftertable;
    @Setter
    private Set<String> beforemissed;
    @Setter
    private Set<String> aftermissed;
    @Setter
    private List<String> unitedHeaders;
    private List<String> matchedKeys = new ArrayList<>();
    private Map<String, Integer> markers = new HashMap<>();
    private Map<String, Integer> unitedHeaderMapping;
    private boolean headered = false;

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

    /**
     *
     * @param comparisonResult
     * @throws FileNotFoundException
     */
    public ComparisonTemplate(ComparisonResult comparisonResult) throws FileNotFoundException {
        this.comparisonResult = comparisonResult;
        populate();
    }

    /**
     *
     * @throws Exception
     */
    public void save() throws Exception {
        List<ComparisonResult.RowResult> rows = comparisonResult.getMismatches();
        for (ComparisonResult.RowResult rowResult : rows) {
            append(rowResult, comparisonResult);
        }
        book.write(outputStream);
        outputStream.close();
    }

    /**
     *
     */
    private void populate() {
        book = new XSSFWorkbook();
        mismatchshit = book.createSheet("Mismatched");
        amissedshit = book.createSheet("After Missed");
        bmissedshit = book.createSheet("Before Missed");
        if (details == 3) {
            matchedshit = book.createSheet("Matched");
        }
        Styles.init(book);
    }

    /**
     * @param rowResult
     * @param comparisonResult
     * @throws Exception
     */
    private void append(ComparisonResult.RowResult rowResult, ComparisonResult comparisonResult) throws Exception {
        XSSFRow row = mismatchshit.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue(rowResult.getMatchedKey().getValue());
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
        row = mismatchshit.createRow(rowid++);
        cellid = 0;
        x=0;
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


}

