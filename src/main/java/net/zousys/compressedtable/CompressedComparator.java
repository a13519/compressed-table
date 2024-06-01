package net.zousys.compressedtable;

import java.util.List;
import java.util.Set;


public interface CompressedComparator {

    /**
     * This is to count mismatched column time, later after the result spread sheet / csv generated, the header will mark the times of discrenpancies
     *
     * @param mismatch
     */
    void addMarker(ComparisonResult.RowResult mismatch);

    /**
     * This is to compare two tables
     */
    public CompressedComparator compare();

    /**
     * Union of before and after table
     */
    public void uniteHeaders();


}
