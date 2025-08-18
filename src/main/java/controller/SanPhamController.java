package controller;

import service.interfaces.ISanPhamService;
import model.SanPham;
import model.DanhMuc;
import exception.BusinessException;
import exception.ValidationException;
import config.ApplicationConfig;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.util.List;
import java.util.logging.Logger;

/**
 * Improved SanPham Controller using Service Layer and Dependency Injection
 * Follows Single Responsibility Principle - only handles UI-Service communication
 */
public class SanPhamController {
    private static final Logger LOGGER = Logger.getLogger(SanPhamController.class.getName());
    
    private final ISanPhamService sanPhamService;
    
    /**
     * Constructor using dependency injection
     */
    public SanPhamController() {
        // Get service from DI container
        this.sanPhamService = ApplicationConfig.getService(ISanPhamService.class);
    }
    
    /**
     * Get all products
     */
    public List<SanPham> getAllSanPham() {
        try {
            return sanPhamService.getAllProducts();
        } catch (BusinessException e) {
            LOGGER.warning("Failed to get all products: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Search products by keyword
     */
    public List<SanPham> searchSanPham(String keyword) {
        try {
            return sanPhamService.searchProducts(keyword);
        } catch (BusinessException e) {
            LOGGER.warning("Failed to search products: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Get product by ID
     */
    public SanPham getSanPhamById(int id) {
        try {
            return sanPhamService.getProductById(id);
        } catch (BusinessException e) {
            LOGGER.warning("Failed to get product by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Add new product with proper error handling
     */
    public boolean addSanPham(SanPham sp, Component parentComponent) {
        try {
            sanPhamService.createProduct(sp);
            showSuccessMessage(parentComponent, "Thêm sản phẩm thành công!");
            return true;
            
        } catch (ValidationException e) {
            showValidationErrors(parentComponent, e);
            return false;
            
        } catch (BusinessException e) {
            showBusinessError(parentComponent, "Lỗi khi thêm sản phẩm", e);
            return false;
        }
    }
    
    /**
     * Update product with proper error handling
     */
    public boolean updateSanPham(SanPham sp, Component parentComponent) {
        try {
            sanPhamService.updateProduct(sp);
            showSuccessMessage(parentComponent, "Cập nhật sản phẩm thành công!");
            return true;
            
        } catch (ValidationException e) {
            showValidationErrors(parentComponent, e);
            return false;
            
        } catch (BusinessException e) {
            showBusinessError(parentComponent, "Lỗi khi cập nhật sản phẩm", e);
            return false;
        }
    }
    
    /**
     * Delete product with confirmation and proper error handling
     */
    public boolean deleteSanPham(int id, Component parentComponent) {
        try {
            // Get product info for confirmation
            SanPham product = sanPhamService.getProductById(id);
            if (product == null) {
                showErrorMessage(parentComponent, "Sản phẩm không tồn tại!");
                return false;
            }
            
            // Confirm deletion
            int option = JOptionPane.showConfirmDialog(
                parentComponent,
                "Bạn có chắc chắn muốn xóa sản phẩm: " + product.getTenSP() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (option != JOptionPane.YES_OPTION) {
                return false;
            }
            
            sanPhamService.deleteProduct(id);
            showSuccessMessage(parentComponent, "Xóa sản phẩm thành công!");
            return true;
            
        } catch (BusinessException e) {
            showBusinessError(parentComponent, "Lỗi khi xóa sản phẩm", e);
            return false;
        }
    }
    
    /**
     * Get all categories
     */
    public List<DanhMuc> getAllDanhMuc() {
        try {
            return sanPhamService.getAllCategories();
        } catch (BusinessException e) {
            LOGGER.warning("Failed to get categories: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Get products by category
     */
    public List<SanPham> getProductsByCategory(int categoryId) {
        try {
            return sanPhamService.getProductsByCategory(categoryId);
        } catch (BusinessException e) {
            LOGGER.warning("Failed to get products by category: " + e.getMessage());
            return List.of(); // Return empty list on error
        }
    }
    
    /**
     * Check if product exists
     */
    public boolean productExists(int id) {
        try {
            return sanPhamService.productExists(id);
        } catch (BusinessException e) {
            LOGGER.warning("Failed to check product existence: " + e.getMessage());
            return false;
        }
    }
    
    // Private helper methods for UI interaction
    
    private void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Thành công", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent, 
            message, 
            "Lỗi", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void showValidationErrors(Component parent, ValidationException e) {
        StringBuilder message = new StringBuilder("Dữ liệu không hợp lệ:\n");
        for (String error : e.getErrors()) {
            message.append("• ").append(error).append("\n");
        }
        
        JOptionPane.showMessageDialog(
            parent,
            message.toString(),
            "Lỗi xác thực",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    private void showBusinessError(Component parent, String title, BusinessException e) {
        String message = e.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "Đã xảy ra lỗi không xác định";
        }
        
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
        
        // Log the full exception for debugging
        LOGGER.severe(title + ": " + e.getMessage());
        if (e.getCause() != null) {
            LOGGER.severe("Caused by: " + e.getCause().getMessage());
        }
    }
}