package net.zousys.compressedtable;

import lombok.Getter;
import net.zousys.compressedtable.impl.CompressedTable;
import org.apache.commons.csv.CSVFormat;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

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

    public static CompressedTableFactory build(String type) {
        if (type == null){
            return null;
        }
        switch (type.toLowerCase()){
            case "csv":{
                return new CompressedTableFactory(Type.CSV);
            }
        }
        return null;
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
}
