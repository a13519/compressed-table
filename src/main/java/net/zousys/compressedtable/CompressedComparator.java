package net.zousys.compressedtable;

import lombok.Builder;
import net.zousys.compressedtable.impl.CompressedTable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

@Builder
public class CompressedComparator {

    private Set<String> ignoredFields;

    private CompressedTable before;

    private CompressedTable after;

    @Builder.Default
    private Set<String> beforeMissed = new HashSet<>();
    @Builder.Default
    private Set<String> afterMissed = new HashSet<>();
    @Builder.Default
    private List<String> beforeMissedHeaders = new ArrayList<>();
    @Builder.Default
    private List<String> afterMissedHeaders = new ArrayList<>();
    @Builder.Default
    private Set<String> shared = new HashSet<>();

    private List<String> unitedHeaders;

    private Map<String, Integer> unitedHeaderMapping;

    private BookKeeper bookKeeper;

    private boolean trim;

    public CompressedComparator setIgnoredFields(String[] fields) {
        ignoredFields = new HashSet<>();
        ignoredFields.addAll(Arrays.stream(fields).collect(Collectors.toSet()));
        return this;
    }

    public void compare() {
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), beforeMissed, null);
        bookKeeper.handleMissedInBefore(beforeMissed);
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), afterMissed, shared);
        bookKeeper.handleMissedInAfter(afterMissed);
        shared.removeAll(beforeMissed);
        beforeMissed = null;
        afterMissed = null;
        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders, null);
        bookKeeper.handleMissedBeforeHeader(beforeMissedHeaders);
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders, null);
        bookKeeper.handleMissedAfterHeader(afterMissedHeaders);
        uniteHeaders();
        bookKeeper.updateUnitedHeaders(unitedHeaders);

        ArrayList<String> mml = new ArrayList<>();
        shared.forEach(key -> {
            if (before.getKeyedMapping().get(key).getContent().hash()==
                    after.getKeyedMapping().get(key).getContent().hash()) {
                bookKeeper.handleMatched(key);
            } else {
                mml.add(key);
            }
        });

        mml.forEach(key -> {
            try {
                ComparisonResult.RowResult mismatch =
                        compareRow(
                                before.getKeyedMapping().get(key),
                                after.getKeyedMapping().get(key),
                                ignoredFields,
                                before,
                                after,
                                trim,
                                unitedHeaders);
                bookKeeper.handleMissMatched(mismatch);
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private static final ComparisonResult.RowResult compareRow(
            Row a,
            Row b,
            Set<String> ignoredFields,
            CompressedTable before,
            CompressedTable after,
            boolean trim,
            List<String> unitedHeaders) throws DataFormatException, IOException {

        List<String> fieldsA = a.getContent().form();
        List<String> fieldsB = b.getContent().form();

        ComparisonResult.RowResult rowResult = new ComparisonResult.RowResult();
        rowResult.setStringkey(a.getKey().toString());

        for (String headerA : unitedHeaders) {
            Integer beforeInd = before.getHeaderMapping().get(headerA);
            Integer afterInd = after.getHeaderMapping().get(headerA);
            String fvbefore = beforeInd==null?null:fieldsA.get(beforeInd);
            String fvafter = afterInd==null?null:fieldsB.get(afterInd);
            ComparisonResult.ResultField rf = ComparisonResult.ResultField.builder()
                    .beforeField(trim&&fvbefore!=null?fvbefore.trim():fvbefore)
                    .afterField(trim&&fvafter!=null?fvafter.trim():fvafter)
                    .build();
            rowResult.getFields().add(rf);

            if (ignoredFields!=null && ignoredFields.contains(headerA)) {
                rf.setIgnored(true);
            } else {
                if ((rf.getBeforeField() == null || rf.getAfterField() == null)
                        ||!rf.getBeforeField().equals(rf.getAfterField())) {
                    rf.missmatched=true;
                    rowResult.unifiedMismatch=true;
                }
            }
        }

        return rowResult;
    }


    public static final void contains(Set<String> a, Set<String> b, Set<String> register, Set<String> deregister) {
        a.forEach(key->{
            if (!b.contains(key)) {
                register.add(key);
            } else {
                if (deregister != null) {
                    deregister.add(key);
                }
            }
        });
    }
    public static final void contains(List<String> a, List<String> b, List<String> register, List<String> deregister) {
        a.forEach(key->{
            if (!b.contains(key)) {
                register.add(key);
            } else {
                if (deregister != null) {
                    deregister.add(key);
                }
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

    private void pickMissed() {
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), beforeMissed, null);
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), afterMissed, shared);

        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders, null);
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders, null);
        uniteHeaders();

    }

    public void uniteHeaders() {
        unitedHeaders = new ArrayList<>();
        unitedHeaderMapping = new HashMap<>();
        unitedHeaders = Stream.concat(before.getHeaders().stream(),beforeMissedHeaders.stream()).collect(Collectors.toList());
        unitedHeaders.stream().forEach(a->unitedHeaderMapping.put(a, unitedHeaderMapping.size()));
    }

    private void compare4Keeper() {
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), beforeMissed, null);
        bookKeeper.handleMissedInBefore(beforeMissed);
        beforeMissed = null;
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), afterMissed, shared);
        bookKeeper.handleMissedInAfter(afterMissed);
        afterMissed = null;
        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders, null);
        bookKeeper.handleMissedBeforeHeader(beforeMissedHeaders);
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders, null);
        bookKeeper.handleMissedAfterHeader(afterMissedHeaders);
        uniteHeaders();
        bookKeeper.updateUnitedHeaders(unitedHeaders);

        before.getKeyedMapping().keySet().forEach(key->{
            Row beforeRow = before.getKeyedMapping().get(key);
            Row afterRow = after.getKeyedMapping().get(key);
            if (afterRow!=null){
                try {
                    ComparisonResult.RowResult mismatch =
                            compareRow(beforeRow,
                                    afterRow,
                                    ignoredFields,
                                    before,
                                    after,
                                    trim,
                                    unitedHeaders);
                    if (mismatch.isUnifiedMismatch()){
                        bookKeeper.handleMissMatched(mismatch);
                    } else {
                        bookKeeper.handleMatched(key);
                    }
                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


}
