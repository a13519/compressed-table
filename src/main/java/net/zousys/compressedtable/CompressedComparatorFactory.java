package net.zousys.compressedtable;

import lombok.Builder;
import lombok.Setter;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.multikeys.MultiKeysCompressedComparator;
import net.zousys.compressedtable.impl.singlekey.SingleKeyCompressedComparator;
import org.apache.commons.math3.analysis.function.Sin;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Builder
public class CompressedComparatorFactory {
    @Setter
    private ComparatorListener comparatorListener;
    @Setter
    private Set<String> ignoredFields;
    @Setter
    private CompressedTable before;
    @Setter
    private CompressedTable after;
    @Setter
    private List<String> unitedHeaders;
    @Setter
    private Map<String, Integer> unitedHeaderMapping;
    @Setter
    private boolean trim;
    @Setter
    @Builder.Default
    private CompressedTableFactory.Mode mode = CompressedTableFactory.Mode.SINGLE_KEY;
    /**
     *
     * @return
     */
    public CompressedComparator create() {
        if (mode == CompressedTableFactory.Mode.MULTI_KEYS) {
            return MultiKeysCompressedComparator.builder()
                    .comparatorListener(comparatorListener)
                    .ignoredFields(ignoredFields)
                    .before(before)
                    .after(after)
                    .unitedHeaders(unitedHeaders)
                    .unitedHeaderMapping(unitedHeaderMapping)
                    .trim(trim)
                    .build();
        } else if (mode == CompressedTableFactory.Mode.SINGLE_KEY) {
            return SingleKeyCompressedComparator.builder()
                    .comparatorListener(comparatorListener)
                    .ignoredFields(ignoredFields)
                    .before(before)
                    .after(after)
                    .unitedHeaders(unitedHeaders)
                    .unitedHeaderMapping(unitedHeaderMapping)
                    .trim(trim)
                    .build();
        }
        return null;
    }
}
