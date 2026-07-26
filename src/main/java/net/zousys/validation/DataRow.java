package net.zousys.validation;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DataRow {

    private String tableName;

    private long rowNumber;

    private String primaryKey;

    private Map<String, String> values = new HashMap<String, String>();

    public String get(String column) {
        return values.get(column);
    }

    // getters/setters
}
