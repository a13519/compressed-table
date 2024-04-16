package net.zousys.compressedtable;

import lombok.*;
import net.zousys.compressedtable.impl.CompressedTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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

    public ComparisonResult(@NonNull CompressedTable before, @NonNull CompressedTable after) {
        this.before = before;
        this.after = after;

    }


    @NoArgsConstructor
    public static class RowResult {
        @Setter
        @Getter
        String stringkey;
        @Getter
        List<ResultField> fields = new ArrayList<>();
        @Getter
        @Setter
        boolean unifiedMismatch;
    }

    @Builder
    @Getter
    @Setter
    public static class ResultField {
        String beforeField;
        String afterField;
        boolean missmatched;
        boolean ignored;
    }

}
