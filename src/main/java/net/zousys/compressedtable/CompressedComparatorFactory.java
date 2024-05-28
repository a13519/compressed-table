package net.zousys.compressedtable;

import net.zousys.compressedtable.impl.multikeys.MultiKeysCompressedComparator;
import net.zousys.compressedtable.impl.singlekey.SingleKeyCompressedComparator;
import org.apache.commons.math3.analysis.function.Sin;

public class CompressedComparatorFactory {
    /**
     *
     * @param mode
     * @return
     */
    public static CompressedComparator crate(CompressedTableFactory.Mode mode) {
        if (mode == CompressedTableFactory.Mode.MULTI_KEYS) {
            return MultiKeysCompressedComparator.builder().build();
        } else if (mode == CompressedTableFactory.Mode.SINGLE_KEY) {
            return SingleKeyCompressedComparator.builder().build();
        }
        return null;
    }
}
