package net.zousys.compressedtable;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface Content {
    List<String> form() throws DataFormatException, IOException;

    byte[] getByteArray();

    float getCompressionRatio();

    long hash();
}
