package net.zousys.compressedtable.template;

import lombok.Data;
import lombok.Setter;
import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.*;

import static net.zousys.compressedtable.template.Styles.*;

/**
 *
 */
@Data
public class ComparisonExcelTemplate {
    private OutputStream outputStream;

    private XSSFWorkbook book;

    private int ignrn = 0;
    private int rowid = 0;
    private int coln = 0;
    @Setter
    private int details = 2;

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
     * @param comparisonResult
     * @throws FileNotFoundException
     */
    public ComparisonExcelTemplate(ComparisonResult comparisonResult) throws Exception {
        this.comparisonResult = comparisonResult;
    }

    /**
     * @throws Exception
     */
    public void save() throws Exception {
        populate();
        book.write(outputStream);
        outputStream.close();
    }

    /**
     *
     */
    public void populate() throws Exception {
        book = new XSSFWorkbook();
        Styles.init(book);

        populateMisMatched(book.createSheet("Mismatched"));
        populateAfterMissed(book.createSheet("After Missed"));
        populateBeforeMissed(book.createSheet("Before Missed"));
        if (details == 3) {
//            matchedshit = book.createSheet("Matched");
        }

    }

    /**
     * @param xssfSheet
     * @throws Exception
     */
    private void populateAfterMissed(XSSFSheet xssfSheet) throws Exception {
        rowid = 0;
        // Headers
        XSSFRow row = xssfSheet.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue("KeyValue");
        keyCell.setCellStyle(styles.get(HEADERS));

        int x = 0;
        for (String header : comparisonResult.getBefore().getHeaders()) {
            Cell cell = row.createCell(cellid++);
            cell.setCellValue(header);
            cell.setCellStyle(styles.get(HEADERS));
        }

        // Markers
        row = xssfSheet.createRow(rowid++);
        cellid = 0;
        keyCell = row.createCell(cellid++);
        int cint = comparisonResult.getAfterMissed().size();
        if (cint == 0) {
            keyCell.setCellValue("");
            keyCell.setCellStyle(styles.get(HEADERS));
        } else {
            keyCell.setCellValue("" + cint);
            keyCell.setCellStyle(styles.get(MARKER));
        }

        Map<String, Row> trows = comparisonResult.getBefore().getKeyedMappingMap().getMainKeyedMapping();

        for (String akey : comparisonResult.getAfterMissed()) {
            Row arow = trows.get(akey);
            row = xssfSheet.createRow(rowid++);
            cellid = 0;
            Cell cell = row.createCell(cellid++);
            cell.setCellValue(akey);
            cell.setCellStyle(styles.get(BEFORE));
            for (String header : comparisonResult.getBefore().getHeaders()) {
                cell = row.createCell(cellid++);
                cell.setCellValue(arow.getField(header));
                cell.setCellStyle(styles.get(BEFORE));
            }
        }
        autoColumnWidth(xssfSheet);
    }

    /**
     * @param xssfSheet
     * @throws Exception
     */
    private void populateBeforeMissed(XSSFSheet xssfSheet) throws Exception {
        rowid = 0;
        // Headers
        XSSFRow row = xssfSheet.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue("KeyValue");
        keyCell.setCellStyle(styles.get(HEADERS));

        // Headers
        int x = 0;
        for (String header : comparisonResult.getAfter().getHeaders()) {
            Cell cell = row.createCell(cellid++);
            cell.setCellValue(header);
            cell.setCellStyle(styles.get(HEADERS));
        }

        // Markers
        row = xssfSheet.createRow(rowid++);
        cellid = 0;
        keyCell = row.createCell(cellid++);
        int cint = comparisonResult.getBeforeMissed().size();
        if (cint == 0) {
            keyCell.setCellValue("");
            keyCell.setCellStyle(styles.get(HEADERS));
        } else {
            keyCell.setCellValue("" + cint);
            keyCell.setCellStyle(styles.get(MARKER));
        }

        Map<String, Row> trows = comparisonResult.getAfter().getKeyedMappingMap().getMainKeyedMapping();

        for (String akey : comparisonResult.getBeforeMissed()) {
            Row arow = trows.get(akey);
            row = xssfSheet.createRow(rowid++);
            cellid = 0;
            Cell cell = row.createCell(cellid++);
            cell.setCellValue(akey);
            cell.setCellStyle(styles.get(AFTER));
            for (String header : comparisonResult.getAfter().getHeaders()) {
                cell = row.createCell(cellid++);
                cell.setCellValue(arow.getField(header));
                cell.setCellStyle(styles.get(AFTER));
            }
        }
        autoColumnWidth(xssfSheet);
    }


    /**
     * @param xssfSheet
     * @throws Exception
     */
    private void populateMisMatched(XSSFSheet xssfSheet) throws Exception {
        rowid = 0;
        populateHeaders(xssfSheet);
        List<ComparisonResult.RowResult> rows = comparisonResult.getMismatches();
        for (ComparisonResult.RowResult rowResult : rows) {
            appendMismatch(xssfSheet, rowResult, comparisonResult);
        }
        autoColumnWidth(xssfSheet);
    }

