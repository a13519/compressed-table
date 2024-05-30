package net.zousys.compressedtable.impl.multikeys.key;

import lombok.Data;
import lombok.Getter;
import net.zousys.compressedtable.KeySet;
import net.zousys.compressedtable.impl.KeyHeaders;
import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.impl.KeyValue;
import net.zousys.compressedtable.impl.CompressedRow;

import java.util.*;

@Data
//@Log4j
public class MultiStringKey implements KeySet {
    private KeyHeadersList keyHeadersList = new KeyHeadersList();
    /**
     * map key is MultiStringKey's a value of key
     */
    private Map<String, KeyValue> keyValueList;
    private KeyValue matchedKeyValue;
    private CompressedRow row;
    private String mainKeyValue;
    private String nativeKeyValue;

    /**
     *
     * @param row
     */
    private MultiStringKey(CompressedRow row) {
        this.row = row;
    }

    /**
     *
     * @param row
     */
    private MultiStringKey(KeyHeadersList keyHeaderList, CompressedRow row) {
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
    public static MultiStringKey create(KeyHeadersList keyHeaderList, List<String> fields, CompressedRow row) {
        MultiStringKey sk = new MultiStringKey(keyHeaderList, row);
        sk.cast(fields,
                row.getTable().getHeaderMapping(),
                System.currentTimeMillis()+"."+row.getContent().hash());
        return sk;
    }

    @Override
    public String getNativeKeyValue() {
        return nativeKeyValue;
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

    public void cast(List<String> fields, Map<String, Integer> headerMapping, String nativeKeyValue) {
        this.nativeKeyValue = nativeKeyValue;
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
                keyValueList.put(headers.getCompositedKey(),
                        KeyValue.builder()
                                .name(headers.getCompositedKey())
                                .value(sb.toString()).build());
            }
        }
    }

    @Override
    public String toString() {
        return "{" + keyValueList + '}';
    }
}
