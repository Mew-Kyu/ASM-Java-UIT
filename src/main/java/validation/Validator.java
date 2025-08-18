package validation;

/**
 * Base interface for validators following Strategy pattern
 */
public interface Validator<T> {
    
    /**
     * Validate the given object
     * @param object Object to validate
     * @return ValidationResult containing errors and warnings
     */
    ValidationResult validate(T object);
}
