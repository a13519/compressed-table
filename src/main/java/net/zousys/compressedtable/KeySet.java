package net.zousys.compressedtable;

import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.List;
import java.util.Map;

/**
 * The table composit key. It composits from header map with fields list
 * This object is associated with Row
 */
public interface KeySet {
    String getNativeKeyValue();
    String getMainKeyValue();
    KeyValue getMatchedKeyValue();
    KeyValue getKeyValue(String key);
    String[] getKeyheaders(int index);
    int size();

}
