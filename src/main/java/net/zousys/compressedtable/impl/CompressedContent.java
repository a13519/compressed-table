package net.zousys.compressedtable.impl;

import net.zousys.compressedtable.Content;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

public class CompressedContent extends CompressedByteArray implements Content {
    /**
     *
     */
    private CompressedContent() {
        super();
    }

    /**
     * @param fields
     * @return
     * @throws IOException
     *
     *
     */
    public static CompressedContent load(List<String> fields) throws IOException {
        CompressedContent compressedContent = new CompressedContent();
        StringWriter bw = new StringWriter();
        fields.forEach(field -> bw.write(field.trim() + "\n"));
        compressedContent.loadContent(String.valueOf(bw).getBytes());
        return compressedContent;
    }

    /**
     * @param fields
     * @return
     * @throws IOException
     */
    public static CompressedContent load(String[] fields) throws IOException {
        CompressedContent compressedContent = new CompressedContent();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Arrays.stream(fields).parallel().forEach(field -> bao.writeBytes(field.getBytes(StandardCharsets.UTF_8)));
        compressedContent.loadContent(bao.toByteArray());
        return compressedContent;
    }

    /**
     * @return
     * @throws DataFormatException
     * @throws IOException
     */
    @Override
    public List<String> form() throws DataFormatException, IOException {
        ByteArrayInputStream bao = new ByteArrayInputStream(decompress(super.getByteArray(), false));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(bao));
        ArrayList<String> r = new ArrayList<>();
        for (String line; (line = reader.readLine()) != null; ) {
            r.add(line);
        }
        return r;
    }

    /**
     * @return
     */
    @Override
    public long hash() {
        return getHash();
    }

}
