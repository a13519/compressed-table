package net.zousys.compressedtable;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@NoArgsConstructor
public class ComparisonResult {
    private Set<String> beforeMissed = new HashSet<>();
    private Set<String> afterMissed = new HashSet<>();
    private List<String> matched = new ArrayList<>();
    private List<RowResult> mismatches = new ArrayList<>();
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
    public static class ResultField {
        String beforeField;
        String afterField;
        boolean missmatched;
    }
}
