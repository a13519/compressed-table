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
public class Main {

    public static void main(String[] a) throws IOException, DataFormatException {
        singlekey();
//        multikeys();
    }

    /**
     *
     */
    public static void singlekey() throws IOException {
        CompareListener k = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(false)
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get(Paths.get("customers-1000b.csv")
                        .toAbsolutePath()
                        .toString()).toString());
        k.handleBeforeLoaded(beforetable);
        log.info("Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

        CompressedTable aftertable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(true)
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get(Paths.get("customers-1000a.csv")
                        .toAbsolutePath()
                        .toString()).toString());
        k.handleAfterLoaded(aftertable);
        log.info("After size: " + aftertable.getContents().size() + " " + aftertable.getHeaders() + " Mode: " + beforetable.getMode());

        CompressedComparatorFactory.builder()
                .before(beforetable)
                .after(aftertable)
                .comparatorListener(k)
                .ignoredFields(new HashSet(Arrays.asList(new String[]{})))
                .strictMissed(true)
                .build().create()
                .compare();

    }

    /**
     *
     */
    public static void multikeys() throws IOException {
        CompareListener k = new CompareListener();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"First Name", "Last Name"})
                        .addHeaders(new String[]{"Phone 1", "Phone 2", "Email"})
                )
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get(Paths.get("customers-1000b.csv")
                        .toAbsolutePath()
                        .toString()).toString());
        k.handleBeforeLoaded(beforetable);
        log.info("Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders());

        CompressedTable aftertable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"First Name", "Last Name"})
                        .addHeaders(new String[]{"Phone 1", "Phone 2", "Email"})
                )
                .ignoredLines(0)
                .delimeter(',')
                .parse(Paths.get(Paths.get("customers-1000a.csv")
                        .toAbsolutePath()
                        .toString()).toString());
        k.handleAfterLoaded(aftertable);
        log.info("After size: " + aftertable.getContents().size() + " " + aftertable.getHeaders());

        CompressedComparatorFactory.builder()
                .before(beforetable)
                .after(aftertable)
                .comparatorListener(k)
                .strictMissed(false)
                .ignoredFields(new HashSet(Arrays.asList(new String[]{})))
                .build().create()
                .compare();

    }

}
