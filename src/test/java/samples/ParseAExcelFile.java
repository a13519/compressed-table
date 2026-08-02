package samples;

import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.Row;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.template.CompareListener;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void parseExcelFileWithEmptyLines() throws IOException, DataFormatException {
        CompareListener listener = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("excel")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "Index"})
                        .addHeaders(new String[]{"Customer Id"})
                )
                .compressed(false)
                .ignoreEmptyLines(true)
                .headerPosition(0)
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-empty.xlsx"));
        listener.handleBeforeLoaded(beforetable);
        System.out.println("Table size: " + beforetable.getContents().size() + " Headers: " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());
        assertTrue(beforetable.getContents().size()==15);

        beforetable = CompressedTableFactory
                .build("excel")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "Index"})
                        .addHeaders(new String[]{"Customer Id"})
                )
                .compressed(false)
                .ignoreEmptyLines(false)
                .headerPosition(0)
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-empty.xlsx"));
        listener.handleBeforeLoaded(beforetable);
        System.out.println("Table size: " + beforetable.getContents().size() + " Headers: " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());
        assertTrue(beforetable.getContents().size()==17);
    }


}
