package net.zousys.validation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ValidationResult {

    String executionId;

    String ruleId;

    String ruleType;

    String severity; // ERROR/WARN

    String tableName;

    String fileName;

    long rowNumber;

    String primaryKey;

    String columnName;

    String oldValue;

    String expectedValue;

    String message;

    LocalDateTime timestamp;
}
