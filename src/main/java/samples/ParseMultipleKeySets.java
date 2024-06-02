package samples;

import lombok.extern.log4j.Log4j2;
import net.zousys.compressedtable.CompressedComparatorFactory;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.DataFormatException;

@Log4j2
public class ParseMultipleKeySets {
    /**
     * Default the Table is compressed. This is to parse a CSV file to compressed table.
     *
     * @param args
     * @throws IOException
     * @throws DataFormatException
     */
    public static void main(String[] args) throws IOException, DataFormatException {
        CompareListener k = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"First Name", "Last Name"})
                        .addHeaders(new String[]{"Phone 1", "Phone 2", "Email"})
                )
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get("customers-1000b.csv")
                        .toAbsolutePath()
                        .toString());
        k.handleBeforeLoaded(beforetable);
        log.info("Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders());
    }

}
