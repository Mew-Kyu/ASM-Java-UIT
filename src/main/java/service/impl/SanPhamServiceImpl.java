package service.impl;

import service.interfaces.ISanPhamService;
import dao.interfaces.ISanPhamDAO;
import dao.interfaces.IDanhMucDAO;
import model.SanPham;
import model.DanhMuc;
import validation.SanPhamValidator;
import validation.ValidationResult;
import exception.BusinessException;
import exception.ValidationException;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service implementation for SanPham business operations
 * Implements business logic layer following Single Responsibility Principle
 */
public class SanPhamServiceImpl implements ISanPhamService {
    private static final Logger LOGGER = Logger.getLogger(SanPhamServiceImpl.class.getName());
    
    private final ISanPhamDAO sanPhamDAO;
    private final IDanhMucDAO danhMucDAO;
    private final SanPhamValidator validator;
    
    // Constructor injection following Dependency Inversion Principle
    public SanPhamServiceImpl(ISanPhamDAO sanPhamDAO, IDanhMucDAO danhMucDAO) {
        this.sanPhamDAO = sanPhamDAO;
        this.danhMucDAO = danhMucDAO;
        this.validator = new SanPhamValidator();
    }
    
    @Override
    public List<SanPham> getAllProducts() throws BusinessException {
        try {
            LOGGER.fine("Retrieving all products");
            return sanPhamDAO.findAll();
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve all products: " + e.getMessage());
            throw new BusinessException("Không thể lấy danh sách sản phẩm", e);
        }
    }
    
    @Override
    public SanPham getProductById(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID sản phẩm không hợp lệ");
        }
        
