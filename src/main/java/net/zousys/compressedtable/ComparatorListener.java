package net.zousys.compressedtable;

import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.key.KeyValue;

import java.util.List;
import java.util.Set;

public interface ComparatorListener {

    void handleBeforeLoaded(CompressedTable before);

    void handleAfterLoaded(CompressedTable after);

    void handleMissedInBefore(Set<KeyValue> entries);

    void handleMissedInAfter(Set<KeyValue> entries);

    void handleMissedBeforeHeader(List<String> headers);

    void handleMissedAfterHeader(List<String> headers);

    void handleMisMatched(ComparisonResult.RowResult mismatch);

    void handleMatched(KeyValue key);

    void handleMatchedList(List<KeyValue> keys);

    void handleMisMatchedList(List<KeyValue> keys);

    void updateUnitedHeaders(List<String> unitedHeaders);

    void handleNotice(String key);

    void appendInformation();
}
