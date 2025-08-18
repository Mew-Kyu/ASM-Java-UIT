package validation;

import model.TaiKhoan;

import java.util.regex.Pattern;

/**
 * Validator for TaiKhoan entities following Strategy pattern
 */
public class TaiKhoanValidator implements Validator<TaiKhoan> {
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    
    @Override
    public ValidationResult validate(TaiKhoan taiKhoan) {
        ValidationResult result = new ValidationResult();
        
        if (taiKhoan == null) {
            result.addError("Tài khoản không được null");
            return result;
        }
        
        // Validate username
        validateUsername(taiKhoan.getTenDangNhap(), result);
        
        // Validate password (only if provided)
        if (taiKhoan.getMatKhau() != null && !taiKhoan.getMatKhau().isEmpty()) {
            validatePassword(taiKhoan.getMatKhau(), result);
        }
        
        // Validate role
        validateRole(taiKhoan.getQuyen(), result);
        
        // Validate employee association
        validateEmployee(taiKhoan, result);
        
        return result;
    }
    
    private void validateUsername(String username, ValidationResult result) {
        if (username == null || username.trim().isEmpty()) {
            result.addError("Tên đăng nhập không được để trống");
            return;
        }
        
        String trimmed = username.trim();
        if (!USERNAME_PATTERN.matcher(trimmed).matches()) {
            result.addError("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới, độ dài từ 3-50 ký tự");
        }
    }
    
    private void validatePassword(String password, ValidationResult result) {
        if (password == null || password.isEmpty()) {
            result.addError("Mật khẩu không được để trống");
            return;
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            result.addError("Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự");
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            result.addError("Mật khẩu không được vượt quá " + MAX_PASSWORD_LENGTH + " ký tự");
        }
        
        // Check password complexity
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        
        if (!hasLetter) {
            result.addWarning("Mật khẩu nên chứa ít nhất một chữ cái");
        }
        
        if (!hasDigit) {
            result.addWarning("Mật khẩu nên chứa ít nhất một chữ số");
        }
        
        // Check for common weak passwords
        if (isWeakPassword(password)) {
            result.addWarning("Mật khẩu quá đơn giản, nên sử dụng mật khẩu phức tạp hơn");
        }
    }
    
    private void validateRole(String role, ValidationResult result) {
        if (role == null || role.trim().isEmpty()) {
            result.addError("Quyền hạn không được để trống");
            return;
        }
        
        String trimmed = role.trim().toUpperCase();
        if (!trimmed.equals("ADMIN") && !trimmed.equals("MANAGER") && !trimmed.equals("STAFF")) {
            result.addError("Quyền hạn phải là ADMIN, MANAGER hoặc STAFF");
        }
    }
    
    private void validateEmployee(TaiKhoan taiKhoan, ValidationResult result) {
        if (taiKhoan.getMaNV() == null) {
            result.addError("Tài khoản phải được liên kết với một nhân viên");
            return;
        }
        
        // Additional employee validation can be added here
        // For example, checking if employee exists in database
    }
    
    private boolean isWeakPassword(String password) {
        String lower = password.toLowerCase();
        String[] weakPasswords = {
            "123456", "password", "123456789", "12345678", "12345",
            "1234567", "qwerty", "abc123", "password123", "admin"
        };
        
        for (String weak : weakPasswords) {
            if (lower.contains(weak)) {
                return true;
            }
        }
        
        // Check for repeated characters
        if (password.matches("(.)\\1{2,}")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Validate account for creation
     */
    public ValidationResult validateForCreation(TaiKhoan taiKhoan) {
        ValidationResult result = validate(taiKhoan);
        
        // Password is required for creation
        if (taiKhoan != null && (taiKhoan.getMatKhau() == null || taiKhoan.getMatKhau().trim().isEmpty())) {
            result.addError("Mật khẩu không được để trống khi tạo tài khoản mới");
        }
        
        return result;
    }
    
    /**
     * Validate account for update
     */
    public ValidationResult validateForUpdate(TaiKhoan taiKhoan) {
        ValidationResult result = new ValidationResult();
        
        if (taiKhoan == null) {
            result.addError("Tài khoản không được null");
            return result;
        }
        
        // Username is required for update
        validateUsername(taiKhoan.getTenDangNhap(), result);
        
        // Password is optional for update (if null/empty, keep existing password)
        if (taiKhoan.getMatKhau() != null && !taiKhoan.getMatKhau().trim().isEmpty()) {
            validatePassword(taiKhoan.getMatKhau(), result);
        }
        
        // Role and employee are still required
        validateRole(taiKhoan.getQuyen(), result);
        validateEmployee(taiKhoan, result);
        
        return result;
    }
    
    /**
     * Validate password change
     */
    public ValidationResult validatePasswordChange(String oldPassword, String newPassword) {
        ValidationResult result = new ValidationResult();
        
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            result.addError("Mật khẩu hiện tại không được để trống");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.addError("Mật khẩu mới không được để trống");
        } else {
            validatePassword(newPassword, result);
            
            // Check if new password is same as old
            if (oldPassword != null && oldPassword.equals(newPassword)) {
                result.addError("Mật khẩu mới phải khác mật khẩu hiện tại");
            }
        }
        
        return result;
    }
}
