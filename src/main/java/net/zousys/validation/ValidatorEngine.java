package net.zousys.validation;


import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ValidatorEngine {
    /**
     * @param rules
     * @param validationContext
     * @return
     */
    public List<ValidationObservation> validate(
            List<ValidationRule> rules,
            ValidationContext validationContext) {

        List<ValidationObservation> result = new ArrayList<>();

        for (ValidationRule rule : rules) {
            switch (rule.getNature()) {
                case "DATA_TYPE":
                    result.addAll(new DataTypeValidator().validate(rule, validationContext));
                    break;

                case "MANY_TO_ONE":
                    result.addAll(new RelationValidator().validate(rule, validationContext));
                    break;
                case "OBSERVATION":
                    List<ValidationObservation> r = new RelationValidator().validate(rule, validationContext);
                    DataVault.getInstance().setValue(rule.getStore(), r);
            }
        }

        return result;
    }
}
