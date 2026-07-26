package net.zousys.validation;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class ValidationObservation {

    private String ruleId;
    private boolean isError = false;

    private String tableName;

    private long rowNumber;

    private String primaryKey;

    private String columnName;

    private String type;

    private String actualValue;

    private Collection<String> bundleData;

    private String message;

}