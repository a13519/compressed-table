package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.ComparatorListener;
import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.impl.CompressedRow;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyValue;
import net.zousys.compressedtable.template.ComparisonTemplate;
import net.zousys.compressedtable.template.Styles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
public class CompareListenerInExcel implements ComparatorListener {
    ArrayList<CompressedRow> beforemissed = new ArrayList<CompressedRow>();
    ArrayList<CompressedRow> aftermissed = new ArrayList<CompressedRow>();

    private ComparisonResult comparisonResult;

    /**
     *
     */
    public CompareListenerInExcel(ComparisonResult comparisonResult) {
        this.comparisonResult = comparisonResult;
    }

    @Override
    public void handleBeforeLoaded(CompressedTable before) {
        log.info("Load before rows: " + before.size());
        comparisonResult.setBefore(before);
    }

    @Override
    public void handleAfterLoaded(CompressedTable after) {
        log.info("Load after rows: " + after.size());
        comparisonResult.setAfter(after);
    }

    @Override
    public void handleMissedInBefore(Set<KeyValue> entries) {
        log.info("Missed rows in before table: " + entries.size());
        entries.forEach(a -> comparisonResult.getBeforeMissed().add(a.getValue()));
    }

    @Override
    public void handleMissedInAfter(Set<KeyValue> entries) {
        log.info("Missed rows in after table: " + entries.size());
        entries.forEach(a -> comparisonResult.getAfterMissed().add(a.getValue()));
    }

    @Override
    public void handleMissedBeforeHeader(List<String> headers) {
        log.info("Missed headers in before table " + headers.size());
        comparisonResult.getBeforeMissedHeaders().addAll(headers);
    }

    @Override
    public void handleMissedAfterHeader(List<String> headers) {
        log.info("Missed headers in after table " + headers.size());
        comparisonResult.getAfterMissedHeaders().addAll(headers);
    }

    @Override
    public void handleMisMatched(ComparisonResult.RowResult mismatch) {
        log.info("Mismatched: " + mismatch.getMatchedKey() + " > " + mismatch.getFields().toString());
        comparisonResult.getMismatches().add(mismatch);
    }

    @Override
    public void updateUnitedHeaders(List<String> unitedHeaders) {
        log.info("United Headers: " + unitedHeaders.size());
        comparisonResult.setUnitedHeaders(unitedHeaders);
    }

    @Override
    public void handleUnitedHeaderMapping(Map<String, Integer> unitedHeadermapping) {

    }


    @Override
    public void handleMarkers(Map<String, Integer> markers) {
        comparisonResult.setMarkers(markers);
    }

    @Override
    public void handleMatched(KeyValue key) {
        log.info("Matched: "+key);
        comparisonResult.getMatched().add(key.getValue());
    }

    @Override
    public void handleMatchedList(List<KeyValue> keys) {
        log.info("Matched list: " + keys.size());
        comparisonResult.getMatched().clear();
        keys.forEach(a -> comparisonResult.getMatched().add(a.getValue()));
    }

    @Override
    public void handleMisMatchedList(List<KeyValue> keys) {
        log.info("MisMatched list: " + keys.size());
    }

    @Override
    public void handleNotice(String key) {

    }

    @Override
    public void appendInformation() {

    }

    @Override
    public void finished() {

        try {
            ComparisonTemplate template = new ComparisonTemplate(comparisonResult);
            template.setOutputStream(new FileOutputStream("/Users/songzou/Documents/IdeaProjects/compressed-table/build/xxx.xlsx"));
            template.setStyles(Styles.styles);
            template.save();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
