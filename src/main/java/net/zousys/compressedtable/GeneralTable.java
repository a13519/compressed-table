package net.zousys.compressedtable;

import net.zousys.compressedtable.impl.KeyHeaders;
import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GeneralTable {
    List<Row> getContents();

    List<String> getHeaders();

    int getHeaderRowNumber();

    Optional<Row> seekByNativeKey(String keyValue);

    Optional<Row> seekByMainKey(KeyValue keyValue);

    Optional<Row> seekByKey(KeyValue keyValue);

    int size();

    void addKeyHeaders(KeyHeaders keyHeaders);

    void setKeyHeaderList(KeyHeadersList keyHeaderList);

    KeyHeadersList getKeyHeaderList();

    void removeRowByNativeKey(KeyValue key);

    void removeRow(Row row);

    void removeRowByKey(KeyValue key);

    void removeRowByMainKey(KeyValue key);

    void removeRowsByNativeKey(Collection<KeyValue> keys);

    void removeRows(Collection<Row> rows);

    void sort(String[] headers);

    void setHeaderRowNumber(int no);

    Map<String, Integer> getHeaderMapping();

    boolean isCompressed();
}
