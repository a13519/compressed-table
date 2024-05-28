package net.zousys.compressedtable.impl.singlekey;

import lombok.Builder;
import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.CompressedComparator;

@Builder
public class SingleKeyCompressedComparator implements net.zousys.compressedtable.CompressedComparator {
    @Override
    public void addMarker(ComparisonResult.RowResult mismatch) {

    }

    @Override
    public CompressedComparator compare() {
        return null;
    }

    @Override
    public void uniteHeaders() {

    }
}
