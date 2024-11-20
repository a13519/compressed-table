package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.GeneralTable;
import net.zousys.compressedtable.KeySet;
import net.zousys.compressedtable.Row;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * No 'RETURN' or newLine char should be in data
 */
public class CompressedTable implements GeneralTable {
    @Getter
    private CompressedTableFactory.Mode mode;
    @Getter
    /**
     * The map of multiple keysets,
     * if only one key set is specified the mode will tagged as Single Key Set table
     * if multiple key sets are spcified the mode should be multi-key set table
     * The only map entry represents the main key if this is single key set
     */
    private KeyedMappingMap keyedMappingMap = new KeyedMappingMap();
    /**
     * the native key is to identify the entry by system time and hash code of the content
     */
    private Map<String, Row> nativeKeyMap = new HashMap<>();
    /**
     * The header lists of key sets
     * for Single Key Set, the only key set should be set that index of 0
     */
    private KeyHeadersList keyHeaderList = new KeyHeadersList();

    private List<Row> rows = new ArrayList<>();
    @Getter
    private List<String> headers;
    @Setter
    @Getter
    private Map<String, Integer> headerMapping = new HashMap<>();
    private boolean onHeader = true;
    @Setter
    @Getter
    private boolean compressed = true;
    private int headerRowNumber = -1;
    @Getter
    @Setter
    private int physicalLineNumber = 0;

    /**
     *
     * @param mode
     */
    public CompressedTable(CompressedTableFactory.Mode mode) {
        this.mode = mode;
    }

    /**
     *
     * @return
     */
    public int increasePhysicalLineNumber() {
        physicalLineNumber++;
        return physicalLineNumber;
    }
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
     * @param fields
     */
    public void setHeaders(List<String> fields) {
        headers = fields.stream().map(a->a.replaceAll("[\uFEFF-\uFEFF]", "")).collect(Collectors.toList());
        int ind = 0;
        for (String header : headers) {
            headerMapping.put(header, ind++);
        }
    }

    /**
     *
     * @param fields
     * @param isIncludeHeader
     * @throws IOException
     */
    public void appendRow(List<String> fields, boolean isIncludeHeader) throws IOException {
        if (isIncludeHeader && onHeader) {
            setHeaders(fields);
            onHeader = false;
        } else {
            appendRow(fields);
        }
    }

    /**
     *
     * @param fields
     * @throws IOException
     */
    public void appendRow(String[] fields) throws IOException {
        appendRow(Arrays.asList(fields));
    }

    /**
     *
     * @param fields
     * @param isIncludeHeader
     * @throws IOException
     */
    public void appendRow(String[] fields, boolean isIncludeHeader) throws IOException {
        appendRow(Arrays.asList(fields), isIncludeHeader);
    }

    /**
     *
     * @param fields
     * @throws IOException
     */
    public void appendRow(List<String> fields) throws IOException {
        fields = fields.stream().map(a->a.replaceAll("[\uFEFF-\uFEFF]", "")).collect(Collectors.toList());
        if (fields != null) {
            CompressedRow compressedRow = new CompressedRow(this);
            compressedRow.make(fields);

            this.rows.add(compressedRow);
            increasePhysicalLineNumber();
            nativeKeyMap.put(compressedRow.getKey().getNativeKeyValue(), compressedRow);

            if (compressedRow.getKey() != null) {
                if (mode == CompressedTableFactory.Mode.SINGLE_KEY) {
                    // single key set
                    keyedMappingMap.getMainKeyedMapping().put(compressedRow.getKey().getMainKeyValue(), compressedRow);
                } else {
                    for (KeyHeaders akey : keyHeaderList.getKeyHeadersList()) {
                        Map<String, Row> akmap = keyedMappingMap.get(akey.getCompositedKey());
                        if (akmap == null) {
                            akmap = new HashMap<>();
                            keyedMappingMap.put(akey.getCompositedKey(), akmap);
                        }
                        akmap.put(compressedRow.getKey().getKeyValue(akey.getCompositedKey()).getValue(), compressedRow);
                    }
                }
            }
        }
    }

    @Override
    public List<Row> getContents() {
        return rows;
    }

    @Override
    public Optional<Row> seekByNativeKey(String keyValue) {
        try {
            Optional<Row> r = Optional.of(
                    nativeKeyMap.get(keyValue));
            return r;
        } catch (Throwable t) {
            System.out.println("Optional record is null"+keyValue);
            return null;
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
    public Optional<Row> seekByMainKey(KeyValue keyValue) {
        if (keyHeaderList==null||keyHeaderList.getKeyHeadersList().size()==0) {
            return Optional.of(null);
        } else {
            Optional<Row> r = Optional.of(keyedMappingMap.getMainKeyedMapping().get(keyValue.getValue()));
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
    public void removeRowByNativeKey(KeyValue mainKey) {
        if (mainKey != null) {
            Row row = nativeKeyMap.get(mainKey.getValue());
            if (row != null) {
                removeRow(row);
            }
        }
    }

    @Override
    public void removeRowByKey(KeyValue key) {
        if (key != null) {
            Row row = this.keyedMappingMap.get(key.getName()).get(key.getValue());
            if (row != null) {
                removeRow(row);
            }
        }
    }

    @Override
    public void removeRowByMainKey(KeyValue key) {
        if (key != null) {
            Row row = this.keyedMappingMap.getMainKeyedMapping().get(key.getValue());
            if (row != null) {
                removeRow(row);
            }
        }
    }

    @Override
    public void removeRow(Row row) {
        if (row != null) {
            rows.remove(row);
            if (row.getKey() != null) {
                nativeKeyMap.remove(row.getKey().getNativeKeyValue());

                if (mode == CompressedTableFactory.Mode.SINGLE_KEY) {
                    keyedMappingMap.getMainKeyedMapping().remove(row.getKey().getMainKeyValue());
                } else {
                    for (KeyHeaders akh : keyHeaderList.getKeyHeadersList()){
                        String kv = akh.getCompositedKey();
                        keyedMappingMap.get(kv).remove(row.getKey().getKeyValue(kv));
                    }
                }
            }
        }
    }

    @Override
    public void removeRowsByNativeKey(Collection<KeyValue> keys) {
        keys.forEach(this::removeRowByNativeKey);
    }

    @Override
    public void removeRows(Collection<Row> rows) {
        rows.forEach(this::removeRow);
    }

    @Override
    public void sort(String[] headers) {

    }

}
