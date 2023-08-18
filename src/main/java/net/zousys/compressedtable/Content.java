package net.zousys.compressedtable;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

public interface Content {
    public List<String> form() throws DataFormatException, IOException;
    public byte[] getByteArray();
    public float getCompressionRatio();
}
