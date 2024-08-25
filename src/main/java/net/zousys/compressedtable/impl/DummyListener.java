package net.zousys.compressedtable.impl;

import net.zousys.compressedtable.ComparatorListener;
import net.zousys.compressedtable.ComparisonResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DummyListener implements ComparatorListener {
    @Override
    public void handleBeforeLoaded(CompressedTable before) {

    }

    @Override
    public void handleAfterLoaded(CompressedTable after) {

    }

    @Override
    public void handleMissedInBefore(Set<KeyValue> entries) {

    }

    @Override
    public void handleMissedInAfter(Set<KeyValue> entries) {

    }

    @Override
    public void handleMissedBeforeHeader(List<String> headers) {

    }

    @Override
    public void handleMissedAfterHeader(List<String> headers) {

    }

    @Override
    public void handleMisMatched(ComparisonResult.RowResult mismatch) {

    }

    @Override
    public void handleMatched(KeyValue key) {

    }

    @Override
    public void handleMatchedList(List<KeyValue> keys) {

    }

    @Override
    public void handleMisMatchedList(List<KeyValue> keys) {

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
