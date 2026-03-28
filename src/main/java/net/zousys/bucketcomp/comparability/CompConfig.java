package net.zousys.bucketcomp.comparability;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CompConfig {

    private int bucketNumber;
    private int headerLine;

    private char delimiter = ',';
    private char quote = '"';
    private char escape = '\\';

    private boolean skipEmptyLines = true;
    private boolean extractHeader = true;

    private List<String> ignoredHeaders = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    private String bucket;

}