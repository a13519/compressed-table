package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.ComparatorListener;
import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.impl.CompressedRow;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareListener implements ComparatorListener {
    ArrayList<CompressedRow> beforemissed = new ArrayList<CompressedRow>();
    ArrayList<CompressedRow> aftermissed = new ArrayList<CompressedRow>();
    @Override
    public void handleBeforeLoaded(CompressedTable before) {
        System.out.println("Load before rows: "+before.size());
    }

    @Override
    public void handleAfterLoaded(CompressedTable after) {
        System.out.println("Load after rows: "+after.size());
    }

    @Override
    public void handleMissedInBefore(Set<KeyValue> entries) {
        System.out.println("Missed rows in before table: "+entries.size());
        entries.forEach(a->System.out.println(" -"+a));
    }

    @Override
    public void handleMissedInAfter(Set<KeyValue> entries) {
        System.out.println("Missed rows in after table: "+entries.size());
        entries.forEach(a->System.out.println(" -"+a));
    }

    @Override
    public void handleMissedBeforeHeader(List<String> headers) {
        System.out.println("Missed headers in before table "+headers.size());
    }

    @Override
    public void handleMissedAfterHeader(List<String> headers) {
        System.out.println("Missed headers in after table "+headers.size());
    }

    @Override
    public void handleMisMatched(ComparisonResult.RowResult mismatch) {
        System.out.println("Mismatched: "+mismatch.getMatchedKey());
    }

    @Override
    public void updateUnitedHeaders(List<String> unitedHeaders) {
        System.out.println("United Headers: "+unitedHeaders.size());
    }

    @Override
    public void handleUnitedHeaderMapping(Map<String, Integer> unitedHeadermapping) {

    }


    @Override
    public void handleMarkers(Map<String, Integer> markers) {
    }

    @Override
    public void handleMatched(KeyValue key) {
//        System.out.println("Matched: "+key);
    }

    @Override
    public void handleMatchedList(List<KeyValue> keys) {
        System.out.println("Matched list: "+keys.size());
    }

    @Override
    public void handleMisMatchedList(List<KeyValue> keys) {
        System.out.println("MisMatched list: "+keys.size());
    }

    @Override
    public void handleNotice(String key) {

    }

    @Override
    public void appendInformation() {

    }
}
