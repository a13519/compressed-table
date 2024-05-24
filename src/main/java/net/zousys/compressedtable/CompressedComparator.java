package net.zousys.compressedtable;

import lombok.Builder;
import lombok.Getter;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.key.KeyHeaders;
import net.zousys.compressedtable.key.KeyHeadersList;
import net.zousys.compressedtable.key.KeyValue;

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
    private Set<KeyValue> beforeMissed = new HashSet<>();
    @Builder.Default
    private Set<KeyValue> afterMissed = new HashSet<>();
    @Builder.Default
    private List<String> beforeMissedHeaders = new ArrayList<>();
    @Builder.Default
    private List<String> afterMissedHeaders = new ArrayList<>();
    @Builder.Default
    private Set<KeyValue> shared = new HashSet<>();

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
    public CompressedComparator compare() {
        // missed in before
        contains(after, before, beforeMissed, null);
        comparatorListener.handleMissedInBefore(beforeMissed);
        // remove from after
        after.removeRowsByMainKey(beforeMissed);

        // missed in after
        contains(before, after, afterMissed, shared);
        comparatorListener.handleMissedInAfter(afterMissed);
        // remove from before
        before.removeRowsByMainKey(afterMissed);

        // for comparator
        shared.removeAll(beforeMissed);

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
            Row atb = before.getKeyedMappingMap().get(key.getName()).get(key.getValue());
            Row btb = null;
            boolean identical = false;
            for (KeyHeaders akh : after.getKeyHeaderList()) {
                Map<String, Row> msk = after.getKeyedMappingMap().get(key.getName());
                btb = msk==null?null:msk.get(key.getValue());
                if (btb!=null) {
                    if (atb.getContent().hash() == btb.getContent().hash()) {
                        ml.add(key);
                        identical = true;
                        // remove from before and after
                        after.removeRowByMainKey(key);
                        before.removeRowByMainKey(key);
                        comparatorListener.handleMatched(key);
                        break;
                    }
                }
            }
            if (!identical) {
                if (btb==null){
                    System.out.println("CC btb is null to "+atb.getKey().getMainKey());
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
                        after.removeRowByMainKey(key);
                        before.removeRowByMainKey(key);
                    } catch (DataFormatException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        });

        comparatorListener.handleMatchedList(ml);
        comparatorListener.handleMisMatchedList(mml);
        return this;
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
            KeyValue key,
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
        rowResult.setStringkey(a.getKey());

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
                if ((rf.getBeforeField() == null || rf.getAfterField() == null)
                        || !rf.getBeforeField().equals(rf.getAfterField())) {
                    rf.setMissmatched(true);
                }
            }

            rowResult.getFields().add(rf);
        }

        return rowResult;
    }

    /**
     *
     * @param a
     * @param b
     * @param register
     * @param deregister
     */
    public static final void contains(CompressedTable a, CompressedTable b, Set<KeyValue> register, Set<KeyValue> deregister) {
        Set<String> keyMapnameset = a.getKeyedMappingMap().keySet();
        keyMapnameset.forEach( akmv->contains(akmv, a, b, register, deregister));
    }

    /**
     *
     * @param keyname
     * @param act
     * @param bct
     * @param register
     * @param deregister
     */
    public static final void contains(String keyname, CompressedTable act, CompressedTable bct, Set<KeyValue> register, Set<KeyValue> deregister) {
        Map<String, Row> akvm = act.getKeyedMappingMap().get(keyname);
        Map<String, Row> bkvm = bct.getKeyedMappingMap().get(keyname);
        Set<String> a = akvm.keySet();
        Set<String> b = bkvm.keySet();
        a.forEach(key -> {
            if (!b.contains(key)) {
                Row ar = akvm.get(key);
                Key ak = ar.getKey();
                KeyValue av = ak.getKeyValue(key);
                if (av!=null) {
                    register.add(av);
                }
            } else {
                if (deregister != null) {
                    KeyValue av = akvm.get(key).getKey().getKeyValue(key);
                    if (av != null) {
                        deregister.add(av);
                    }
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
        contains(after, before, beforeMissed, null);
        contains(before, after, afterMissed, shared);

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
    }


}
