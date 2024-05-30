package net.zousys.compressedtable.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.*;
import net.zousys.compressedtable.impl.multikeys.key.MultiStringKey;
import net.zousys.compressedtable.impl.singlekey.key.SingleStringKey;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Builder
public class CompressedRow implements Row {
    private KeySet stringKey;
    private CompressedContent compressedContent;
    private CompressedTable compressedTable;

    /**
     *
     * @param compressedTable
     */
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
            this.compressedContent = CompressedContent.load(fields);
            if (compressedTable.getMode()== CompressedTableFactory.Mode.MULTI_KEYS) {
                this.stringKey = MultiStringKey.create(compressedTable.getKeyHeaderList(), fields, this);
            } else {
                this.stringKey = SingleStringKey.create(compressedTable.getKeyHeaderList(), fields, this);
            }
        }
    }

    @Override
    public KeySet getKey() {
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
