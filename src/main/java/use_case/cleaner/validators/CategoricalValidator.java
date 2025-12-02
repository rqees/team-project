package use_case.cleaner.validators;

// Validator for CATEGORICAL data.
// Valid for non-empty trimmed String
public class CategoricalValidator implements DataTypeValidator{

    public boolean isValid(String value) {
        // value is assumed non-null and trimmed
        return !value.isEmpty();
    }
}
