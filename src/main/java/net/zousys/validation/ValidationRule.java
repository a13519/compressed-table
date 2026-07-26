package net.zousys.validation;

import lombok.Data;

@Data
public class ValidationRule {
    private String id;
    private String nature;
    private String type;
    private String table;
    private String column;
    private String dataType;
    private String sourceTable;
    private String sourceColumn;
    private String targetTable;
    private String targetColumn;
    private String store;
}
