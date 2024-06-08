package net.zousys.compressedtable.sterotype;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 */
public class Cache {
    private ArrayList<String[]> stack = new ArrayList<>();
    private int number = 0;

    /**
     *
     * @param number
     */
    public Cache(int number) {
        this.number = number;
    }

    /**
     *
     * @param fields
     * @return
     */
    public String[] append(String[] fields) {
        if (number == 0) {
            return fields;
        }
        stack.add(fields);
        if (stack.size()>number) {
            String[] r = (String[])stack.get(0);
            stack.remove(0);
            return r;
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public List<String[]> remaining() {
        return stack;
    }
}
