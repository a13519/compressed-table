package net.zousys.bucketcomp.comparability;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

@ToString
@Builder
@Data
public class ComparedRow {
    private boolean missmatched;
    private boolean ignored;
    private boolean strictMissed;
    private String key;
    private String[] beforeRowFields;
    private String[] afterRowFields;
    @Builder.Default
    private List<PairIndices> mismatchIndices = new ArrayList<>();

    @Builder
    @Data
    public static class PairIndices {
        private int beforeColumnIndex;
        private int afterColumnIndex;
    }
}
