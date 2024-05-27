package net.zousys.compressedtable.impl;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

@Builder
@Data
@ToString
public class KeyValue {
    private String name;
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyValue keyValue = (KeyValue) o;
        return Objects.equals(name, keyValue.name) && Objects.equals(value, keyValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}


