package net.zousys.compressedtable;

import java.util.List;
import java.util.Map;

/**
 * The table composit key. It composits from header map with fields list
 */
public interface Key {
    String getMainKey();
    String[] getKeys();
    String getKey(int index);
    String[] getKeyheaders(int index);
    int size();
    void cast(List<String> fields, Map<String, Integer> headerMapping);
}
