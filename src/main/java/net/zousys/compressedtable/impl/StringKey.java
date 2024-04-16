package net.zousys.compressedtable.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.zousys.compressedtable.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
//@Log4j
public class StringKey implements Key {
    private List<String> keys = new ArrayList<>();

    public static StringKey create(String[] keyheaders, Map<String, Integer> map, List<String> fields) {
        StringKey sk = new StringKey();
        sk.setKey(keyheaders, map, fields);
        return sk;
    }

    @Override
    public String toString() {
        return "{" + keys +
                '}';
    }

    @Override
    public void setKey(String[] keyheaders, Map<String, Integer> map, List<String> fields) {
        if (keyheaders != null && map != null && fields != null) {
            keys = new ArrayList<>();
            Arrays.stream(keyheaders).forEach(header -> {
                try {
                    keys.add(fields.get(map.get(header)));
                } catch (Exception e) {
                    // ignore
                }
            });
        }
    }
}
