package net.zousys.compressedtable;

import net.zousys.compressedtable.key.KeyValue;

import java.util.List;
import java.util.Map;

/**
 * The table composit key. It composits from header map with fields list
 */
public interface KeySet {
    String getMainKeyValue();
    KeyValue getMatchedKeyValue();
    KeyValue getKeyValue(String key);
    String[] getKeyheaders(int index);
    int size();
    void cast(List<String> fields, Map<String, Integer> headerMapping, String mainkey);
}
