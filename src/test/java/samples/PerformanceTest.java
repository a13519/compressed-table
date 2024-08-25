package samples;

import net.zousys.compressedtable.CompressedComparatorFactory;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.DataFormatException;

public class PerformanceTest {
    /**
     *
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void compare100000() throws IOException, DataFormatException, URISyntaxException {
        System.out.println("Uncompressed loading of 100K records: ");
        comparePT("/Users/songzou/Downloads/COMPA/organizations-100000A.csv", "/Users/songzou/Downloads/COMPA/organizations-100000B.csv", false);
        System.out.println("Compressed loading of 100K records: ");
        comparePT("/Users/songzou/Downloads/COMPA/organizations-100000A.csv", "/Users/songzou/Downloads/COMPA/organizations-100000B.csv", true);
    }
    @Test
    public void compare500000() throws IOException, DataFormatException, URISyntaxException {
        System.out.println("Uncompressed loading of 500K records: ");
        comparePT("/Users/songzou/Downloads/COMPA/organizations-500000A.csv", "/Users/songzou/Downloads/COMPA/organizations-500000B.csv", false);
        System.out.println("Compressed loading of 500K records: ");
        comparePT("/Users/songzou/Downloads/COMPA/organizations-500000A.csv", "/Users/songzou/Downloads/COMPA/organizations-500000B.csv", true);
    }
    public void comparePT(String a, String b, boolean comp) throws IOException, DataFormatException, URISyntaxException {
        CompareListener listener = new CompareListener();
        long start = System.currentTimeMillis();

        CompressedTable beforetable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Organization Id"})
                )
                .compressed(comp)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Paths.get(a).toFile());
        listener.handleBeforeLoaded(beforetable);
        System.out.println("-- Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());
        System.out.println("-- Before loaded in "+(System.currentTimeMillis()-start)+"ms");

        start = System.currentTimeMillis();
        CompressedTable aftertable = CompressedTableFactory
                .build("csv")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Organization Id"})
                )
                .compressed(comp)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Paths.get(b).toFile());
        listener.handleAfterLoaded(aftertable);
        System.out.println("-- After size: " + aftertable.getContents().size() + " " + aftertable.getHeaders() + " Mode: " + beforetable.getMode());
        System.out.println("-- After loaded in "+(System.currentTimeMillis()-start)+"ms");

        start = System.currentTimeMillis();
        CompressedComparatorFactory.builder()
                .before(beforetable)
                .after(aftertable)
//                .comparatorListener(listener)
                .ignoredFields(new HashSet(Arrays.asList(new String[]{})))
                .strictMissed(true)
                .build().create()
                .compare();
        System.out.println("-- Compared in "+(System.currentTimeMillis()-start)+"ms");
    }
}
