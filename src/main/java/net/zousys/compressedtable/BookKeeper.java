package net.zousys.compressedtable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookKeeper {
    void handleMissedInBefore(Set<String> entries);
    void handleMissedInAfter(Set<String> entries);

    void handleMissedBeforeHeader(List<String> headers);
    void handleMissedAfterHeader(List<String> headers);

    void handleMisMatched(ComparisonResult.RowResult mismatch);
    void handleMatched(String key);

    void handleMatchedList(List<String> keys);
    void handleMisMatchedList(List<String> keys);

    void updateUnitedHeaders(List<String> unitedHeaders);

    void handleNotice(String key);
    void appendInformation();
}
