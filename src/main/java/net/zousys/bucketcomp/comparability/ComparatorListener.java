package net.zousys.bucketcomp.comparability;

import java.util.List;
import java.util.Map;

public interface ComparatorListener {

    void handleMissedInBefore(String key);

    void handleMissedInAfter(String key);

    void handleMissedBeforeHeader(List<String> headers);

    void handleMissedAfterHeader(List<String> headers);

    void handleMisMatched(String key, ComparedRow comparedRow);

    void handleMatched(String key, ComparedRow comparedRow);

    void updateUnitedHeaders(List<String> unitedHeaders);

    void handleUnitedHeaderMapping(Map<String, Integer> unitedHeadermapping);

    void handleMarkers(Map<String, Integer> markers);

    void handleNotice(String key);

    void appendInformation();
}
