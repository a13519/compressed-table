package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zousys.compressedtable.GeneralTable;
import net.zousys.compressedtable.Key;
import net.zousys.compressedtable.Row;

import java.io.IOException;
import java.util.*;

/**
 * No 'RETURN' or newLine char should be in data
 */
@NoArgsConstructor
public class CompressedTable implements GeneralTable {
    @Getter
    private Map<String, Row> keyedMapping = new HashMap<>();
    private List<Row> rows = new ArrayList<>();
    @Getter
    private List<String> headers;
    @Setter
    @Getter
    private Map<String, Integer> headerMapping = new HashMap<>();
    @Getter
    private String[] headerkeys;
    private boolean onHeader = true;
    private int headerRowNumber = -1;
    /**
     *
     * @param no
     */
    public void setHeaderRowNumber(int no){
        this.headerRowNumber = no;
    }

    /**
     *
     * @return
     */
    @Override
    public int getHeaderRowNumber() {
        return headerRowNumber;
    }

    /**
     *
     * @param headers
     */
    public void setHeaders(String[] headers) {
        this.headers = List.of(headers);
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
    public Optional<Row> seekByKey(String key) {
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

    @Override
    public void removeRowByKey(String key) {
        if (key != null) {
            Row row = keyedMapping.remove(key);
            if (row != null) {
                rows.remove(row);
            }
        }
    }

    @Override
    public void removeRow(Row row) {
        if (row != null) {
            rows.remove(row);
            if (row.getKey() != null) {
                keyedMapping.remove(row.getKey());
            }
        }
    }

    @Override
    public void removeRowsByKey(Collection<String> keys) {
        keys.forEach(this::removeRowByKey);
    }

    @Override
    public void removeRows(Collection<Row> rows) {
        rows.forEach(this::removeRow);
    }

    @Override
    public void sort(String[] headers) {

    }

}
