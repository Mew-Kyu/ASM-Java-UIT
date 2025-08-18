package validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of validation operations
 */
public class ValidationResult {
    private final List<String> errors;
    private final List<String> warnings;
    
    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * Add validation error
     */
    public void addError(String error) {
        errors.add(error);
    }
    
    /**
     * Add validation warning
     */
    public void addWarning(String warning) {
        warnings.add(warning);
    }
    
    /**
     * Check if validation passed (no errors)
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    /**
     * Check if there are any warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Get all validation errors
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Get all validation warnings
     */
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    /**
     * Get first error message
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
    
    /**
     * Get all errors as single string
     */
    public String getErrorsAsString() {
        return String.join(", ", errors);
    }
    
    /**
     * Get all warnings as single string
     */
    public String getWarningsAsString() {
        return String.join(", ", warnings);
    }
    
    /**
     * Merge another validation result into this one
     */
    public void merge(ValidationResult other) {
        this.errors.addAll(other.errors);
        this.warnings.addAll(other.warnings);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{");
        sb.append("valid=").append(isValid());
        if (!errors.isEmpty()) {
            sb.append(", errors=").append(errors);
        }
        if (!warnings.isEmpty()) {
            sb.append(", warnings=").append(warnings);
        }
        sb.append('}');
        return sb.toString();
    }
}
