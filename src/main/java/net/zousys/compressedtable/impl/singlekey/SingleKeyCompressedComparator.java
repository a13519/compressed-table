package net.zousys.compressedtable.impl.singlekey;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.ComparatorListener;
import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.CompressedComparator;
import net.zousys.compressedtable.Row;
import net.zousys.compressedtable.impl.KeyValue;

import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

/**
 *
 */
@Log4j2
@Builder
public class SingleKeyCompressedComparator implements net.zousys.compressedtable.CompressedComparator {
    @Setter
    private ComparatorListener comparatorListener;
    private Set<String> ignoredFields;
    @Setter
    private net.zousys.compressedtable.impl.CompressedTable before;
    @Setter
    private net.zousys.compressedtable.impl.CompressedTable after;
    @Setter
    private List<String> unitedHeaders;
    @Setter
    private Map<String, Integer> unitedHeaderMapping;

    @Builder.Default
    private Set<KeyValue> beforeMissed = new HashSet<>();
    @Builder.Default
    private Set<KeyValue> afterMissed = new HashSet<>();
    @Builder.Default
    private List<String> beforeMissedHeaders = new ArrayList<>();
    @Builder.Default
    private List<String> afterMissedHeaders = new ArrayList<>();
    @Builder.Default
    private Set<KeyValue> shared = new HashSet<>();
    @Setter
    private boolean trim;
    private boolean strictMissed;

    @Getter
    @Builder.Default
    private Map<String, Integer> markers = new HashMap<>();


    /**
     * This is to set the ignored colume of table in comparison, those columns won't be compared
     *
     * @param fields the columns
     * @return
     */
    public CompressedComparator setIgnoredFields(String[] fields) {
        ignoredFields = new HashSet<>();
        ignoredFields.addAll(Arrays.stream(fields).collect(Collectors.toSet()));
        return this;
    }

