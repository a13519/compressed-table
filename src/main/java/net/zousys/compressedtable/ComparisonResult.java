package net.zousys.compressedtable;

import lombok.*;
import net.zousys.compressedtable.impl.CompressedTable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
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
    private Map<String, Integer> unitedHeaderMapping = new HashMap<>();

    public ComparisonResult(@NonNull CompressedTable before, @NonNull CompressedTable after) {
        this.before = before;
        this.after = after;
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), getBeforeMissed());
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), getAfterMissed());
        contains(after.getHeaders(), before.getHeaders(), getBeforeMissedHeaders());
        contains(before.getHeaders(), after.getHeaders(), getAfterMissedHeaders());
        uniteHeaders();
    }

    public void uniteHeaders() {
        unitedHeaders = Stream.concat(before.getHeaders().stream(),beforeMissedHeaders.stream()).collect(Collectors.toList());
        unitedHeaders.stream().forEach(a->unitedHeaderMapping.put(a, unitedHeaderMapping.size()));
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

    public static final void contains(Set<String> a, Set<String> b, Set<String> register) {
        a.forEach(key->{
            if (!b.contains(key)) {
                register.add(key);
            }
        });
    }
    public static final void contains(List<String> a, List<String> b, List<String> register) {
        a.forEach(key->{
            if (!b.contains(key)) {
                register.add(key);
            }
        });
    }
    public static final void contains(String[] as, String[] bs, List<String> register) {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        a.addAll(Arrays.stream(as).collect(Collectors.toSet()));
        b.addAll(Arrays.stream(bs).collect(Collectors.toSet()));
        a.forEach(key->{
            if (!b.contains(key)) {
                register.add(key);
            }
        });
    }
}
