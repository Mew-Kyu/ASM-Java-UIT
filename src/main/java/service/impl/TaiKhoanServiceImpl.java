package service.impl;

import service.interfaces.ITaiKhoanService;
import dao.interfaces.ITaiKhoanDAO;
import dao.interfaces.INhanVienDAO;
import model.TaiKhoan;
import model.NhanVien;
import validation.TaiKhoanValidator;
import validation.ValidationResult;
import exception.BusinessException;
import exception.ValidationException;
import util.PasswordUtils;

import java.util.List;
import java.util.logging.Logger;

/**
 * Service implementation for TaiKhoan business operations
 */
public class TaiKhoanServiceImpl implements ITaiKhoanService {
    private static final Logger LOGGER = Logger.getLogger(TaiKhoanServiceImpl.class.getName());
    
    private final ITaiKhoanDAO taiKhoanDAO;
    private final INhanVienDAO nhanVienDAO;
    private final TaiKhoanValidator validator;
    
    public TaiKhoanServiceImpl(ITaiKhoanDAO taiKhoanDAO, INhanVienDAO nhanVienDAO) {
        this.taiKhoanDAO = taiKhoanDAO;
        this.nhanVienDAO = nhanVienDAO;
        this.validator = new TaiKhoanValidator();
    }
    
    @Override
    public TaiKhoan authenticate(String username, String password) throws BusinessException {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("Tên đăng nhập không được để trống");
        }
        
        if (password == null || password.isEmpty()) {
            throw new BusinessException("Mật khẩu không được để trống");
        }
        
