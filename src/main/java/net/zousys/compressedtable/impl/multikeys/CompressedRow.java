package net.zousys.compressedtable.impl.multikeys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.Content;
import net.zousys.compressedtable.GeneralTable;
import net.zousys.compressedtable.KeySet;
import net.zousys.compressedtable.Row;
import net.zousys.compressedtable.impl.CompressedContent;
import net.zousys.compressedtable.impl.multikeys.key.MultiStringKey;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CompressedRow implements Row {
    private MultiStringKey multiStringKey;
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
            this.compressedContent = CompressedContent.load(fields);
            this.multiStringKey = MultiStringKey.create(compressedTable.getKeyHeaderList(), fields, this);
        }
    }

    @Override
    public KeySet getKey() {
        return multiStringKey;
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
