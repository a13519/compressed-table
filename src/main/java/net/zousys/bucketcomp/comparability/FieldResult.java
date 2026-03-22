package net.zousys.bucketcomp.comparability;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class FieldResult {
    private String name;
    private int beforeColumnIndex;
    private int afterColumnIndex;
    private String beforeField;
    private String afterField;
    private boolean missmatched;
    private boolean ignored;
    private boolean strictMissed;
}