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
public class ParseSingleKeySet {
    /**
     * This is to parse a Single Key Set CSV file to an un-compressed table
     * @param args
     * @throws IOException
     * @throws DataFormatException
     */
    public static void main(String[] args) throws IOException, DataFormatException {
        CompareListener listener = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(false)
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get("customers-1000b.csv")
                        .toAbsolutePath()
                        .toString());
        listener.handleBeforeLoaded(beforetable);
        log.info("Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

    }
}
