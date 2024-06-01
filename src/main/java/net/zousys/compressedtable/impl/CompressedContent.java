package net.zousys.compressedtable.impl;

import net.zousys.compressedtable.Content;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 *
 */
public class CompressedContent extends CompressedByteArray implements Content {
    private List<String> fields;
    private boolean compressed = true;

    /**
     *
     */
    private CompressedContent(boolean compressed) {
        super();
        this.compressed = compressed;
    }

    /**
     *
     * @param fields
     * @param compressed
     * @return
     * @throws IOException
     */
    public static CompressedContent load(List<String> fields, boolean compressed) throws IOException {
        CompressedContent compressedContent = new CompressedContent(compressed);
        if (compressed) {
            StringWriter bw = new StringWriter();
            fields.forEach(field -> bw.write(field.trim() + "\n"));
            compressedContent.loadContent(String.valueOf(bw).getBytes());
        } else {
            compressedContent.fields = new ArrayList<>();
            compressedContent.fields.addAll(fields);
        }
        return compressedContent;
    }

    /**
     *
     * @param fields
     * @param compressed
     * @return
     * @throws IOException
     */
    public static CompressedContent load(String[] fields, boolean compressed) throws IOException {
        CompressedContent compressedContent = new CompressedContent(compressed);
        if (compressed) {
            StringWriter bw = new StringWriter();
            Arrays.stream(fields).forEach(field -> bw.write(field.trim() + "\n"));
            compressedContent.loadContent(String.valueOf(bw).getBytes());
        } else {
            compressedContent.fields = new ArrayList<>();
            compressedContent.fields.addAll(Arrays.asList(fields));
        }
        return compressedContent;
    }

    /**
     * @return
     * @throws DataFormatException
     * @throws IOException
     */
    @Override
    public List<String> form() throws DataFormatException, IOException {
        if (compressed) {
            ByteArrayInputStream bao = new ByteArrayInputStream(decompress(super.getByteArray(), false));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(bao));
            ArrayList<String> r = new ArrayList<>();
            for (String line; (line = reader.readLine()) != null; ) {
                r.add(line);
            }
            return r;
        } else {
            return fields;
        }
    }

    /**
     * @return
     */
    @Override
    public long hash() {
        return getHash();
    }

}
