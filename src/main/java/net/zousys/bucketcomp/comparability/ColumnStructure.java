package net.zousys.bucketcomp.comparability;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Log4j2
@Getter
public class ColumnStructure {
    private Source beforeSource;
    private Source afterSource;
    private List<String> beforeMissedColumns = new ArrayList<>();
    private List<String> afterMissedColumns = new ArrayList<>();
    private List<String> commonColumns = new ArrayList<>();
    private List<String> commonComparableColumns = new ArrayList<>();
    private int commonComparableColumnNumber;
    private List<Integer> beforeCommonComparableColumnIndexes = new ArrayList<>();
    private List<Integer> afterCommonComparableColumnIndexes = new ArrayList<>();
    private CompConfig config;

    /**
     * @param beforeSource
     * @param afterSource
     */
    public ColumnStructure(Source beforeSource, Source afterSource, CompConfig config, ComparatorListener listener) {
        this.beforeSource = beforeSource;
        this.afterSource = afterSource;
        this.config = config;
        analyst(listener);
    }

    /**
     * @param listener
     */
    private void analyst(ComparatorListener listener) {
        beforeSource.getColumn2indexMap().keySet().forEach(key -> {
            if (afterSource.getColumn2indexMap().containsKey(key)) {
                commonColumns.add(key);
                if (!config.getIgnoredHeaders().contains(key)) {
                    beforeCommonComparableColumnIndexes.add(beforeSource.getColumn2indexMap().get(key));
                    afterCommonComparableColumnIndexes.add(afterSource.getColumn2indexMap().get(key));
                    commonComparableColumns.add(key);
                    commonComparableColumnNumber++;
                }

            } else {
                afterMissedColumns.add(key);
            }
        });
        afterSource.getColumn2indexMap().keySet().forEach(key -> {
            if (!beforeSource.getColumn2indexMap().containsKey(key)) {
                beforeMissedColumns.add(key);
            }
        });

        log.info("Before headers: " + String.join(", ", beforeSource.getHeaders()));
        log.info("After headers: " + String.join(", ", afterSource.getHeaders()));
        log.info("Common Headers: " + String.join(", ", commonColumns));
        listener.handleMissedBeforeHeader(beforeMissedColumns);
        listener.handleMissedAfterHeader(afterMissedColumns);
    }

}
