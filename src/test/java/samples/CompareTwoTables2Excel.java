package samples;

import net.zousys.compressedtable.ComparisonResult;
import net.zousys.compressedtable.CompressedComparator;
import net.zousys.compressedtable.CompressedComparatorFactory;
import net.zousys.compressedtable.CompressedTableFactory;
import net.zousys.compressedtable.impl.CompressedTable;
import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.template.CompareListenerInExcel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompareTwoTables2Excel {
    /**
     * @throws IOException
     * @throws DataFormatException
     */
    @Test
    public void compareCSVSingleKey() throws IOException, DataFormatException {
        Set<String> ignoredColumns = new HashSet(Arrays.asList(new String[]{"Country"}));

        CompareListenerInExcel listener = new CompareListenerInExcel("CSVsingleKey.xlsx");

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
                .strictMissed(true)
                .build().create()
                .compare();

    }

    @Test
    public void compareEXCELSingleKey() throws IOException, DataFormatException {

        CompareListenerInExcel listener = new CompareListenerInExcel("ExcelsingleKey.xlsx");

        CompressedTable beforetable = CompressedTableFactory
                .build("excel")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(true)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000b.xlsx"));
        listener.handleBeforeLoaded(beforetable);
        System.out.println("Before size: " + beforetable.getContents().size() + " " + beforetable.getHeaders() + " Mode: " + beforetable.getMode());

        CompressedTable aftertable = CompressedTableFactory
                .build("excel")
                .keyHeaderList(new KeyHeadersList()
                        .addHeaders(new String[]{"Customer Id", "First Name"})
                )
                .compressed(true)
                .ignoredLines(0)
                .headerPosition(0)
                .delimeter(',')
                .parse(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("customers-1000a.xlsx"));
        listener.handleAfterLoaded(aftertable);
        System.out.println("After size: " + aftertable.getContents().size() + " " + aftertable.getHeaders() + " Mode: " + beforetable.getMode());

        CompressedComparator compressedComparator = CompressedComparatorFactory.builder()
                .before(beforetable)
                .after(aftertable)
                .comparatorListener(listener)
                .ignoredFields("Last Name")
                .strictMissed(true)
                .build().create()
                .compare();

        assertTrue(compressedComparator.getComparisonResult().getMismatches().size()==990);
        assertTrue(compressedComparator.getComparisonResult().getBeforeMissed().size()==2);
        assertTrue(compressedComparator.getComparisonResult().getAfterMissed().size()==3);
        assertTrue(compressedComparator.getComparisonResult().getUnitedHeaders().size()==14);
    }

}
