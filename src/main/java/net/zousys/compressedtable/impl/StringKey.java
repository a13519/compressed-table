package net.zousys.compressedtable.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
//@Log4j
public class StringKey implements Key {
    private List<String[]> keyHeaderList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();

    private CompressedRow row;

    /**
     *
     * @param row
     */
    private StringKey(List<String[]> keyHeaderList, CompressedRow row) {
        this.row = row;
    }

    /**
     *
     * @param keyHeaderList
     * @param fields
     * @param row
     * @return
     */
    public static StringKey create(List<String[]> keyHeaderList, List<String> fields, CompressedRow row) {
        StringKey sk = new StringKey(keyHeaderList, row);
        sk.cast(fields, row.getCompressedTable().getHeaderMapping());
        return sk;
    }

    @Override
    public String getMainKey() {
        if (keyHeaderList.size()>0){
            return keyList.get(0);
        } else {
            return "";
        }
    }

    @Override
    public String[] getKeys() {
        return keyList.toArray(new String[]{});
    }

    @Override
    public String getKey(int index) {
        return keyList.get(index);
    }

    @Override
    public String[] getKeyheaders(int index) {
        return new String[0];
    }

    @Override
    public int size() {
        return keyList.size();
    }

    @Override
    public void cast(List<String> fields, Map<String, Integer> headerMapping) {
        if (keyHeaderList != null && row != null) {
            keyList = new ArrayList<>();
            for (String[] headers : keyHeaderList) {
                StringBuffer sb = new StringBuffer();
                Arrays.stream(headers).forEach(header -> {
                    try {
                        sb.append(fields.get(headerMapping.get(header)));
                        sb.append("|");
                    } catch (Exception e) {
                        // ignore
                    }
                });
                keyList.add(sb.toString());
            }
        }
    }

    @Override
    public String toString() {
        return "{" + keyList + '}';
    }
}
