package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.CompressedComparatorFactory;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.DataFormatException;

public class CompareTwoTables {
    /**
     *
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void compare() throws IOException, DataFormatException {
        CompareListener listener = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(true)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000b.csv"));
        listener.handleBeforeLoaded(beforetable);
        System.out.println("Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

        CompressedTable aftertable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(true)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000a.csv"));
        listener.handleAfterLoaded(aftertable);
        System.out.println("After size: " + aftertable.getContents().size() + " " + aftertable.getHeaders() + " Mode: " + beforetable.getMode());

        CompressedComparatorFactory.builder()
                .before(beforetable)
                .after(aftertable)
                .comparatorListener(listener)
                .ignoredFields(new HashSet(Arrays.asList(new String[]{})))
                .strictMissed(true)
                .build().create()
                .compare();

    }

}