        try {
            LOGGER.fine("Retrieving product with ID: " + id);
            return sanPhamDAO.findById(id);
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve product with ID " + id + ": " + e.getMessage());
            throw new BusinessException("Không thể lấy thông tin sản phẩm", e);
        }
    }
    
    @Override
    public List<SanPham> searchProducts(String keyword) throws BusinessException {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                LOGGER.fine("Empty search keyword, returning all products");
                return getAllProducts();
            }
            
            String trimmedKeyword = keyword.trim();
            LOGGER.fine("Searching products with keyword: " + trimmedKeyword);
            return sanPhamDAO.findByName(trimmedKeyword);
        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            LOGGER.severe("Failed to search products: " + e.getMessage());
            throw new BusinessException("Không thể tìm kiếm sản phẩm", e);
        }
    }
    
    @Override
    public void createProduct(SanPham product) throws ValidationException, BusinessException {
        // Validate input
        ValidationResult validationResult = validator.validateForCreation(product);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }
        
        try {
            // Business rule: Check if product name already exists
            if (isProductNameExists(product.getTenSP(), null)) {
                throw new BusinessException("Tên sản phẩm đã tồn tại");
            }
            
            // Business rule: Verify category exists
            if (!isCategoryExists(product.getDanhMuc().getId())) {
                throw new BusinessException("Danh mục sản phẩm không tồn tại");
            }
            
            LOGGER.info("Creating new product: " + product.getTenSP());
            sanPhamDAO.insert(product);
            LOGGER.info("Product created successfully with ID: " + product.getId());
            
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions
        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            LOGGER.severe("Failed to create product: " + e.getMessage());
            throw new BusinessException("Không thể tạo sản phẩm mới", e);
        }
    }
    
    @Override
    public void updateProduct(SanPham product) throws ValidationException, BusinessException {
        // Validate input
        ValidationResult validationResult = validator.validateForUpdate(product);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }
        
        try {
            // Business rule: Check if product exists
            SanPham existingProduct = getProductById(product.getId());
            if (existingProduct == null) {
                throw new BusinessException("Sản phẩm không tồn tại");
            }
            
            // Business rule: Check if new name conflicts with other products
            if (isProductNameExists(product.getTenSP(), product.getId())) {
                throw new BusinessException("Tên sản phẩm đã tồn tại");
            }
            
            // Business rule: Verify category exists
            if (!isCategoryExists(product.getDanhMuc().getId())) {
                throw new BusinessException("Danh mục sản phẩm không tồn tại");
            }
            
            LOGGER.info("Updating product: " + product.getTenSP() + " (ID: " + product.getId() + ")");
            sanPhamDAO.update(product);
            LOGGER.info("Product updated successfully");
            
        } catch (ValidationException e) {
            throw e; // Re-throw validation exceptions
        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            LOGGER.severe("Failed to update product: " + e.getMessage());
            throw new BusinessException("Không thể cập nhật sản phẩm", e);
        }
    }
    
    @Override
    public void deleteProduct(int id) throws BusinessException {
        if (id <= 0) {
            throw new BusinessException("ID sản phẩm không hợp lệ");
        }
        
        try {
            // Business rule: Check if product exists
            SanPham existingProduct = getProductById(id);
            if (existingProduct == null) {
                throw new BusinessException("Sản phẩm không tồn tại");
            }
            
            // Business rule: Check if product can be deleted
            // (e.g., not referenced in orders, variants, etc.)
            if (hasProductVariants(id)) {
                throw new BusinessException("Không thể xóa sản phẩm có biến thể");
            }
            
            if (isProductInOrders(id)) {
                throw new BusinessException("Không thể xóa sản phẩm đã có trong đơn hàng");
            }
            
            LOGGER.info("Deleting product with ID: " + id);
            sanPhamDAO.delete(id);
            LOGGER.info("Product deleted successfully");
            
        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        } catch (Exception e) {
            LOGGER.severe("Failed to delete product: " + e.getMessage());
            throw new BusinessException("Không thể xóa sản phẩm", e);
        }
    }
    
    @Override
    public boolean productExists(int id) throws BusinessException {
        return getProductById(id) != null;
    }
    
    @Override
    public List<SanPham> getProductsByCategory(int categoryId) throws BusinessException {
        if (categoryId <= 0) {
            throw new BusinessException("ID danh mục không hợp lệ");
        }
        
        try {
            LOGGER.fine("Retrieving products for category ID: " + categoryId);
            // This would require a new method in DAO
            // For now, filter from all products
            return getAllProducts().stream()
                    .filter(p -> p.getDanhMuc() != null && 
                               p.getDanhMuc().getId() != null && 
                               p.getDanhMuc().getId().equals(categoryId))
                    .toList();
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve products by category: " + e.getMessage());
            throw new BusinessException("Không thể lấy sản phẩm theo danh mục", e);
        }
    }
    
    @Override
    public List<DanhMuc> getAllCategories() throws BusinessException {
        try {
            LOGGER.fine("Retrieving all categories");
            return danhMucDAO.findAll();
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve categories: " + e.getMessage());
            throw new BusinessException("Không thể lấy danh sách danh mục", e);
        }
    }
    
    // Private helper methods for business rules
    
    private boolean isProductNameExists(String productName, Integer excludeId) {
        try {
            List<SanPham> products = sanPhamDAO.findByName(productName);
            return products.stream()
                    .anyMatch(p -> !p.getId().equals(excludeId) && 
                                 p.getTenSP().equalsIgnoreCase(productName.trim()));
        } catch (Exception e) {
            LOGGER.warning("Error checking product name existence: " + e.getMessage());
            return false; // Assume doesn't exist if check fails
        }
    }
    
    private boolean isCategoryExists(Integer categoryId) {
        try {
            DanhMuc category = danhMucDAO.findById(categoryId);
            return category != null;
        } catch (Exception e) {
            LOGGER.warning("Error checking category existence: " + e.getMessage());
            return false;
        }
    }
    
    private boolean hasProductVariants(int productId) {
        // This would check if product has variants in BienTheSanPham table
        // Implementation depends on BienTheSanPhamDAO
        // For now, return false (allowing deletion)
        return false;
    }
    
    private boolean isProductInOrders(int productId) {
        // This would check if product is referenced in orders
        // Implementation depends on order-related DAOs
        // For now, return false (allowing deletion)
        return false;
    }
}
