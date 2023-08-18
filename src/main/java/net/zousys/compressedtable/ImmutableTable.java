package net.zousys.compressedtable;

import java.util.List;
import java.util.Optional;

public interface ImmutableTable {
    public List<Row> getContents();
    public String[] getHeaders();
    public Optional<Row> seekByKey(Key key);
    public Optional<Row> seekByIndex(int index);
    public int size();
    public void setKeyHeaders(String[] keys);
}
