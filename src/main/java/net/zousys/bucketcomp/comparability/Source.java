package net.zousys.bucketcomp.comparability;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Getter
@Log4j2
public class Source {

    private CompConfig config;
    private BufferedReader bufferedReader;
    private String side;
    private Map<String, Integer> column2indexMap = new HashMap<>();
    private Map<Integer, String> index2columnMap = new HashMap<>();
    private String[] headers;
    private int[] keyColumnIndices;
    /**
     *
     * @param side
     * @param config
     * @param bufferedReader
     */
    public Source(String side, CompConfig config, BufferedReader bufferedReader) throws IOException {
        this.config = config;
        this.side = side;
        this.bufferedReader = bufferedReader;
        log.info("Bucketing "+side+" file...");
        bucketize();
    }

    /**
     *
     * @throws IOException
     */
    public void bucketize() throws IOException {
        Files.createDirectories(Paths.get(getBucketDir()));

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setDelimiter(config.getDelimeter());
        if (config.getQuote() != 0) {
            settings.getFormat().setQuote(config.getQuote());
        }
        if (config.getEscape() != 0) {
            settings.getFormat().setQuoteEscape(config.getEscape());
        }
        settings.setHeaderExtractionEnabled(config.isExtractHeader());
        settings.setSkipEmptyLines(config.isSkipEmptyLines());

        CsvWriterSettings writerSettings = new CsvWriterSettings();
        writerSettings.getFormat().setDelimiter(config.getDelimeter());
        writerSettings.getFormat().setQuote(config.getQuote());
        writerSettings.getFormat().setQuoteEscape(config.getEscape());
        writerSettings.setHeaderWritingEnabled(config.isExtractHeader());

        Map<Integer, CsvWriter> writers = new HashMap<>();

        analyzeKeys();

        settings.setProcessor(new RowProcessor() {
            @Override
            public void rowProcessed(String[] row, ParsingContext context) {
                if (row == null || row.length == 0) return;

                String key = produceKeys(row, keyColumnIndices);

                int bucket = Math.abs(key.hashCode()) % config.getBucketNumber();

                CsvWriter writer = writers.computeIfAbsent(bucket, b -> {
                    try {
                        return new CsvWriter(new FileWriter(getBucketFile(b)), writerSettings);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                writer.writeRow(row);
            }

            @Override
            public void processStarted(ParsingContext context) {
            }

            @Override
            public void processEnded(ParsingContext context) {
                for (CsvWriter w : writers.values()) {
                    try {
                        w.flush();
                        w.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        CsvParser parser = new CsvParser(settings);
        parser.parse(bufferedReader);
    }

    /**
     * @param bufferedReader
     * @param delimeterre
     * @return
     * @throws IOException
     */
    private final static String[] getHeaders(BufferedReader bufferedReader, String delimeterre) throws IOException {
        String line = bufferedReader.readLine();           // returns null if file is empty
        String[] tokens = line.split(delimeterre);
        return tokens;
    }

    /**
     *
     * @throws IOException
     */
    private void analyzeKeys() throws IOException {
        headers = getHeaders(bufferedReader, ""+config.getDelimeter());

        List<Integer> indices = new ArrayList<>();
        int index = 0;
        for (String header : headers) {
            column2indexMap.put(header.trim(), index);
            index2columnMap.put(index, header.trim());
            index ++;
        }

        if (config.getKeys() != null && config.getKeys().size() > 0) {
            for (String key : config.getKeys()) {
                Integer idx = column2indexMap.get(key.trim());
                if (idx != null) {
                    indices.add(idx);
                }
            }
            keyColumnIndices = indices.stream().mapToInt(i -> i).toArray();
        }
    }

    /**
     *
     * @param fields
     * @param keyColumnIndices
     * @return
     */
    public final static String produceKeys(String[] fields, int[] keyColumnIndices) {
        StringBuilder key = new StringBuilder("[");
        for (int idx : keyColumnIndices) {
            if (idx < fields.length && fields[idx] != null) {
                key.append(fields[idx].trim()).append("|");
            }
        }
        key.append("]");
        return key.toString();
    }

    /**
     *
     * @return
     */
    public final String getBucketDir() {
        return config.getBucket() + "/"+getSide()+"/";
    }

    /**
     *
     * @param bucket
     * @return
     */
    public final String getBucketFile(int bucket) {
        return getBucketDir() + bucket + ".csv";
    }

    /**
     *
     * @param index
     * @return
     */
    public final String getHeader(int index) {
        return index2columnMap.get(index);
    }
}
