package net.zousys.compressedtable.impl.singlekey.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.KeySet;
import net.zousys.compressedtable.impl.CompressedRow;
import net.zousys.compressedtable.impl.KeyHeadersList;
import net.zousys.compressedtable.impl.KeyValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
//@Log4j
public class SingleStringKey implements KeySet {
    private StringBuffer sb = new StringBuffer();
    private String nativeKeyValue;

    /**
     *
     * @param keyHeaderList
     * @param fields
     * @param row
     * @return
     */
    public static SingleStringKey create(KeyHeadersList keyHeaderList, List<String> fields, CompressedRow row) {
        SingleStringKey sk = new SingleStringKey();
        sk.cast(fields,
                row.getTable().getHeaderMapping(),
                keyHeaderList,
                System.currentTimeMillis()+"."+row.getContent().hash());
        return sk;
    }

    @Override
    public String toString() {
        return "{" + sb +
                '}';
    }

    @Override
    public String getNativeKeyValue() {
        return nativeKeyValue;
    }

    @Override
    public String getMainKeyValue() {
        return toString();
    }

    @Override
    public KeyValue getMatchedKeyValue() {
        return null;
    }

    @Override
    public KeyValue getKeyValue(String key) {
        return KeyValue.builder().name(KeyValue.MAINAME).value(toString()).build();
    }

    @Override
    public String[] getKeyheaders(int index) {
        return new String[0];
    }

    @Override
    public int size() {
        return 1;
    }

    /**
     * for single key
     * @param fields
     * @param map
     * @param keyHeaderList
     */
    public void cast(List<String> fields, Map<String, Integer> map, KeyHeadersList keyHeaderList, String nativeKeyValue) {
        if (keyHeaderList != null && map != null && fields != null && keyHeaderList.getKeyHeadersList().size() == 1) {
            this.nativeKeyValue = nativeKeyValue;
            Arrays.stream(keyHeaderList.getKeyHeadersList().get(0).getKeyHeaders()).forEach(header -> {
                try {
                    sb.append(fields.get(map.get(header))+"|");
                } catch (Exception e) {
                    // ignore
                }
            });
            if (sb.length()>0) {
                sb.delete(sb.length() - 1, sb.length());
            }
        }
    }
}