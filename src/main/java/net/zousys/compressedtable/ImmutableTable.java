package net.zousys.compressedtable;

import java.util.List;
import java.util.Optional;

public interface ImmutableTable {
    List<Row> getContents();

    String[] getHeaders();

    Optional<Row> seekByKey(Key key);

    Optional<Row> seekByIndex(int index);

    int size();

    void setKeyHeaders(String[] keys);
}
