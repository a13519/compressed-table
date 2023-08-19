package net.zousys.compressedtable;

public interface Row {
    Key getKey();
    ImmutableTable getTable();
    Content getContent();
}
