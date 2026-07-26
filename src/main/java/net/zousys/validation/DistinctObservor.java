package net.zousys.validation;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class DistinctObservor extends Validator {
    /**
     * @param rule
     * @param validationContext
     * @return
     */
    public List<ValidationObservation> validate(
            ValidationRule rule,
            ValidationContext validationContext) {

        List<ValidationObservation> observations = new ArrayList<>();
        Set<String> distinctValues = new HashSet<>();

        if (rule.getTable() != null && "*".equals(rule.getTable())) {

        }
        if (rule.getTable() != null) {
            DataTable table = validationContext.getTables().get(rule.getTable());
            for (DataRow row : table.getRows()) {
                String value = row.get(rule.getColumn());
                if (value != null) {
                    distinctValues.add(value);
                }
            }
        }

        observations.add(
                ValidationObservation.builder().
                        ruleId(rule.getId())
                        .tableName(rule.getTable())
                        .columnName(rule.getColumn())
                        .bundleData(distinctValues)
                        .message("Observations").build()
        );

        return observations;
    }


}