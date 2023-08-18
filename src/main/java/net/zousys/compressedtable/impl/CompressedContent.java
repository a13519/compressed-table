package net.zousys.compressedtable.impl;

import net.zousys.compressedtable.Content;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

public class CompressedContent extends CompressedByteArray implements Content {

    private CompressedContent(){
        super();
    }
    public static CompressedContent load(List<String> fields) throws IOException {
        CompressedContent compressedContent = new CompressedContent();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        fields.stream().forEach(field -> bao.writeBytes(field.getBytes(StandardCharsets.UTF_8)));
        compressedContent.loadContent(bao.toByteArray());
        return compressedContent;
    }
    public static CompressedContent load(String[] fields) throws IOException {
        CompressedContent compressedContent = new CompressedContent();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Arrays.stream(fields).parallel().forEach(field -> bao.writeBytes(field.getBytes(StandardCharsets.UTF_8)));
        compressedContent.loadContent(bao.toByteArray());
        return compressedContent;
    }
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

}
