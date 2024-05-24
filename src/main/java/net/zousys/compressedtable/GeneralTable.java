package net.zousys.compressedtable;

import net.zousys.compressedtable.key.KeyHeaders;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GeneralTable {
    List<Row> getContents();

    List<String> getHeaders();

    Optional<Map<String, Row>> seekByKey(Key key);

    Optional<Row> seekByMainKey(String keyValue);

    int size();

    void addKeyHeaders(KeyHeaders keyHeaders);

    void setKeyHeaderList(List<KeyHeaders> keyHeaderList);

    List<KeyHeaders> getKeyHeaderList();

    void removeRowByMainKey(String key);

    void removeRow(Row row);

    void removeRowsByMainKey(Collection<String> keys);

    void removeRows(Collection<Row> rows);

    void sort(String[] headers);

    void setHeaderRowNumber(int no);
    int getHeaderRowNumber();
}