    /**
     * This is to count mismatched column time, later after the result spread sheet / csv generated, the header will mark the times of discrenpancies
     * @param mismatch
     */
    public void addMarker(ComparisonResult.RowResult mismatch) {
        for (ComparisonResult.ResultField arf : mismatch.getFields()) {
            if (arf.isMissmatched()) {
                Integer ai = markers.get(arf.getName());
                if (ai == null) {
                    markers.put(arf.getName(), 1);
                } else {
                    markers.put(arf.getName(), ai.intValue() + 1);
                }
            }
        }
    }
    /**
     * This is to compare two tables
     *
     */
    public SingleKeyCompressedComparator compare() {
        // missed in before
        contains(after.getKeyedMappingMap().getMainKeyedMapping().keySet(),
                before.getKeyedMappingMap().getMainKeyedMapping().keySet(), beforeMissed, null);
        comparatorListener.handleMissedInBefore(beforeMissed);
        // remove from after
        after.removeRowsByNativeKey(beforeMissed);

        // missed in after
        contains(before.getKeyedMappingMap().getMainKeyedMapping().keySet(),
                after.getKeyedMappingMap().getMainKeyedMapping().keySet(), afterMissed, shared);
        comparatorListener.handleMissedInAfter(afterMissed);
        // remove from before
        before.removeRowsByNativeKey(afterMissed);

        // for comparator
        shared.removeAll(beforeMissed);

        beforeMissed = null;
        afterMissed = null;
        // headers
        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders, null);
        comparatorListener.handleMissedBeforeHeader(beforeMissedHeaders);
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders, null);
        comparatorListener.handleMissedAfterHeader(afterMissedHeaders);
        uniteHeaders();
        comparatorListener.updateUnitedHeaders(unitedHeaders);

        ArrayList<KeyValue> ml = new ArrayList<>();
        ArrayList<KeyValue> mml = new ArrayList<>();
        shared.forEach(key -> {
            if (before.getKeyedMappingMap().getMainKeyedMapping().get(key.getValue()).getContent().hash() ==
                    after.getKeyedMappingMap().getMainKeyedMapping().get(key.getValue()).getContent().hash()) {
                ml.add(key);
                comparatorListener.handleMatched(key);
                // remove from before and after
//                after.removeRowByMainKey(key);
//                before.removeRowByMainKey(key);
            } else {
                try {
                    ComparisonResult.RowResult mismatch = compareRow(key);
                    if (mismatch.getMissMatchNumber() > 0) {
                        mml.add(key);
                        addMarker(mismatch);
                        comparatorListener.handleMisMatched(mismatch);
                    // remove from before and after
                    after.removeRowByMainKey(key);
                    before.removeRowByMainKey(key);
                    } else {
                        // this is still matched if no fields are mismatch
                        // this could be the trimming or the strictColumn indicator is false
                        // and no column field is different but the missing columns or headers
                        ml.add(key);
                        comparatorListener.handleMatched(key);
                        // remove from before and after
//                        after.removeRowByMainKey(key);
//                        before.removeRowByMainKey(key);
                    }

                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        comparatorListener.handleMarkers(markers);
        comparatorListener.handleMatchedList(ml);
        comparatorListener.handleMisMatchedList(mml);
        return this;
    }

    /**
     * @param key
     * @return
     * @throws DataFormatException
     * @throws IOException
     */
    private final ComparisonResult.RowResult compareRow(KeyValue key) throws DataFormatException, IOException {
        Row a = before.seekByMainKey(key).orElseThrow();
        Row b = after.seekByMainKey(key).orElseThrow();
        List<String> fieldsA = a.getContent().form();
        List<String> fieldsB = b.getContent().form();

        ComparisonResult.RowResult rowResult = new ComparisonResult.RowResult();
        rowResult.setMatchedKey(key);

        for (String headerA : unitedHeaders) {
            Integer beforeInd = before.getHeaderMapping().get(headerA);
            Integer afterInd = after.getHeaderMapping().get(headerA);
            String fvbefore = beforeInd == null ? null : fieldsA.get(beforeInd);
            String fvafter = afterInd == null ? null : fieldsB.get(afterInd);

            ComparisonResult.ResultField rf = ComparisonResult.ResultField.builder()
                    .name(headerA)
                    .beforeField(trim && fvbefore != null ? fvbefore.trim() : fvbefore)
                    .afterField(trim && fvafter != null ? fvafter.trim() : fvafter)
                    .build();

            if (!strictMissed &&
                    (afterMissedHeaders.contains(headerA) || beforeMissedHeaders.contains(headerA))) {
                rf.setStrictMissed(false);
                rf.setMissmatched(false);
                rf.setIgnored(false);
            } else if (ignoredFields != null && ignoredFields.contains(headerA)) {
                rf.setIgnored(true);
                rf.setMissmatched(false);
                rf.setStrictMissed(false);
            } else {
                rf.setIgnored(false);
                rf.setStrictMissed(true);
                if ((rf.getBeforeField() == null || rf.getAfterField() == null)
                        || !rf.getBeforeField().equals(rf.getAfterField())) {
                    rf.setMissmatched(true);
                }
            }

            rowResult.addFieldResult(rf);
        }

        return rowResult;
    }


    public static final void contains(Set<String> a, Set<String> b, Set<KeyValue> register, Set<KeyValue> deregister) {

        a.forEach(key -> {
            if (!b.contains(key)) {
                register.add(KeyValue.mainKey(key));
            } else {
                if (deregister != null) {
                    deregister.add(KeyValue.mainKey(key));
                }
            }
        });
    }

    public static final void contains(List<String> a, List<String> b, List<String> register, List<String> deregister) {
        a.forEach(key -> {
            if (!b.contains(key)) {
                register.add(key);
            } else {
                if (deregister != null) {
                    deregister.add(key);
                }
            }
        });
    }

    public static final void contains(String[] as, String[] bs, List<KeyValue> register) {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        a.addAll(Arrays.stream(as).collect(Collectors.toSet()));
        b.addAll(Arrays.stream(bs).collect(Collectors.toSet()));
        a.forEach(key -> {
            if (!b.contains(key)) {
                register.add(KeyValue.mainKey(key));
            }
        });
    }

    private void pickMissed() {
        contains(after.getKeyedMappingMap().getMainKeyedMapping().keySet(),
                before.getKeyedMappingMap().getMainKeyedMapping().keySet(), beforeMissed, null);
        contains(before.getKeyedMappingMap().getMainKeyedMapping().keySet(),
                after.getKeyedMappingMap().getMainKeyedMapping().keySet(), afterMissed, shared);

        contains(after.getHeaders(), before.getHeaders(), beforeMissedHeaders, null);
        contains(before.getHeaders(), after.getHeaders(), afterMissedHeaders, null);
        uniteHeaders();

    }

    /**
     * Union of before and after table
     */
    public void uniteHeaders() {
        unitedHeaders = new ArrayList<>();
        unitedHeaderMapping = new HashMap<>();
        unitedHeaders = Stream.concat(before.getHeaders().stream(), beforeMissedHeaders.stream()).collect(Collectors.toList());
        unitedHeaders.stream().forEach(a -> unitedHeaderMapping.put(a, unitedHeaderMapping.size()));
        comparatorListener.handleUnitedHeaderMapping(unitedHeaderMapping);
    }


}
