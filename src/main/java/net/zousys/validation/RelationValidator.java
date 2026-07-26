package net.zousys.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelationValidator {

    public List<ValidationObservation> validate(
            ValidationRule rule,
            ValidationContext validationContext) {

        List<ValidationObservation> errors = new ArrayList<>();

        DataTable source = validationContext.getTables().get(rule.getSourceTable());
        DataTable target = validationContext.getTables().get(rule.getTargetTable());

        Map<Object, DataRow> masterIndex =
                target.getRows()
                        .stream()
                        .collect(Collectors.toMap(
                                r -> r.get(rule.getTargetColumn()),
                                r -> r
                        ));

        for (DataRow row : source.getRows()) {

            String key = row.get(rule.getSourceColumn());
            if (!masterIndex.containsKey(key)) {
                errors.add(
                        ValidationObservation.builder().
                                ruleId(rule.getId())
                                .tableName(rule.getTable())
                                .rowNumber(row.getRowNumber())
                                .primaryKey(row.getPrimaryKey())
                                .columnName(rule.getColumn())
                                .actualValue(key)
                                .message("Invalid " + rule.getDataType()).build()
                );
            }

        }

        return errors;
    }
}
