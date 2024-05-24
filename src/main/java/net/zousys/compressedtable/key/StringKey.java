package net.zousys.compressedtable.key;

import lombok.Getter;
import net.zousys.compressedtable.Key;
import net.zousys.compressedtable.impl.CompressedRow;

import java.util.*;

@Getter
//@Log4j
public class StringKey implements Key {
    private List<KeyHeaders> keyHeadersList = new ArrayList<>();
    /**
     * map key is StringKey's a value of key
     */
    private Map<String, KeyValue> keyValueList;
    private KeyValue mainKeyValue;
    private CompressedRow row;

    /**
     *
     * @param row
     */
    private StringKey(CompressedRow row) {
        this.row = row;
    }

    /**
     *
     * @param row
     */
    private StringKey(List<KeyHeaders> keyHeaderList, CompressedRow row) {
        this.row = row;
    }

    /**
     *
     * @param keyHeaderList
     * @param fields
     * @param row
     * @return
     */
    public static StringKey create(List<KeyHeaders> keyHeaderList, List<String> fields, CompressedRow row) {
        StringKey sk = new StringKey(keyHeaderList, row);
        sk.cast(fields, row.getCompressedTable().getHeaderMapping());
        return sk;
    }


    @Override
    public String getMainKey() {
        return "";
    }

    @Override
    public KeyValue getMainKeyValue() {
        return mainKeyValue;
    }

    @Override
    public KeyValue getKeyValue(String key) {
        return keyValueList.get(key);
    }

    @Override
    public String[] getKeyheaders(int index) {
        return new String[0];
    }

    @Override
    public int size() {
        return keyValueList.size();
    }

    @Override
    public void cast(List<String> fields, Map<String, Integer> headerMapping) {
        if (keyHeadersList != null && row != null) {
            keyValueList = new HashMap<>();
            int n = 0;
            for (KeyHeaders headers : keyHeadersList) {
                StringBuffer sb = new StringBuffer();
                Arrays.stream(headers.getKeyHeaders()).forEach(header -> {
                    try {
                        sb.append(fields.get(headerMapping.get(header)));
                        sb.append("|");
                    } catch (Exception e) {
                        // ignore
                    }
                });
                if (n++==0) {
                    mainKeyValue = KeyValue.builder().name(headers.getCompositedKeyValue()).value(sb.toString()).build();
                }
                keyValueList.put(headers.getCompositedKeyValue(),
                        KeyValue.builder()
                                .name(headers.getCompositedKeyValue())
                                .value(sb.toString()).build());
            }
        }
    }

    @Override
    public String toString() {
        return "{" + keyValueList + '}';
    }
}
