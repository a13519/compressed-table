package net.zousys.compressedtable;

import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.List;
import java.util.Map;

/**
 * The table composit key. It composits from header map with fields list
 */
public interface KeySet {
    String getMainKey();
    String getMainKeyValue();
    KeyValue getMatchedKeyValue();
    KeyValue getKeyValue(String key);
    String[] getKeyheaders(int index);
    int size();
    // for multi keys
    void cast(List<String> fields, Map<String, Integer> headerMapping, String mainKeyValue);
    // for single key
    void cast(List<String> fields, Map<String, Integer> headerMapping, KeyHeadersList keyHeaderList);
}
