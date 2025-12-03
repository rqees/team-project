package use_case.cleaner.validators;

// A validator for a specific DataType.
public interface DataTypeValidator {
    /**
     * Return whether the given value is valid for this validator's data type
     *
     * @param value is not null and has already trimmed
     * @return boolean
     */
    boolean isValid(String value);
}
