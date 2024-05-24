package net.zousys.compressedtable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GeneralTable {
    List<Row> getContents();

    List<String> getHeaders();

    Optional<Row> seekByMainKey(String key);

    Optional<Row> seekByIndex(int index);

    int size();

    void addKeyHeaders(String[] keyHeaders);

    void setKeyHeaderList(List<String[]> keyHeaderList);

    List<String[]> getKeyHeaderList();

    void removeRowByMainKey(String key);

    void removeRow(Row row);

    void removeRowsByMainKey(Collection<String> keys);

    void removeRows(Collection<Row> rows);

    void sort(String[] headers);

    void setHeaderRowNumber(int no);
    int getHeaderRowNumber();
}
