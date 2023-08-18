package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.ImmutableTable;
import net.zousys.compressedtable.Key;
import net.zousys.compressedtable.Row;

import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;

@NoArgsConstructor
public class CompressedTable implements ImmutableTable {
    private Map<String, Row> mapping = new HashMap<>();
    private List<Row> rows = new ArrayList<>();
    @Getter
    private String[] headers;
    private Map<String, Integer> headermapping = new HashMap<>();
    @Getter
    private String[] headerkeys;

    public void setHeaders(String[] headers) {
        this.headers = headers;
        int ind = 0;
        for (String header : headers) {
            headermapping.put(header, ind++);
        }
    }

    public void appendRow(List<String> fields) throws IOException {
        if (fields != null) {
            Optional<Row> ocr = CompressedRow.build(headerkeys, headermapping, fields);
            ocr.ifPresent(row -> {
                this.rows.add(row);
                mapping.put(row.getKey().toString(), row);
            });
        }
    }

    @Override
    public List<Row> getContents() {
        return rows;
    }


    @Override
    public Optional<Row> seekByKey(Key key) {
        return Optional.of(mapping.get(key));
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
