package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

@Log4j2
public class ParseAExcelFile {
    /**
     * This is to parse a Single Key Set CSV file to an un-compressed table
     * @param args
     * @throws IOException
     * @throws DataFormatException
     */
    public static void main(String[] args) throws IOException, DataFormatException {
        CompareListener listener = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("excel")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "Index"})
                        .addHeaders(new String[]{"Customer Id"})
                )
                .compressed(false)
                .headerPosition(0)
                .parse(Paths.get("customers-1000.xlsx")
                        .toAbsolutePath()
                        .toString());
        listener.handleBeforeLoaded(beforetable);
        log.info("Table size: " + beforetable.getContents().size() + " Headers: " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

    }
}
