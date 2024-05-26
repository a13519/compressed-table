package net.zousys.compressedtable.key;

import lombok.Getter;
import net.zousys.compressedtable.KeySet;
import net.zousys.compressedtable.impl.CompressedRow;

import java.util.*;

@Getter
//@Log4j
public class StringKey implements KeySet {
    private KeyHeadersList keyHeadersList = new KeyHeadersList();
    /**
     * map key is StringKey's a value of key
     */
    private Map<String, KeyValue> keyValueList;
    private KeyValue matchedKeyValue;
    private CompressedRow row;
    private String mainKeyValue;
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
    private StringKey(KeyHeadersList keyHeaderList, CompressedRow row) {
        this.keyHeadersList = keyHeaderList;
        this.row = row;
    }

    /**
     *
     * @param keyHeaderList
     * @param fields
     * @param row
     * @return
     */
    public static StringKey create(KeyHeadersList keyHeaderList, List<String> fields, CompressedRow row) {
        StringKey sk = new StringKey(keyHeaderList, row);
        sk.cast(fields, row.getCompressedTable().getHeaderMapping(), row.getCompressedTable().getContents().size()+"."+row.getCompressedContent().hash());
        return sk;
    }

    @Override
    public String getMainKeyValue() {
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
    public void cast(List<String> fields, Map<String, Integer> headerMapping, String mainkey) {
        mainKeyValue = mainkey;
        if (keyHeadersList != null && row != null) {
            keyValueList = new HashMap<>();
            for (KeyHeaders headers : keyHeadersList.getKeyHeadersList()) {
                StringBuffer sb = new StringBuffer();
                Arrays.stream(headers.getKeyHeaders()).forEach(header -> {
                    try {
                        sb.append(fields.get(headerMapping.get(header)));
                        sb.append("|");
                    } catch (Exception e) {
                        // ignore
                    }
                });
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
