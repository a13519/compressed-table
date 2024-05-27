package net.zousys.compressedtable.key;

import lombok.Getter;

import java.util.Arrays;

public class KeyHeaders {
    @Getter
    private String [] keyHeaders;
    private String value;

    /**
     *
     * @param keyHeaders
     */
    public KeyHeaders(String[] keyHeaders) {
        this.keyHeaders = keyHeaders;
        value = "{" + Arrays.toString(keyHeaders) + '}';
    }

    /**
     *
     * @return
     */
    public String getCompositedKey() {
        return value;
    }
}
