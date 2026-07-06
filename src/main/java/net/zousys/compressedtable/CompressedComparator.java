package net.zousys.compressedtable;

public interface CompressedComparator {

    /**
     * This is to return the comparison result
     * @return
     */
    public ComparisonResult getComparisonResult();

    /**
     * This is to count mismatched column time, later after the result spread sheet / csv generated, the header will mark the times of discrenpancies
     *
     * @param mismatch
     */
    public void addMarker(ComparisonResult.RowResult mismatch);

    /**
     * This is to compare two tables
     */
    public CompressedComparator compare();

    /**
     * Union of before and after table
     */
    public void uniteHeaders();


}
