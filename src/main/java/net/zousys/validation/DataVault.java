package net.zousys.validation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class DataVault {

    private static DataVault instance = null;
    private Map<String, Object> vault = new HashMap<>();

    /**
     *
     * @return
     */
    public static DataVault getInstance() {
        if (instance == null) {
            instance = new DataVault();
        }
        return instance;
    }

    /**
     *
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return vault.get(key);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValue(String key, Object value) {
        vault.put(key, value);
    }
}
