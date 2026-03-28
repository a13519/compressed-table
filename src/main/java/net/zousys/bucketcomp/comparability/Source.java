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
import java.util.*;

/**
 *
 */
public interface Source {

    /**
     * @throws IOException
     */
    public void bucketize() throws IOException ;

    /**
     *
     * @return
     */
    public int[] getKeyColumnIndices();

    /**
     *
     * @return
     */
    public Map<Integer, String> getIndex2columnMap();

    /**
     *
     * @return
     */
    public Map<String, Integer> getColumn2indexMap();

    /**
     * @return
     */
    public String getBucketDir() ;

    /**
     * @param bucket
     * @return
     */
    public String getBucketFile(int bucket) ;

    /**
     *
     * @return
     */
    public String[] getHeaders() ;

    /**
     *
     * @return
     */
    public String getSide();

    /**
     * @param fields
     * @param keyColumnIndices
     * @return
     */
    public static String produceKeys(String[] fields, int[] keyColumnIndices) {
        StringBuilder key = new StringBuilder("[");
        for (int idx : keyColumnIndices) {
            if (idx < fields.length && fields[idx] != null) {
                key.append(fields[idx].trim()).append("|");
            }
        }
        key.append("]");
        return key.toString();
    }

}
