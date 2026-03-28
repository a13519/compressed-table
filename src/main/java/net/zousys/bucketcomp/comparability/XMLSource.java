package net.zousys.bucketcomp.comparability;

import java.io.IOException;
import java.util.Map;

/**
 * TODO
 */
public class XMLSource implements Source{
    @Override
    public void bucketize() throws IOException {

    }

    @Override
    public int[] getKeyColumnIndices() {
        return new int[0];
    }

    @Override
    public Map<Integer, String> getIndex2columnMap() {
        return Map.of();
    }

    @Override
    public Map<String, Integer> getColumn2indexMap() {
        return Map.of();
    }

    @Override
    public String getBucketDir() {
        return "";
    }

    @Override
    public String getBucketFile(int bucket) {
        return "";
    }

    @Override
    public String[] getHeaders() {
        return new String[0];
    }

    @Override
    public String getSide() {
        return "";
    }
}
