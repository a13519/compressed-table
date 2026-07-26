package net.zousys.validation;


import java.util.List;


public abstract class Validator {
    public abstract List<ValidationObservation> validate(ValidationRule rule, ValidationContext context);
}
