package net.zousys.compressedtable.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.zousys.compressedtable.Content;
import net.zousys.compressedtable.Key;
import net.zousys.compressedtable.Row;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompressedRow implements Row {
    private StringKey stringKey;
    private CompressedContent compressedContent;

    public static Optional<Row> build(String[] headerkeys, Map<String, Integer> headermapping, List<String> fields) throws IOException {
        StringKey k = null;
        if (headerkeys != null && headermapping != null && fields != null) {
            k = new StringKey();
            k.setKey(headerkeys, headermapping, fields);
        }
        CompressedContent compressedContent = CompressedContent.load(fields);
        CompressedRow compressedRow = new CompressedRow(k, compressedContent);
        return Optional.of(compressedRow);
    }

    @Override
    public Key getKey() {
        return stringKey;
    }

    @Override
    public Content getContent() {
        return compressedContent;
    }
}
