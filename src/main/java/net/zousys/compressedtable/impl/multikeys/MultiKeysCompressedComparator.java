package net.zousys.compressedtable.impl.multikeys;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.*;
import net.zousys.compressedtable.impl.KeyHeaders;
import net.zousys.compressedtable.impl.KeyValue;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;

@Log4j2
@Builder
public class MultiKeysCompressedComparator implements net.zousys.compressedtable.CompressedComparator {
    private ComparatorListener comparatorListener;
    private Set<String> ignoredFields;

    private CompressedTable before;

    private CompressedTable after;

    /**
     * main key
     */
    @Builder.Default
    private Set<String> beforeMissed = new HashSet<>();
    /**
     * main key
     */
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
    public MultiKeysCompressedComparator setIgnoredFields(String[] fields) {
        ignoredFields = new HashSet<>();
        ignoredFields.addAll(Arrays.stream(fields).collect(Collectors.toSet()));
        return this;
    }

    /**
     * This is to count mismatched column time, later after the result spread sheet / csv generated, the header will mark the times of discrenpancies
     *
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
     */
    public MultiKeysCompressedComparator compare() {
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

        ArrayList<String> ml = new ArrayList<>();
        ArrayList<KeyValue> mml = new ArrayList<>();
        shared.forEach(beforeMainKey -> {
            Row beforeRow = before.seekByMainKey(beforeMainKey).get();
            Row afterRow = null;
            boolean identical = false;
            boolean matched = false;
            KeyValue matchedKey = null;
            KeyValue beforeKey = null;
            String beforeKeyV;
            Map<String, Row> msk = null;
            for (KeyHeaders akh : after.getKeyHeaderList().getKeyHeadersList()) {
                String beforecomkey = akh.getCompositedKey();
                beforeKey = beforeRow.getKey().getKeyValue(beforecomkey);
                beforeKeyV = beforeKey.getValue();
                msk = after.getKeyedMappingMap().get(beforecomkey);
                afterRow = msk == null ? null : msk.get(beforeKeyV);
                if (afterRow != null) {
                    matched = true;
                    matchedKey = beforeKey;
                    if (matchedKey.getValue()!=null && beforeRow.getContent().hash() == afterRow.getContent().hash()) {
                        ml.add(beforeMainKey);
                        identical = true;
                        // remove from before and after
//                        after.removeRowByMainKey(beforekey);
//                        before.removeRowByMainKey(beforekey);
                        comparatorListener.handleMatched(beforeMainKey);
                        break;
                    }
                }
            }
            if (!matched || matchedKey.getValue() == null) {
                System.out.println("not match :" + beforeMainKey);
            } else if (!identical) {
                mml.add(matchedKey);
                try {
                    ComparisonResult.RowResult mismatch =
                            compareRow(
                                    matchedKey,
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
//                    after.removeRowByMainKey(beforekey);
//                    before.removeRowByMainKey(beforekey);
                } catch (DataFormatException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
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


            if (ignoredFields != null && ignoredFields.contains(headerA)) {
                rf.setIgnored(true);
                rf.setMissmatched(false);
            } else {
                if ((rf.getBeforeField() == null || rf.getAfterField() == null)
                        || !rf.getBeforeField().equals(rf.getAfterField())) {
                    rf.setMissmatched(true);
                }
            }

            rowResult.addFieldResult(rf);
        }

        return rowResult;
    }

    /**
     * This is to find out the b records missed in table a
     * It will go through every record in a and try to match b
     *
     * @param a
     * @param b
     * @param register
     * @param deregister
     */
    public static final void contains(CompressedTable a, CompressedTable b, Set<String> register, Set<String> deregister) {
        Set<String> keyMapnameset = a.getKeyedMappingMap().keySet();
        List<Set<String>> keySetMap = new ArrayList<>();

        keyMapnameset.forEach(akmv -> {
            try {
                keySetMap.add(containsX(akmv, a, b));
            } catch (MissingKeySetException mkse) {
                log.error("key missed: " + mkse);
            }
        });

        for (int i = 1; i < keySetMap.size(); i++) {
            keySetMap.get(0).retainAll(keySetMap.get(i));
        }
        register.addAll(keySetMap.get(0));
        if (deregister != null) {
            for (Row arow : a.getContents()) {
                if (!keySetMap.get(0).contains(arow.getKey().getMainKeyValue())) {
                    deregister.add(arow.getKey().getMainKeyValue());
                }
            }
        }

    }

    /**
     * @param keyname
     * @param act
     * @param bct
     */
    public static final Set<String> containsX(String keyname, CompressedTable act, CompressedTable bct) throws MissingKeySetException {
        Map<String, Row> bkvm = bct.getKeyedMappingMap().get(keyname);
        Map<String, Row> akvm = act.getKeyedMappingMap().get(keyname);

        if (akvm == null) {
            throw new MissingKeySetException("before table miss keyset of " + keyname);
        }
        if (bkvm == null) {
            throw new MissingKeySetException("after table miss keyset of " + keyname);
        }

        Set<String> a = akvm.keySet();
        Set<String> b = bkvm.keySet();
        Set<String> r = new HashSet<>();

        a.forEach(key -> {
            if (!b.contains(key)) {
                // this is the records from a missed in b
                Row ar = akvm.get(key);
                KeySet ak = ar.getKey();
                if (ak != null) {
                    r.add(ak.getMainKeyValue());
                }
            }
        });
        return r;
    }

    /**
     * @param keyname
     * @param act
     * @param bct
     * @param register
     * @param deregister
     */
    public static final void contains(String keyname,
                                      CompressedTable act,
                                      CompressedTable bct,
                                      Set<String> register,
                                      Set<String> deregister) throws MissingKeySetException {
        Map<String, Row> bkvm = bct.getKeyedMappingMap().get(keyname);
        Map<String, Row> akvm = act.getKeyedMappingMap().get(keyname);

        if (bkvm == null) {
            throw new MissingKeySetException();
        }


        Set<String> a = akvm.keySet();
        Set<String> b = bkvm.keySet();

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
