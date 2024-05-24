package net.zousys.compressedtable.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.Content;
import net.zousys.compressedtable.GeneralTable;
import net.zousys.compressedtable.Key;
import net.zousys.compressedtable.Row;
import net.zousys.compressedtable.key.StringKey;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CompressedRow implements Row {
    private StringKey stringKey ;
    private CompressedContent compressedContent;
    private CompressedTable compressedTable;

    public CompressedRow(CompressedTable compressedTable) {
        this.compressedTable = compressedTable;
    }

    /**
     *
     * @param fields
     * @throws IOException
     */
    public void make(List<String> fields) throws IOException {
        if (compressedTable != null && fields != null) {
            this.stringKey = StringKey.create(compressedTable.getKeyHeaderList(), fields, this);
            this.compressedContent = CompressedContent.load(fields);
        }
    }

    @Override
    public Key getKey() {
        return stringKey;
    }

    @Override
    public GeneralTable getTable() {
        return compressedTable;
    }

    @Override
    public Content getContent() {
        return compressedContent;
    }

    @Override
    public long hash() {
        return compressedContent.getHash();
    }
}
