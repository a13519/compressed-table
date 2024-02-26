package net.zousys.compressedtable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookKeeper {
    void handleMissedInBefore(Set<String> entries);
    void handleMissedInAfter(Set<String> entries);

    void handleMissedBeforeHeader(List<String> headers);
    void handleMissedAfterHeader(List<String> headers);

    void handleMissMatched(ComparisonResult.RowResult mismatch);

    void handleMatched(String key);

    void appendInformation();
}
