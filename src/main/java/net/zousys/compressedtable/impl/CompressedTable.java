package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zousys.compressedtable.GeneralTable;
import net.zousys.compressedtable.KeySet;
import net.zousys.compressedtable.key.KeyHeaders;
import net.zousys.compressedtable.Row;
import net.zousys.compressedtable.key.KeyHeadersList;
import net.zousys.compressedtable.key.KeyValue;

import java.io.IOException;
import java.util.*;

/**
 * No 'RETURN' or newLine char should be in data
 */
@NoArgsConstructor
public class CompressedTable implements GeneralTable {
    @Getter
    private Map<String, Map<String, Row>> keyedMappingMap = new HashMap<>();
    private Map<String, Row> mainKeyMap = new HashMap<>();

    private List<Row> rows = new ArrayList<>();
    @Getter
    private List<String> headers;
    @Setter
    @Getter
    private Map<String, Integer> headerMapping = new HashMap<>();
    private KeyHeadersList keyHeaderList = new KeyHeadersList();
    private boolean onHeader = true;
    private int headerRowNumber = -1;
    @Getter
    private int physicalLineNumber = 0;
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
            mainKeyMap.put(compressedRow.getKey().getMainKeyValue(), compressedRow);

            if (compressedRow.getKey() != null) {
                for (KeyHeaders akey : keyHeaderList.getKeyHeadersList()) {
                    Map<String, Row> akmap = keyedMappingMap.get(akey.getCompositedKeyValue());
                    if (akmap == null) {
                        akmap = new HashMap<>();
                        keyedMappingMap.put(akey.getCompositedKeyValue(), akmap);
                    }
                    akmap.put(compressedRow.getKey().getKeyValue(akey.getCompositedKeyValue()).getValue(), compressedRow);
                }
            }
        }
    }

    @Override
    public List<Row> getContents() {
        return rows;
    }


    @Override
    public Optional<Map<String, Row>> seekByKey(KeySet key) {
        if (key==null) {
            return Optional.of(null);
        } else {
            Map<String, Row> r = new HashMap<>();
            for (KeyHeaders ak : this.keyHeaderList.getKeyHeadersList()) {
                r.put(ak.getCompositedKeyValue(), keyedMappingMap.get(ak.getCompositedKeyValue()).get(key.getKeyValue(ak.getCompositedKeyValue())));
            }
            return Optional.of(r);
        }
    }

    @Override
    public Optional<Row> seekByMainKey(String keyValue) {
        if (keyHeaderList==null||keyHeaderList.getKeyHeadersList().size()==0) {
            return Optional.of(null);
        } else {
        Optional<Row> r = Optional.of(
                mainKeyMap.get(keyValue));
        return r;
        }
    }

    @Override
    public Optional<Row> seekByKey(KeyValue keyValue) {
        if (keyHeaderList==null||keyHeaderList.getKeyHeadersList().size()==0) {
            return Optional.of(null);
        } else {
            Optional<Row> r = Optional.of(keyedMappingMap.get(keyValue.getName()).get(keyValue.getValue()));
            return r;
        }
    }


    @Override
    public int size() {
        return rows.size();
    }

    @Override
    public void addKeyHeaders(KeyHeaders keyHeaders) {
        keyHeaderList.getKeyHeadersList().add(keyHeaders);
    }

    @Override
    public void setKeyHeaderList(KeyHeadersList keyHeaderList) {
        this.keyHeaderList = keyHeaderList;
    }

    @Override
    public KeyHeadersList getKeyHeaderList() {
        return keyHeaderList;
    }

    @Override
    public void removeRowByMainKey(String mainKey) {
        if (mainKey != null) {
            Row row = mainKeyMap.get(mainKey);
            if (row != null) {
                removeRow(row);
            }
        }
    }

    @Override
    public void removeRow(Row row) {
        if (row != null) {
            rows.remove(row);
            mainKeyMap.remove(row.getKey().getMainKeyValue());
            if (row.getKey() != null) {
                for (KeyHeaders akh : keyHeaderList.getKeyHeadersList()){
                    String kv = akh.getCompositedKeyValue();
                    keyedMappingMap.get(kv).remove(row.getKey().getKeyValue(kv));
                }
            }
        }
    }

    @Override
    public void removeRowsByMainKey(Collection<String> keys) {
        keys.forEach(this::removeRowByMainKey);
    }

    @Override
    public void removeRows(Collection<Row> rows) {
        rows.forEach(this::removeRow);
    }

    @Override
    public void sort(String[] headers) {

    }

}
