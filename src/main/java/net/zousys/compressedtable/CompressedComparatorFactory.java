package net.zousys.compressedtable;

import lombok.Builder;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.multikeys.MultiKeysCompressedComparator;
import net.zousys.compressedtable.impl.singlekey.SingleKeyCompressedComparator;
import org.apache.commons.math3.analysis.function.Sin;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
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
    /**
     *
     * @return
     */
    public CompressedComparator create() {
        if (before.getMode() == CompressedTableFactory.Mode.MULTI_KEYS &&
                after.getMode() == CompressedTableFactory.Mode.MULTI_KEYS) {
            return MultiKeysCompressedComparator.builder()
                    .comparatorListener(comparatorListener)
                    .ignoredFields(ignoredFields)
                    .before(before)
                    .after(after)
                    .unitedHeaders(unitedHeaders)
                    .unitedHeaderMapping(unitedHeaderMapping)
                    .trim(trim)
                    .build();
        } else if (before.getMode() == CompressedTableFactory.Mode.SINGLE_KEY &&
                after.getMode() == CompressedTableFactory.Mode.SINGLE_KEY) {
            return SingleKeyCompressedComparator.builder()
                    .comparatorListener(comparatorListener)
                    .ignoredFields(ignoredFields)
                    .before(before)
                    .after(after)
                    .unitedHeaders(unitedHeaders)
                    .unitedHeaderMapping(unitedHeaderMapping)
                    .trim(trim)
                    .build();
        } else {
            log.error("the before and after table has different mode");
        }
        return null;
    }
}
