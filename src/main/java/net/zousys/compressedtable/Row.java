package net.zousys.compressedtable;

public interface Row {
    Key getKey();
    GeneralTable getTable();
    Content getContent();

    long hash();
}
