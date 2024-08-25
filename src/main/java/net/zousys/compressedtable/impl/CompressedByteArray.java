package net.zousys.compressedtable.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@NoArgsConstructor
public class CompressedByteArray {
    private byte[] bytearray;
    private long beforesize;
    private long aftersize;
    @Getter
    @Setter
    protected long hash;

    public void loadContent(byte[] bytearray) throws IOException {
        compress(bytearray);
    }

    public void loadContent(String string) throws IOException {
        compress(string.getBytes(StandardCharsets.UTF_8));
    }

    protected void hashIt() {
        this.hash = java.util.Arrays.hashCode(bytearray);
    }

    private void compress(byte[] bytearray) throws IOException {
        if (bytearray == null) {
            return;
        }
        this.beforesize = bytearray.length;
        this.bytearray = compress(bytearray, Deflater.BEST_COMPRESSION, false);
        this.aftersize = this.bytearray.length;
    }

    protected void clean() {
        this.beforesize = 0;
        this.aftersize = 0;
        this.bytearray = null;
    }

    public byte[] formBytes() throws DataFormatException, IOException {
        if (bytearray == null) {
            return null;
        } else {
            return decompress(this.bytearray, false);
        }
    }

    public byte[] getByteArray() {
        return this.bytearray;
    }

    public float getCompressionRatio() {
        return (float) aftersize / beforesize;
    }


    public static byte[] compress(byte[] input, int compressionLevel,
                                  boolean GZIPFormat) throws IOException {
        Deflater compressor = new Deflater(compressionLevel, GZIPFormat);
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] readBuffer = new byte[1024];
        int readCount = 0;
        while (!compressor.finished()) {
            readCount = compressor.deflate(readBuffer);
            if (readCount > 0) {
                bao.write(readBuffer, 0, readCount);
            }
        }
        compressor.end();
        return bao.toByteArray();
    }

    public static byte[] decompress(byte[] input, boolean GZIPFormat)
            throws IOException, DataFormatException {
        Inflater decompressor = new Inflater(GZIPFormat);
        decompressor.setInput(input);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] readBuffer = new byte[1024];
        int readCount = 0;
        while (!decompressor.finished()) {
            readCount = decompressor.inflate(readBuffer);
            if (readCount > 0) {
                bao.write(readBuffer, 0, readCount);
            }
        }
        decompressor.end();
        return bao.toByteArray();
    }

}
