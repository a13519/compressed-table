package net.zousys.compressedtable.key;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Builder
@Data
@ToString
public class KeyValue {
    private String name;
    private String value;
}