    /**
     * @param xssfSheet
     */
    private void populateHeaders(XSSFSheet xssfSheet) {
        // Headers
        XSSFRow row = xssfSheet.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue("KeyValue");
        keyCell.setCellStyle(styles.get(HEADERS));

        for (String header : comparisonResult.getUnitedHeaders()) {
            Cell cell = row.createCell(cellid++);
            Integer bheaderInd = comparisonResult.getBefore().getHeaderMapping().get(header);
            Integer aheaderInd = comparisonResult.getAfter().getHeaderMapping().get(header);
            if (comparisonResult.getIgnoredFields().contains(header)) {
                cell.setCellValue(header);
                cell.setCellStyle(styles.get(IGNORE));
            } else {
                if (bheaderInd == null) {
                    cell.setCellValue(header);
                    cell.setCellStyle(styles.get(HEADERSAONLY));
                } else if (aheaderInd == null) {
                    cell.setCellValue(header);
                    cell.setCellStyle(styles.get(HEADERSBONLY));
                } else {
                    cell.setCellValue(header);
                    cell.setCellStyle(styles.get(HEADERS));
                }
            }
        }

        // Marker
        row = xssfSheet.createRow(rowid++);
        cellid = 0;
        keyCell = row.createCell(cellid++);
        int tint = comparisonResult.getMismatches().size();
        if (tint == 0) {
            keyCell.setCellValue("");
//            keyCell.setCellStyle(styles.get(HEADERS));
        } else {
            keyCell.setCellValue("" + tint);
            keyCell.setCellStyle(styles.get(MARKER));
        }

        for (String header : comparisonResult.getUnitedHeaders()) {
            Cell cell = row.createCell(cellid++);
            Integer cint = comparisonResult.getMarkers().get(header);
            if (comparisonResult.getIgnoredFields().contains(header)) {
                cell.setCellValue("");
                cell.setCellStyle(styles.get(IGNORE));
            }
            if (cint == null) {
                cell.setCellValue("");
//                cell.setCellStyle(styles.get(HEADERS));
            } else {
                cell.setCellValue(cint.toString());
                cell.setCellStyle(styles.get(MARKER));
            }

        }
    }

    /**
     * @param rowResult
     * @param comparisonResult
     * @throws Exception
     */
    private void appendMismatch(XSSFSheet xssfSheet, ComparisonResult.RowResult rowResult, ComparisonResult comparisonResult) throws Exception {
        XSSFRow row = xssfSheet.createRow(rowid++);
        cellid = 0;
        Cell keyCell = row.createCell(cellid++);
        keyCell.setCellValue(rowResult.getMatchedKey().getValue());
        keyCell.setCellStyle(styles.get(BEFORE));

        Map<String, ComparisonResult.ResultField> fields = rowResult.getFields();
        for (String header : comparisonResult.getUnitedHeaders()) {
            Cell cell = row.createCell(cellid++);
            Integer bheaderInd = comparisonResult.getBefore().getHeaderMapping().get(header);
            Integer aheaderInd = comparisonResult.getAfter().getHeaderMapping().get(header);
            ComparisonResult.ResultField rf = fields.get(header);
            if (comparisonResult.getIgnoredFields().contains(header)) {
                cell.setCellValue(rf.getBeforeField());
                if (rf.isMissmatched()) {
                    cell.setCellStyle(styles.get(MISMATCHBEFORE));
                } else {
                    cell.setCellStyle(styles.get(IGNORE));
                }
            } else {
                if (bheaderInd == null) {
                    cell.setCellValue("<BLANK>");
                    cell.setCellStyle(styles.get(NULLBEFORE));
                } else if (aheaderInd == null) {
                    cell.setCellValue(rowResult.getFields().get(header).getBeforeField());
                    cell.setCellStyle(styles.get(BEFORE));
                } else {
                    cell.setCellValue(rf.getBeforeField());
                    if (fields.get(header).isMissmatched()) {
                        cell.setCellStyle(styles.get(MISMATCHBEFORE));
                    } else {
                        cell.setCellStyle(styles.get(BEFORE));
                    }
                }
            }
        }
        row = xssfSheet.createRow(rowid++);
        cellid = 0;
        keyCell = row.createCell(cellid++);
        keyCell.setCellValue("");

        for (String header : comparisonResult.getUnitedHeaders()) {
            Cell cell = row.createCell(cellid++);
            Integer bheaderInd = comparisonResult.getBefore().getHeaderMapping().get(header);
            Integer aheaderInd = comparisonResult.getAfter().getHeaderMapping().get(header);
            ComparisonResult.ResultField rf = fields.get(header);
            if (comparisonResult.getIgnoredFields().contains(header)) {
                cell.setCellValue(rf.getAfterField());
                if (rf.isMissmatched()) {
                    cell.setCellStyle(styles.get(MISMATCHAFTER));
                } else {
                    cell.setCellStyle(styles.get(IGNORE));
                }
            } else {
                if (aheaderInd == null) {
                    cell.setCellValue("<BLANK>");
                    cell.setCellStyle(styles.get(NULLAFTER));
                } else if (bheaderInd == null) {
                    ComparisonResult.ResultField s = rowResult.getFields().get(header);
                    cell.setCellValue(rowResult.getFields().get(header).getAfterField());
                    cell.setCellStyle(styles.get(AFTER));
                } else {
                    cell.setCellValue(rf.getAfterField());
                    if (fields.get(header).isMissmatched()) {
                        cell.setCellStyle(styles.get(MISMATCHAFTER));
                    }
                }
            }
        }
    }

    /**
     * @param xssfSheet
     * @throws Exception
     */
    private void autoColumnWidth(XSSFSheet xssfSheet) throws Exception {
        int maxColumns = xssfSheet.getRow(0).getLastCellNum();
        for (int i = 0; i < maxColumns; i++) {
            xssfSheet.autoSizeColumn(i);
        }
        xssfSheet.setAutoFilter(new CellRangeAddress(0, 0, 0, maxColumns == 0 ? 0 : maxColumns - 1));
    }
}

