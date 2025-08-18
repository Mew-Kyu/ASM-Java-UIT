package validation;

import model.SanPham;

/**
 * Validator for SanPham entities following Strategy pattern
 */
public class SanPhamValidator implements Validator<SanPham> {
    
    @Override
    public ValidationResult validate(SanPham sanPham) {
        ValidationResult result = new ValidationResult();
        
        if (sanPham == null) {
            result.addError("Sản phẩm không được null");
            return result;
        }
        
        // Validate product name
        validateProductName(sanPham.getTenSP(), result);
        
        // Validate category
        validateCategory(sanPham, result);
        
        // Validate description
        validateDescription(sanPham.getMoTa(), result);
        
        return result;
    }
    
    private void validateProductName(String tenSP, ValidationResult result) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            result.addError("Tên sản phẩm không được để trống");
            return;
        }
        
        String trimmed = tenSP.trim();
        if (trimmed.length() < 2) {
            result.addError("Tên sản phẩm phải có ít nhất 2 ký tự");
        }
        
        if (trimmed.length() > 100) {
            result.addError("Tên sản phẩm không được vượt quá 100 ký tự");
        }
        
        // Check for invalid characters
        if (!trimmed.matches("^[\\p{L}\\p{N}\\s\\-_.()]+$")) {
            result.addError("Tên sản phẩm chứa ký tự không hợp lệ");
        }
    }
    
    private void validateCategory(SanPham sanPham, ValidationResult result) {
        if (sanPham.getDanhMuc() == null) {
            result.addError("Vui lòng chọn danh mục sản phẩm");
            return;
        }
        
        if (sanPham.getDanhMuc().getId() == null || sanPham.getDanhMuc().getId() <= 0) {
            result.addError("Danh mục sản phẩm không hợp lệ");
        }
    }
    
    private void validateDescription(String moTa, ValidationResult result) {
        if (moTa != null && moTa.length() > 200) {
            result.addError("Mô tả sản phẩm không được vượt quá 200 ký tự");
        }
        
        // Description is optional, so null or empty is allowed
        if (moTa != null && !moTa.trim().isEmpty()) {
            String trimmed = moTa.trim();
            if (trimmed.length() < 5) {
                result.addWarning("Mô tả sản phẩm quá ngắn, nên có ít nhất 5 ký tự");
            }
        }
    }
    
    /**
     * Validate product for creation (stricter validation)
     */
    public ValidationResult validateForCreation(SanPham sanPham) {
        ValidationResult result = validate(sanPham);
        
        // Additional validations for creation
        if (sanPham != null && sanPham.getId() != null) {
            result.addError("ID sản phẩm phải null khi tạo mới");
        }
        
        return result;
    }
    
    /**
     * Validate product for update
     */
    public ValidationResult validateForUpdate(SanPham sanPham) {
        ValidationResult result = validate(sanPham);
        
        // Additional validations for update
        if (sanPham != null && (sanPham.getId() == null || sanPham.getId() <= 0)) {
            result.addError("ID sản phẩm phải có giá trị hợp lệ khi cập nhật");
        }
        
        return result;
    }
}
