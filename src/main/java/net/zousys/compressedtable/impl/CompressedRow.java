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
import java.util.zip.DataFormatException;

@NoArgsConstructor
@AllArgsConstructor

@Builder
public class CompressedRow implements Row {
    @Builder.Default
    private boolean compressed = true;
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
            this.compressedContent = CompressedContent.load(fields, compressedTable.isCompressed());
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
    public String getField(int index) throws DataFormatException, IOException {
        if (compressed){
            compressedContent.form();
        }
        return compressedContent.getField(index);
    }

    @Override
    public String getField(String header) throws IOException, DataFormatException {
        if (compressed){
            compressedContent.form();
        }
        if (header != null) {
            Integer ii = this.compressedTable.getHeaderMapping().get(header);
            if (ii!=null) {
                return compressedContent.getField(ii.intValue());
            }
        }
        return null;
    }

    @Override
    public long hash() {
        return compressedContent.getHash();
    }
}
