package use_case.cleaner.validators;


/**
 * Validator for BOOLEAN data.
 *
 * Accepts case-insensitive:
 *  - "true", "false"
 *  - "yes", "no"
 *  - "1", "0"
 */
public class BooleanValidator implements DataTypeValidator{
    public boolean isValid(String value) {
        String v = value.trim().toLowerCase();

        return v.equals("true") || v.equals("false");
    }

}
