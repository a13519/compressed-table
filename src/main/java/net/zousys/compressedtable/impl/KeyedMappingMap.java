package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.Row;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
public class KeyedMappingMap {
    private Map<String, Map<String, Row>> keyedMappingMap = new HashMap<>();
    private Map<String, Row> mainKeyedMapping = new HashMap<>();

    public Map<String, Row> get(String key) {
        return getKeyedMappingMap().get(key);
    }

    public Map<String, Row> put(String key, Map<String, Row> value) {
        return getKeyedMappingMap().put(key, value);
    }
}
