package net.zousys.bucketcomp.comparability;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompConfig {
    private int bucketNumber;
    private char delimeter = ',';
    private char quote = 0;
    private char escape = 0;
    private boolean skipEmptyLines = true;
    private boolean extractHeader = true;
    private List<String> ignoredHeaders = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private String bucket;
}
