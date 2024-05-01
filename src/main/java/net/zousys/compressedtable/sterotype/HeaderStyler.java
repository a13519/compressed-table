package net.zousys.compressedtable.sterotype;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 */
public interface HeaderStyler {
    public CellStyle createStyler(Workbook book);
}
