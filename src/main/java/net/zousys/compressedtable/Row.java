package net.zousys.compressedtable;

import java.io.IOException;
import java.util.zip.DataFormatException;

public interface Row {
    KeySet getKey();

    GeneralTable getTable();

    Content getContent();

    String getField(int index) throws DataFormatException, IOException;

    String getField(String header) throws IOException, DataFormatException ;

    long hash();
}
