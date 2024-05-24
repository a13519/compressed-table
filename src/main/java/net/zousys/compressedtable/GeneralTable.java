package net.zousys.compressedtable;

import net.zousys.compressedtable.key.KeyHeaders;
import net.zousys.compressedtable.key.KeyValue;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GeneralTable {
    List<Row> getContents();

    List<String> getHeaders();

    int getHeaderRowNumber();

    Optional<Map<String, Row>> seekByKey(Key key);

    Optional<Row> seekByMainKey(String keyValue);

    Optional<Row> seekByKey(KeyValue keyValue);

    int size();

    void addKeyHeaders(KeyHeaders keyHeaders);

    void setKeyHeaderList(List<KeyHeaders> keyHeaderList);

    List<KeyHeaders> getKeyHeaderList();

    void removeRowByMainKey(KeyValue key);

    void removeRow(Row row);

    void removeRowsByMainKey(Collection<KeyValue> keys);

    void removeRows(Collection<Row> rows);

    void sort(String[] headers);

    void setHeaderRowNumber(int no);

}
