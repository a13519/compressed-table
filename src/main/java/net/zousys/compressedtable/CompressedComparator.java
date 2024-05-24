package net.zousys.compressedtable;

import lombok.Builder;
import lombok.Getter;
import net.zousys.compressedtable.impl.CompressedTable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

@Builder
public class CompressedComparator {
    private ComparatorListener comparatorListener;
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

    @Getter
    @Builder.Default
    private Map<String, Integer> markers = new HashMap<>();

    private boolean trim;

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
    private void addMarker(ComparisonResult.RowResult mismatch) {
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
    public void compare() {
        // missed in before
        contains(after.getKeyedMapping().keySet(), before.getKeyedMapping().keySet(), beforeMissed, null);
        comparatorListener.handleMissedInBefore(beforeMissed);
        // remove from after
        after.removeRowsByKey(beforeMissed);

        // missed in after
        contains(before.getKeyedMapping().keySet(), after.getKeyedMapping().keySet(), afterMissed, shared);
        comparatorListener.handleMissedInAfter(afterMissed);
        // remove from before
        before.removeRowsByKey(afterMissed);

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

        beforeMissedHeaders = null;
        afterMissedHeaders = null;

        ArrayList<String> ml = new ArrayList<>();
        ArrayList<String> mml = new ArrayList<>();
        shared.forEach(key -> {
            if (before.getKeyedMapping().get(key).getContent().hash() ==
                    after.getKeyedMapping().get(key).getContent().hash()) {
                ml.add(key);
                comparatorListener.handleMatched(key);
                // remove from before and after
//                after.removeRowByKey(key);
                before.removeRowByKey(key);
            } else {
                mml.add(key);
                try {
                    ComparisonResult.RowResult mismatch =
                            compareRow(
                                    key,
                                    ignoredFields,
                                    before,
                                    after,
                                    trim,
                                    unitedHeaders);
                    if (mismatch.getMissMatchNumber() > 0) {
                        addMarker(mismatch);
                        comparatorListener.handleMisMatched(mismatch);
                    }
                    // remove from before and after
                    after.removeRowByKey(key);
                    before.removeRowByKey(key);
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
    }

    /**
     * @param key
     * @param ignoredFields
     * @param before
     * @param after
     * @param trim
     * @param unitedHeaders
     * @return
     * @throws DataFormatException
     * @throws IOException
     */
    private static final ComparisonResult.RowResult compareRow(
            String key,
            Set<String> ignoredFields,
            CompressedTable before,
            CompressedTable after,
            boolean trim,
            List<String> unitedHeaders) throws DataFormatException, IOException {
        Row a = before.seekByKey(key).orElseThrow();
        Row b = after.seekByKey(key).orElseThrow();
        List<String> fieldsA = a.getContent().form();
        List<String> fieldsB = b.getContent().form();

        ComparisonResult.RowResult rowResult = new ComparisonResult.RowResult();
        rowResult.setStringkey(a.getKey().toString());

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


            if (ignoredFields != null && ignoredFields.contains(headerA)) {
                rf.setIgnored(true);
                rf.setMissmatched(false);
            } else {
                rf.setIgnored(false);
                if ((rf.getBeforeField() == null || rf.getAfterField() == null)
                        || !rf.getBeforeField().equals(rf.getAfterField())) {
                    rf.setMissmatched(true);
                }
            }

            rowResult.addFieldResult(rf);
        }

        return rowResult;
    }


    public static final void contains(Set<String> a, Set<String> b, Set<String> register, Set<String> deregister) {
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

    public static final void contains(String[] as, String[] bs, List<String> register) {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        a.addAll(Arrays.stream(as).collect(Collectors.toSet()));
        b.addAll(Arrays.stream(bs).collect(Collectors.toSet()));
        a.forEach(key -> {
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
