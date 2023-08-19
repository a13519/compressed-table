package net.zousys.compressedtable;

import lombok.Getter;
import net.zousys.compressedtable.impl.CompressedTable;
import org.apache.commons.csv.CSVFormat;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class CompressedTableFactory {
    public static enum Type {
        CSV, EXCEL
    }

    ;
    @Getter
    private Type type;
    @Getter
    private String[] keyHeaders;

    private CompressedTableFactory(Type type) {
        this.type = type;
    }

    public static CompressedTableFactory build(Type type) {
        if (type.equals(Type.CSV)) {
            return new CompressedTableFactory(type);
        } else {
            return null;
        }
    }

    public CompressedTableFactory headerKeys(String[] keyHeaders) {
        this.keyHeaders = keyHeaders;
        return this;
    }

    public CompressedTable readCSVFile(String filename) throws IOException {
        return null;
    }

    public CompressedTable readCSVFile(String filename, char delimeter) throws IOException {
        FileReader in = new FileReader(filename);
        CSVFormat format = CSVFormat.RFC4180.builder()
                .setDelimiter(delimeter)
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setTrim(true)
                .build();
        CompressedTable compressedTable = new CompressedTable();
        if (keyHeaders != null) {
            compressedTable.setKeyHeaders(keyHeaders);
        }
        format.parse(in).stream().forEach(re -> {
            try {
                System.out.println(re);
                compressedTable.appendRow(re.values(), true);
            } catch (IOException e) {
                //
            }
        });

        return compressedTable;
    }

    public static void main(String[] a) throws IOException {
        CompressedTable beforetable = CompressedTableFactory
                .build(Type.CSV)
                .headerKeys(new String[]{"Name", "Age"})
                .readCSVFile(Paths.get("biostats.csv").toString(), ',');
        CompressedTable aftertable = CompressedTableFactory
                .build(Type.CSV)
                .headerKeys(new String[]{"Name", "Age"})
                .readCSVFile(Paths.get("biostats1.csv").toString(), ',');

        ComparisonResult cr =
                CompressedComparator.builder()
                        .before(beforetable)
                .after(aftertable)
                .build()
                        .setIgnoredFields(new String[]{"Height (in)"})
                .compare();

    }
}
