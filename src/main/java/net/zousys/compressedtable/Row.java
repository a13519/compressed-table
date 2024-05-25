package net.zousys.compressedtable;

public interface Row {
    KeySet getKey();

    GeneralTable getTable();

    Content getContent();

    long hash();
}
