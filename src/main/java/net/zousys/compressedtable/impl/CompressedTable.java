package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zousys.compressedtable.ImmutableTable;
import net.zousys.compressedtable.Key;
import net.zousys.compressedtable.Row;

import java.io.IOException;
import java.util.*;

@NoArgsConstructor
public class CompressedTable implements ImmutableTable {
    @Getter
    private Map<String, Row> keyedMapping = new HashMap<>();
    private List<Row> rows = new ArrayList<>();
    @Getter
    private String[] headers;
    @Setter
    @Getter
    private Map<String, Integer> headerMapping = new HashMap<>();
    @Getter
    private String[] headerkeys;
    private boolean onHeader = true;

    public void setHeaders(String[] headers) {
        this.headers = headers;
        int ind = 0;
        for (String header : headers) {
            headerMapping.put(header, ind++);
        }
    }

    public void appendRow(List<String> fields, boolean isIncludeHeader) throws IOException {
        if (isIncludeHeader && onHeader) {
            setHeaders(fields.toArray(new String[]{}));
            onHeader = false;
        } else {
            appendRow(fields);
        }
    }

    public void appendRow(String[] fields) throws IOException {
        appendRow(Arrays.asList(fields));
    }

    public void appendRow(String[] fields, boolean isIncludeHeader) throws IOException {
        appendRow(Arrays.asList(fields), isIncludeHeader);
    }

    public void appendRow(List<String> fields) throws IOException {
        if (fields != null) {
            CompressedRow compressedRow = new CompressedRow(this);
            compressedRow.make(fields);
            this.rows.add(compressedRow);
            if (compressedRow.getKey() != null) {
                    keyedMapping.put(compressedRow.getKey().toString(), compressedRow);
            }
        }
    }

    @Override
    public List<Row> getContents() {
        return rows;
    }


    @Override
    public Optional<Row> seekByKey(Key key) {
        return Optional.of(keyedMapping.get(key));
    }

    @Override
    public Optional<Row> seekByIndex(int index) {
        return Optional.of(rows.get(index));
    }

    @Override
    public int size() {
        return rows.size();
    }

    @Override
    public void setKeyHeaders(String[] keys) {
        this.headerkeys = keys;
    }

}
