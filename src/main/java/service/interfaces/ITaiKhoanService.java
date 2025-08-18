package service.interfaces;

import model.TaiKhoan;
import model.NhanVien;
import exception.BusinessException;
import exception.ValidationException;

import java.util.List;

/**
 * Service interface for TaiKhoan (Account) business operations
 */
public interface ITaiKhoanService {
    
    /**
     * Authenticate user login
     * @param username Username
     * @param password Plain text password
     * @return Authenticated account if successful
     * @throws BusinessException if authentication fails
     */
    TaiKhoan authenticate(String username, String password) throws BusinessException;
    
    /**
     * Create new account
     * @param account Account to create
     * @throws ValidationException if account data is invalid
     * @throws BusinessException if operation fails
     */
    void createAccount(TaiKhoan account) throws ValidationException, BusinessException;
    
    /**
     * Update existing account
     * @param account Account to update
     * @throws ValidationException if account data is invalid
     * @throws BusinessException if operation fails
     */
    void updateAccount(TaiKhoan account) throws ValidationException, BusinessException;
    
    /**
     * Delete account
     * @param username Username
     * @throws BusinessException if operation fails or account cannot be deleted
     */
    void deleteAccount(String username) throws BusinessException;
    
    /**
     * Get all accounts
     * @return List of all accounts
     * @throws BusinessException if operation fails
     */
    List<TaiKhoan> getAllAccounts() throws BusinessException;
    
    /**
     * Get account by username
     * @param username Username
     * @return Account if found, null otherwise
     * @throws BusinessException if operation fails
     */
    TaiKhoan getAccountByUsername(String username) throws BusinessException;
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return true if username exists
     * @throws BusinessException if operation fails
     */
    boolean usernameExists(String username) throws BusinessException;
    
    /**
     * Change password
     * @param username Username
     * @param oldPassword Current password
     * @param newPassword New password
     * @throws ValidationException if password validation fails
     * @throws BusinessException if operation fails
     */
    void changePassword(String username, String oldPassword, String newPassword) 
            throws ValidationException, BusinessException;
    
    /**
     * Get employee by ID
     * @param employeeId Employee ID
     * @return Employee if found, null otherwise
     * @throws BusinessException if operation fails
     */
    NhanVien getEmployeeById(String employeeId) throws BusinessException;
}
