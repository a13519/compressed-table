package net.zousys.compressedtable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DataFormatException;

public interface TableSerialier {

    public void serialize(OutputStream output) throws IOException, DataFormatException;
}
