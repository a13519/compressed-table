package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

public class ParseAExcelFile {
    /**
     * This is to parse a Single Key Set CSV file to an un-compressed table
     *
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void parseExcelFile() throws IOException, DataFormatException {
        CompareListener listener = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("excel")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "Index"})
                        .addHeaders(new String[]{"Customer Id"})
                )
                .compressed(false)
                .headerPosition(0)
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000.xlsx"));
        listener.handleBeforeLoaded(beforetable);
        System.out.println("Table size: " + beforetable.getContents().size() + " Headers: " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

    }
}
