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

    Optional<Map<String, Row>> seekByKey(KeySet key);

    Optional<Row> seekByMainKey(String keyValue);

    Optional<Row> seekByKey(KeyValue keyValue);

    int size();

    void addKeyHeaders(KeyHeaders keyHeaders);

    void setKeyHeaderList(KeyHeadersList keyHeaderList);

    KeyHeadersList getKeyHeaderList();

    void removeRowByMainKey(String key);

    void removeRow(Row row);

    void removeRowsByMainKey(Collection<String> keys);

    void removeRows(Collection<Row> rows);

    void sort(String[] headers);

    void setHeaderRowNumber(int no);

}
