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

    private Set<String> beforeMissed = new HashSet<>();
    private Set<String> afterMissed = new HashSet<>();
    private List<String> beforeMissedHeaders = new ArrayList<>();
    private List<String> afterMissedHeaders = new ArrayList<>();

    private List<String> unitedHeaders;
    private Map<String, Integer> unitedHeaderMapping = new HashMap<>();

    private BookKeeper bookKeeper;

    private boolean trim;

    public CompressedComparator setIgnoredFields(String[] fields) {
        ignoredFields = new HashSet<>();
        ignoredFields.addAll(Arrays.stream(fields).collect(Collectors.toSet()));
        return this;
    }


    public ComparisonResult compare() {
        if (bookKeeper!=null) {
            compare4Keeper();
            return null;
        }

        ComparisonResult comparisonResult = new ComparisonResult(before, after);
        pickMissed();
        comparisonResult.setUnitedHeaders(unitedHeaders);
        comparisonResult.setAfterMissed(afterMissed);
        comparisonResult.setBeforeMissed(beforeMissed);
        comparisonResult.setAfterMissedHeaders(afterMissedHeaders);
        comparisonResult.setBeforeMissedHeaders(beforeMissedHeaders);

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
                        comparisonResult.getMismatches().add(mismatch);
                    } else {
                        comparisonResult.getMatched().add(key);
                    }
                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return comparisonResult;
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

    private void pickMissed() {
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), beforeMissed);
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), afterMissed);
        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders);
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders);
        uniteHeaders();
    }

    public void uniteHeaders() {
        unitedHeaders = Stream.concat(before.getHeaders().stream(),beforeMissedHeaders.stream()).collect(Collectors.toList());
        unitedHeaders.stream().forEach(a->unitedHeaderMapping.put(a, unitedHeaderMapping.size()));
    }

    private void compare4Keeper() {
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), beforeMissed);
        bookKeeper.handleMissedInBefore(beforeMissed);
        beforeMissed = null;
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), afterMissed);
        bookKeeper.handleMissedInAfter(afterMissed);
        afterMissed = null;
        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders);
        bookKeeper.handleMissedBeforeHeader(beforeMissedHeaders);
        beforeMissedHeaders = null;
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders);
        bookKeeper.handleMissedAfterHeader(afterMissedHeaders);
        afterMissedHeaders = null;
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
                        mismatch = null;
                    } else {
                        bookKeeper.handleMatched(key);
                        key = null;
                    }
                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            beforeRow = null;
            afterRow = null;
        });
    }


}
