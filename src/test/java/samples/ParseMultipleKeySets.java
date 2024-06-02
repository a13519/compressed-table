package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParseMultipleKeySets {
    /**
     * Default the Table is compressed. This is to parse a CSV file to compressed table.
     *
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public  void parseMultipleKeySetsCSV() throws IOException, DataFormatException {
        CompareListener k = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"First Name", "Last Name"})
                        .addHeaders(new String[]{"Phone 1", "Phone 2", "Email"})
                )
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000b.csv"));
        k.handleBeforeLoaded(beforetable);
        System.out.println("Table size: " + beforetable.getContents().size() + " Headers: " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

        assertTrue(beforetable.getContents().size()==987);
        assertTrue(beforetable.getMode()== CompressedTableFactory.Mode.MULTI_KEYS);
        assertTrue(beforetable.isCompressed());

    }

}
