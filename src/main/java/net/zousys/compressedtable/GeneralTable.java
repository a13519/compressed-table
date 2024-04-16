package net.zousys.compressedtable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GeneralTable {
    List<Row> getContents();

    List<String> getHeaders();

    Optional<Row> seekByKey(Key key);

    Optional<Row> seekByKey(String key);

    Optional<Row> seekByIndex(int index);

    int size();

    void setKeyHeaders(String[] keys);

    void removeRowByKey(String key);

    void removeRow(Row row);

    void removeRowsByKey(Collection<String> keys);

    void removeRows(Collection<Row> rows);
}
