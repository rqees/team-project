package use_case.cleaner.validators;

// Validator for NUMERIC data.
// Valid if it can be parsed as double
public class NumericValidator implements DataTypeValidator  {
    public boolean isValid(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
