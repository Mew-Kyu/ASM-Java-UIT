package service.interfaces;

import model.SanPham;
import model.DanhMuc;
import exception.BusinessException;
import exception.ValidationException;

import java.util.List;

/**
 * Service interface for SanPham business operations
 * Defines business logic contract following Interface Segregation Principle
 */
public interface ISanPhamService {
    
    /**
     * Get all products
     * @return List of all products
     * @throws BusinessException if operation fails
     */
    List<SanPham> getAllProducts() throws BusinessException;
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return Product if found, null otherwise
     * @throws BusinessException if operation fails
     */
    SanPham getProductById(int id) throws BusinessException;
    
    /**
     * Search products by name
     * @param keyword Search keyword
     * @return List of matching products
     * @throws BusinessException if operation fails
     */
    List<SanPham> searchProducts(String keyword) throws BusinessException;
    
    /**
     * Create new product
     * @param product Product to create
     * @throws ValidationException if product data is invalid
     * @throws BusinessException if operation fails
     */
    void createProduct(SanPham product) throws ValidationException, BusinessException;
    
    /**
     * Update existing product
     * @param product Product to update
     * @throws ValidationException if product data is invalid
     * @throws BusinessException if operation fails
     */
    void updateProduct(SanPham product) throws ValidationException, BusinessException;
    
    /**
     * Delete product
     * @param id Product ID
     * @throws BusinessException if operation fails or product cannot be deleted
     */
    void deleteProduct(int id) throws BusinessException;
    
    /**
     * Check if product exists
     * @param id Product ID
     * @return true if product exists
     * @throws BusinessException if operation fails
     */
    boolean productExists(int id) throws BusinessException;
    
    /**
     * Get products by category
     * @param categoryId Category ID
     * @return List of products in category
     * @throws BusinessException if operation fails
     */
    List<SanPham> getProductsByCategory(int categoryId) throws BusinessException;
    
    /**
     * Get all categories
     * @return List of all categories
     * @throws BusinessException if operation fails
     */
    List<DanhMuc> getAllCategories() throws BusinessException;
}
