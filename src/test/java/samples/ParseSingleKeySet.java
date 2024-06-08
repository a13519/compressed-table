package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParseSingleKeySet {
    /**
     * This is to parse a Single Key Set CSV file to an un-compressed table
     *
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void parseSingleKeySet() throws IOException, DataFormatException {
        CompareListener listener = new CompareListener();

        Stack s = new Stack();
        s.push("1");
        s.push("2");

        System.out.println(s.firstElement());
s.remove(0);
        System.out.println(s.firstElement());
        s.remove(0);

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(false)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000b.csv"));
        listener.handleBeforeLoaded(beforetable);
        System.out.println("Table size: " + beforetable.getContents().size() + " Headers: " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

        assertTrue(beforetable.getContents().size()==987);
        assertTrue(beforetable.getMode()== CompressedTableFactory.Mode.SINGLE_KEY);
        assertFalse(beforetable.isCompressed());
    }
}
