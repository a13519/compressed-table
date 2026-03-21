package net.zousys.bucketcomp.comparability;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ComparatorContext {
    private Source beforeSource;
    private Source afterSource;
    private CompConfig config;
    private ComparatorListener listener;
    private ColumnStructure columnStructure;

    /**
     *
     */
    public void init() {
        columnStructure = new ColumnStructure(beforeSource, afterSource, config, listener);
    }


}
