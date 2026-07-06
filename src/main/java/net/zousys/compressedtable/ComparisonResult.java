package net.zousys.compressedtable;

import lombok.*;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ComparisonResult {
    @NonNull
    private CompressedTable before;
    @NonNull
    private CompressedTable after;

    private Set<String> beforeMissed = new HashSet<>();
    private Set<String> afterMissed = new HashSet<>();
    private List<String> beforeMissedHeaders = new ArrayList<>();
    private List<String> afterMissedHeaders = new ArrayList<>();
    private List<String> matched = new ArrayList<>();
    private List<RowResult> mismatches = new ArrayList<>();
    private List<String> unitedHeaders;
    private Map<String, Integer> markers;
    private Set<String> ignoredFields = new HashSet<>();
    /**
     * @param before
     * @param after
     */
    public ComparisonResult(CompressedTable before, CompressedTable after) {
        this.before = before;
        this.after = after;
    }

    @NoArgsConstructor
    public static class RowResult {
        @Setter
        @Getter
        KeyValue matchedKey;
        @Getter
        Map<String, ResultField> fields = new HashMap<>();
        @Getter
        /**
         * the mismatched row discrepancy counts
         */
        private int missMatchNumber;

        /**
         * add field result to row result
         *
         * @param resultField
         */
        public void addFieldResult(ResultField resultField) {
            fields.put(resultField.getName(), resultField);
            if (resultField.isMissmatched() && !resultField.isIgnored()) {
                missMatchNumber++;
            }
        }
    }

    @Builder
    @Getter
    @Setter
    @ToString
    public static class ResultField {
        private String name;
        private String beforeField;
        private String afterField;
        private boolean missmatched;
        private boolean ignored;
        private boolean strictMissed;
    }

}
