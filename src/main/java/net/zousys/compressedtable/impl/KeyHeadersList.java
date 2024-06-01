package net.zousys.compressedtable.impl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class KeyHeadersList {
    /**
     *
     */
    private List<KeyHeaders> list = new ArrayList<>();

    /**
     *
     * @param headers
     * @return
     */
    public KeyHeadersList addHeaders(String[] headers) {
        list.add(new KeyHeaders(headers));
        return this;
    }

    public KeyHeadersList addHeaders(KeyHeaders keyheaders) {
        list.add(keyheaders);
        return this;
    }

    public int size() {
        return list.size();
    }
    /**
     *
     * @return
     */
    public List<KeyHeaders> getKeyHeadersList() {
        return list;
    }
}