        try {
            TaiKhoan account = taiKhoanDAO.findById(username.trim());
            if (account == null) {
                throw new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng");
            }
            
            // Verify password
            if (!PasswordUtils.checkPassword(password, account.getMatKhau())) {
                throw new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng");
            }
            
            LOGGER.info("User authenticated successfully: " + username);
            return account;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Authentication failed for user " + username + ": " + e.getMessage());
            throw new BusinessException("Lỗi xác thực tài khoản", e);
        }
    }
    
    @Override
    public void createAccount(TaiKhoan account) throws ValidationException, BusinessException {
        // Validate input
        ValidationResult validationResult = validator.validateForCreation(account);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }
        
        try {
            // Business rule: Check if username already exists
            if (usernameExists(account.getTenDangNhap())) {
                throw new BusinessException("Tên đăng nhập đã tồn tại");
            }
            
            // Business rule: Verify employee exists
            if (account.getMaNV() != null) {
                NhanVien employee = nhanVienDAO.findById(Integer.parseInt(account.getMaNV().getId().toString()));
                if (employee == null) {
                    throw new BusinessException("Nhân viên không tồn tại");
                }
            }
            
            // Hash password before saving
            String hashedPassword = PasswordUtils.hashPassword(account.getMatKhau());
            account.setMatKhau(hashedPassword);
            
            LOGGER.info("Creating new account: " + account.getTenDangNhap());
            taiKhoanDAO.insert(account);
            LOGGER.info("Account created successfully");
            
        } catch (ValidationException e) {
            throw e;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to create account: " + e.getMessage());
            throw new BusinessException("Không thể tạo tài khoản mới", e);
        }
    }
    
    @Override
    public void updateAccount(TaiKhoan account) throws ValidationException, BusinessException {
        // Validate input
        ValidationResult validationResult = validator.validateForUpdate(account);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }
        
        try {
            // Business rule: Check if account exists
            TaiKhoan existingAccount = getAccountByUsername(account.getTenDangNhap());
            if (existingAccount == null) {
                throw new BusinessException("Tài khoản không tồn tại");
            }
            
            // Handle password update
            if (account.getMatKhau() == null || account.getMatKhau().trim().isEmpty()) {
                // Keep existing password
                account.setMatKhau(existingAccount.getMatKhau());
            } else {
                // Check if password is already hashed (update from UI might pass unhashed password)
                if (!PasswordUtils.checkPassword(account.getMatKhau(), existingAccount.getMatKhau())) {
                    // Hash new password
                    String hashedPassword = PasswordUtils.hashPassword(account.getMatKhau());
                    account.setMatKhau(hashedPassword);
                } else {
                    // Same password, keep existing hash
                    account.setMatKhau(existingAccount.getMatKhau());
                }
            }
            
            LOGGER.info("Updating account: " + account.getTenDangNhap());
            taiKhoanDAO.update(account);
            LOGGER.info("Account updated successfully");
            
        } catch (ValidationException e) {
            throw e;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to update account: " + e.getMessage());
            throw new BusinessException("Không thể cập nhật tài khoản", e);
        }
    }
    
    @Override
    public void deleteAccount(String username) throws BusinessException {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("Tên đăng nhập không được để trống");
        }
        
        try {
            // Business rule: Check if account exists
            TaiKhoan existingAccount = getAccountByUsername(username);
            if (existingAccount == null) {
                throw new BusinessException("Tài khoản không tồn tại");
            }
            
            // Business rule: Prevent deleting admin accounts (could be enhanced)
            if ("ADMIN".equalsIgnoreCase(existingAccount.getQuyen())) {
                // Count remaining admin accounts
                long adminCount = getAllAccounts().stream()
                    .filter(acc -> "ADMIN".equalsIgnoreCase(acc.getQuyen()))
                    .count();
                
                if (adminCount <= 1) {
                    throw new BusinessException("Không thể xóa tài khoản admin cuối cùng");
                }
            }
            
            LOGGER.info("Deleting account: " + username);
            taiKhoanDAO.delete(username);
            LOGGER.info("Account deleted successfully");
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to delete account: " + e.getMessage());
            throw new BusinessException("Không thể xóa tài khoản", e);
        }
    }
    
    @Override
    public List<TaiKhoan> getAllAccounts() throws BusinessException {
        try {
            LOGGER.fine("Retrieving all accounts");
            return taiKhoanDAO.findAll();
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve all accounts: " + e.getMessage());
            throw new BusinessException("Không thể lấy danh sách tài khoản", e);
        }
    }
    
    @Override
    public TaiKhoan getAccountByUsername(String username) throws BusinessException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        
        try {
            LOGGER.fine("Retrieving account: " + username);
            return taiKhoanDAO.findById(username.trim());
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve account: " + e.getMessage());
            throw new BusinessException("Không thể lấy thông tin tài khoản", e);
        }
    }
    
    @Override
    public boolean usernameExists(String username) throws BusinessException {
        return getAccountByUsername(username) != null;
    }
    
    @Override
    public void changePassword(String username, String oldPassword, String newPassword) 
            throws ValidationException, BusinessException {
        
        // Validate passwords
        ValidationResult validationResult = validator.validatePasswordChange(oldPassword, newPassword);
        if (!validationResult.isValid()) {
            throw new ValidationException(validationResult.getErrors());
        }
        
        try {
            // Get existing account
            TaiKhoan account = getAccountByUsername(username);
            if (account == null) {
                throw new BusinessException("Tài khoản không tồn tại");
            }
            
            // Verify old password
            if (!PasswordUtils.checkPassword(oldPassword, account.getMatKhau())) {
                throw new BusinessException("Mật khẩu hiện tại không đúng");
            }
            
            // Hash and update new password
            String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
            account.setMatKhau(hashedNewPassword);
            
            LOGGER.info("Changing password for account: " + username);
            taiKhoanDAO.update(account);
            LOGGER.info("Password changed successfully");
            
        } catch (ValidationException e) {
            throw e;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.severe("Failed to change password: " + e.getMessage());
            throw new BusinessException("Không thể đổi mật khẩu", e);
        }
    }
    
    @Override
    public NhanVien getEmployeeById(String employeeId) throws BusinessException {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return null;
        }
        
        try {
            LOGGER.fine("Retrieving employee: " + employeeId);
            return nhanVienDAO.findById(Integer.parseInt(employeeId.trim()));
        } catch (NumberFormatException e) {
            throw new BusinessException("ID nhân viên không hợp lệ");
        } catch (Exception e) {
            LOGGER.severe("Failed to retrieve employee: " + e.getMessage());
            throw new BusinessException("Không thể lấy thông tin nhân viên", e);
        }
    }
}
