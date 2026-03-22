package net.zousys.bucketcomp.comparability;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@Log4j2
public class BucketComparator {

    /**
     * @param bucket
     * @param comparatorContext
     * @throws Exception
     */
    public final static void compare(int bucket, ComparatorContext comparatorContext) throws Exception {
        if (!Files.exists(Paths.get(comparatorContext.getBeforeSource().getBucketFile(bucket)))
                && !Files.exists(Paths.get(comparatorContext.getAfterSource().getBucketFile(bucket)))) {
            return;
        }

        Map<String, String[]> beforeMap = loadBucket(
                comparatorContext.getBeforeSource().getBucketFile(bucket),
                comparatorContext.getBeforeSource().getKeyColumnIndices(),
                comparatorContext.getConfig());
        Map<String, String[]> afterMap = loadBucket(
                comparatorContext.getAfterSource().getBucketFile(bucket),
                comparatorContext.getAfterSource().getKeyColumnIndices(),
                comparatorContext.getConfig());

        if (beforeMap.isEmpty() && afterMap.isEmpty()) return;

        Set<String> keysBefore = beforeMap.keySet();
        Set<String> keysAfter = afterMap.keySet();

        AtomicInteger mmcount = new AtomicInteger();
        AtomicInteger mcount = new AtomicInteger();
        AtomicInteger bmisscount = new AtomicInteger();
        AtomicInteger amisscount = new AtomicInteger();

        // Deleted
        keysBefore.stream()
                .filter(k -> !keysAfter.contains(k))
                .forEach(k -> {
                    comparatorContext.getListener().handleMissedInAfter(k);
                    amisscount.incrementAndGet();
                });

        // Added
        keysAfter.stream()
                .filter(k -> !keysBefore.contains(k))
                .forEach(k -> {
                    comparatorContext.getListener().handleMissedInBefore(k);
                    bmisscount.incrementAndGet();
                });

        // Mismatches
        keysBefore.stream()
                .filter(keysAfter::contains)
                .forEach(key -> {
                    String[] b = beforeMap.get(key);
                    String[] a = afterMap.get(key);
                    boolean mm = false;

                    for (int commonColNo = 0; commonColNo < comparatorContext.getColumnStructure().getCommonComparableColumnNumber(); commonColNo++) {
                        int beforeindex = comparatorContext.getColumnStructure().getBeforeCommonComparableColumnIndexes().get(commonColNo);
                        int afterindex = comparatorContext.getColumnStructure().getAfterCommonComparableColumnIndexes().get(commonColNo);

                        if (!Objects.equals(b[beforeindex], a[afterindex])) {
                            comparatorContext.getListener().handleMisMatched(key, FieldResult.builder()
                                    .beforeField(b[beforeindex])
                                    .afterField(a[afterindex])
                                    .beforeColumnIndex(beforeindex)
                                    .afterColumnIndex(afterindex)
                                    .name(comparatorContext.getBeforeSource().getHeader(beforeindex))
                                    .missmatched(true)
                                    .ignored(false)
                                    .build());
                            mm = true;
                        } else {
                            comparatorContext.getListener().handleMatched(key, b);
                        }
                    }
                    if (mm) {
                        mmcount.getAndIncrement();
                    } else {
                        mcount.getAndIncrement();
                    }
                });
        log.info("bucket " + bucket + " bmisscount: " + bmisscount.get());
        log.info("bucket " + bucket + " amisscount: " + amisscount.get());
        log.info("bucket " + bucket + " mmcount: " + mmcount.get());
        log.info("bucket " + bucket + " mcount: " + mcount.get());
    }

    /**
     * @param path
     * @param keyColumnIndices
     * @param config
     * @return combined key to fields value
     * @throws Exception
     */
    private final static Map<String, String[]> loadBucket(String path, int[] keyColumnIndices, CompConfig config) throws Exception {
        if (!Files.exists(Paths.get(path))) return Collections.emptyMap();

        Map<String, String[]> map = new HashMap<>();

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(config.getDelimeter());
        if (config.getQuote() != 0) {
            settings.getFormat().setQuote(config.getQuote());
        }
        if (config.getEscape() != 0) {
            settings.getFormat().setQuoteEscape(config.getEscape());
        }
        settings.setHeaderExtractionEnabled(false); // no header after bucketlizing
        settings.setSkipEmptyLines(config.isSkipEmptyLines());

        settings.setProcessor(new RowProcessor() {
            @Override
            public void rowProcessed(String[] row, ParsingContext context) {
                if (row == null || row.length == 0) return;

                String key = Source.produceKeys(row, keyColumnIndices);

                map.put(key, row.clone());
            }

            @Override
            public void processStarted(ParsingContext context) {
            }

            @Override
            public void processEnded(ParsingContext context) {
            }
        });

        CsvParser parser = new CsvParser(settings);
        try (Reader reader = new BufferedReader(new FileReader(path))) {
            parser.parse(reader);
        }

        return map;
    }

}
