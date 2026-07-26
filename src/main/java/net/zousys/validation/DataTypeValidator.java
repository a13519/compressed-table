package net.zousys.validation;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DataTypeValidator extends Validator {

    public List<ValidationObservation> validate(
            ValidationRule rule,
            ValidationContext validationContext) {

        List<ValidationObservation> errors = new ArrayList<>();

        DataTable table =
                validationContext.getTables().get(rule.getTable());

        for (DataRow row : table.getRows()) {

            String value =
                    row.get(rule.getColumn());

            if (value == null)
                continue;

            boolean ok = true;

            switch (rule.getDataType()) {

                case "DECIMAL":
                    try {
                        new BigDecimal(value);
                    } catch (Exception e) {
                        ok = false;
                    }
                    break;

                case "INTEGER":
                    try {
                        Integer.parseInt(value);
                    } catch (Exception e) {
                        ok = false;
                    }
                    break;
            }

            if (!ok) {

                errors.add(
                        ValidationObservation.builder().
                                ruleId(rule.getId())
                                .tableName(rule.getTable())
                                .rowNumber(row.getRowNumber())
                                .primaryKey(row.getPrimaryKey())
                                .columnName(rule.getColumn())
                                .actualValue(value)
                                .message("Invalid " + rule.getDataType()).build()
                );

            }

        }

        return errors;
    }


}