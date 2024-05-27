package net.zousys.compressedtable;

import net.zousys.compressedtable.impl.multikeys.CompressedTable;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ComparatorListener {

    void handleBeforeLoaded(CompressedTable before);

    void handleAfterLoaded(CompressedTable after);

    void handleMissedInBefore(Set<String> entries);

    void handleMissedInAfter(Set<String> entries);

    void handleMissedBeforeHeader(List<String> headers);

    void handleMissedAfterHeader(List<String> headers);

    void handleMisMatched(ComparisonResult.RowResult mismatch);

    void handleMatched(String key);

    void handleMatchedList(List<String> keys);

    void handleMisMatchedList(List<KeyValue> keys);

    void updateUnitedHeaders(List<String> unitedHeaders);

    void handleUnitedHeadrMapping(Map<String, Integer> unitedHeadermapping);

    void handleMarkers(Map<String, Integer> markers);

    void handleNotice(String key);

    void appendInformation();
}
