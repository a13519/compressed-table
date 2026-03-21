package net.zousys.bucketcomp;

import lombok.extern.log4j.Log4j2;
import net.zousys.bucketcomp.comparability.ComparatorListener;
import net.zousys.bucketcomp.comparability.FieldResult;

import java.util.List;
import java.util.Map;

@Log4j2
public class CompListener implements ComparatorListener {
    @Override
    public void handleMissedInBefore(String key) {
        log.error("Missed in before key: " + key);
    }

    @Override
    public void handleMissedInAfter(String key) {
        log.error("Missed in after key: " + key);
    }

    @Override
    public void handleMissedBeforeHeader(List<String> headers) {

    }

    @Override
    public void handleMissedAfterHeader(List<String> headers) {

    }

    @Override
    public void handleMisMatched(String key, FieldResult fieldresults) {
        log.error("Mismatched by key: "+key+" - "+fieldresults.toString());
    }

    @Override
    public void handleMatched(String key, String[] fields) {

    }

    @Override
    public void updateUnitedHeaders(List<String> unitedHeaders) {

    }

    @Override
    public void handleUnitedHeaderMapping(Map<String, Integer> unitedHeadermapping) {

    }

    @Override
    public void handleMarkers(Map<String, Integer> markers) {

    }

    @Override
    public void handleNotice(String key) {

    }

    @Override
    public void appendInformation() {

    }
}
