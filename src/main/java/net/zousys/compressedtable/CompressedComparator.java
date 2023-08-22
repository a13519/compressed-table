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

    private boolean trim;

    public CompressedComparator setIgnoredFields(String[] fields) {
        ignoredFields = new HashSet<>();
        ignoredFields.addAll(Arrays.stream(fields).collect(Collectors.toSet()));
        return this;
    }

    public ComparisonResult compare() {
        ComparisonResult comparisonResult = new ComparisonResult(before, after);
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
                                    comparisonResult);
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
            ComparisonResult comparisonResult) throws DataFormatException, IOException {

        List<String> fieldsA = a.getContent().form();
        List<String> fieldsB = b.getContent().form();

        ComparisonResult.RowResult rowResult = new ComparisonResult.RowResult();
        rowResult.setStringkey(a.getKey().toString());

        for (String headerA : comparisonResult.getUnitedHeaders()) {
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

}
