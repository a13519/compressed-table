package net.zousys.compressedtable;

import java.util.List;
import java.util.Map;

public interface Key {
    void setKey(String[] keyheaders, Map<String, Integer> map, List<String> fields);
}
