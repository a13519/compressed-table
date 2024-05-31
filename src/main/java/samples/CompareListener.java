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

@Log4j2
public class CompareListener implements ComparatorListener {
    ArrayList<CompressedRow> beforemissed = new ArrayList<CompressedRow>();
    ArrayList<CompressedRow> aftermissed = new ArrayList<CompressedRow>();
    @Override
    public void handleBeforeLoaded(CompressedTable before) {
        log.info("Load before rows: "+before.size());
    }

    @Override
    public void handleAfterLoaded(CompressedTable after) {
        log.info("Load after rows: "+after.size());
    }

    @Override
    public void handleMissedInBefore(Set<KeyValue> entries) {
        log.info("Missed rows in before table: "+entries.size());
        entries.forEach(a->log.info(" -"+a));
    }

    @Override
    public void handleMissedInAfter(Set<KeyValue> entries) {
        log.info("Missed rows in after table: "+entries.size());
        entries.forEach(a->log.info(" -"+a));
    }

    @Override
    public void handleMissedBeforeHeader(List<String> headers) {
        log.info("Missed headers in before table "+headers.size());
    }

    @Override
    public void handleMissedAfterHeader(List<String> headers) {
        log.info("Missed headers in after table "+headers.size());
    }

    @Override
    public void handleMisMatched(ComparisonResult.RowResult mismatch) {
        log.info("Mismatched: "+mismatch.getMatchedKey());
    }

    @Override
    public void updateUnitedHeaders(List<String> unitedHeaders) {
        log.info("United Headers: "+unitedHeaders.size());
    }

    @Override
    public void handleUnitedHeaderMapping(Map<String, Integer> unitedHeadermapping) {

    }

    @Override
    public void handleUnitedHeadrMapping(Map<String, Integer> unitedHeadermapping) {

    }

    @Override
    public void handleMarkers(Map<String, Integer> markers) {
    }

    @Override
    public void handleMatched(KeyValue key) {
//        log.info("Matched: "+key);
    }

    @Override
    public void handleMatchedList(List<KeyValue> keys) {
        log.info("Matched list: "+keys.size());
    }

    @Override
    public void handleMisMatchedList(List<KeyValue> keys) {
        log.info("MisMatched list: "+keys.size());
    }

    @Override
    public void handleNotice(String key) {

    }

    @Override
    public void appendInformation() {

    }
}
