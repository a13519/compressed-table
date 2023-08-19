package net.zousys.compressedtable;

import lombok.Builder;
import net.zousys.compressedtable.impl.CompressedTable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

@Builder
public class CompressedComparator {

    private Set<String> ignoredFields;

    private CompressedTable before;

    private CompressedTable after;

    public CompressedComparator setIgnoredFields(String[] fields) {
        ignoredFields = new HashSet<>();
        ignoredFields.addAll(Arrays.stream(fields).collect(Collectors.toSet()));
        return this;
    }

    public ComparisonResult compare() {
        ComparisonResult comparisonResult = new ComparisonResult();

        Set<String> beforeKeyset = before.getKeyedMapping().keySet();
        Set<String> afterKeyset = after.getKeyedMapping().keySet();
        contains(afterKeyset, beforeKeyset, comparisonResult.getBeforeMissed());
        contains(beforeKeyset, afterKeyset, comparisonResult.getAfterMissed());
        beforeKeyset.forEach(key->{
            Row beforeRow = before.getKeyedMapping().get(key);
            Row afterRow = after.getKeyedMapping().get(key);
            if (afterRow!=null){
                try {
                    ComparisonResult.RowResult mismatch =
                            compareRow(beforeRow,
                                    afterRow,
                                    ignoredFields,
                                    before.getHeaderMapping(),
                                    after.getHeaderMapping());
                    if (mismatch.isMismatch()){
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
            Map<String, Integer> headerMapA,
            Map<String, Integer> headerMapB) throws DataFormatException, IOException {

        List<String> fieldsA = a.getContent().form();
        List<String> fieldsB = b.getContent().form();

        ComparisonResult.RowResult rowResult = new ComparisonResult.RowResult();
        rowResult.setStringkey(a.getKey().toString());
        headerMapA.keySet().forEach(headerA->{
            ComparisonResult.ResultField rf = ComparisonResult.ResultField.builder()
                            .beforeField(fieldsA.get(headerMapA.get(headerA)))
                                    .afterField(fieldsB.get(headerMapB.get(headerA)))
                                            .build();
            rowResult.getFields().add(rf);
            if (ignoredFields==null || !ignoredFields.contains(headerA)) {
                if (!fieldsA.get(headerMapA.get(headerA))
                        .equals(fieldsB.get(headerMapB.get(headerA)))) {
                    rf.missmatched=true;
                }
            }
        });
        return rowResult;
    }
    private static final void contains(Set<String> a, Set<String> b, Set<String> register) {
        a.forEach(key->{
            if (!b.contains(key)) {
                register.add(key);
            }
        });
    }
}
