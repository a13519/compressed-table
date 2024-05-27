package net.zousys.compressedtable.impl.singlekey.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.KeySet;
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
public class StringKey implements KeySet {
    private List<String> keys = new ArrayList<>();

    /**
     *
     * @param keyHeaderLis
     * @param map
     * @param fields
     * @return
     */
    public static StringKey create(KeyHeadersList keyHeaderLis, Map<String, Integer> map, List<String> fields) {
        StringKey sk = new StringKey();
        sk.cast(fields, map, keyHeaderLis);
        return sk;
    }

    @Override
    public String toString() {
        return "{" + keys +
                '}';
    }

    @Override
    public String getMainKey() {
        return "";
    }

    @Override
    public String getMainKeyValue() {
        return "";
    }

    @Override
    public KeyValue getMatchedKeyValue() {
        return null;
    }

    @Override
    public KeyValue getKeyValue(String key) {
        return null;
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
     *
     * @param fields
     * @param headerMapping
     * @param mainkey
     */
    public void cast(List<String> fields, Map<String, Integer> headerMapping, String mainkey) {

    }

    /**
     * for single key
     * @param fields
     * @param map
     * @param keyHeaderList
     */
    @Override
    public void cast(List<String> fields, Map<String, Integer> map, KeyHeadersList keyHeaderList) {
        if (keyHeaderList != null && map != null && fields != null) {
            keys = new ArrayList<>();
            Arrays.stream(keyHeaderList.getKeyHeadersList().get(0).getKeyHeaders()).forEach(header -> {
                try {
                    keys.add(fields.get(map.get(header)));
                } catch (Exception e) {
                    // ignore
                }
            });
        }
    }
}