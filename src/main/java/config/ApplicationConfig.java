package config;

import di.ServiceContainer;
import dao.interfaces.*;
import dao.impl.*;
import service.interfaces.*;
import service.impl.*;

import java.util.logging.Logger;

/**
 * Application configuration class following Factory pattern
 * Configures dependency injection container and initializes services
 */
public class ApplicationConfig {
    private static final Logger LOGGER = Logger.getLogger(ApplicationConfig.class.getName());
    private static boolean initialized = false;
    
    /**
     * Initialize application configuration
     * Sets up dependency injection container with all services and DAOs
     */
    public static synchronized void initialize() {
        if (initialized) {
            LOGGER.info("Application already initialized");
            return;
        }
        
        LOGGER.info("Initializing application configuration...");
        
        ServiceContainer container = ServiceContainer.getInstance();
        
        // Register DAOs as singletons
        registerDAOs(container);
        
        // Register Services as singletons
        registerServices(container);
        
        // Register Validators
        registerValidators(container);
        
        initialized = true;
        LOGGER.info("Application configuration initialized successfully");
    }
    
    /**
     * Register all DAO implementations
     */
    private static void registerDAOs(ServiceContainer container) {
        LOGGER.fine("Registering DAOs...");
        
        // Register DAO implementations as singletons
        container.registerSingleton(ISanPhamDAO.class, new SanPhamDAO());
        container.registerSingleton(IDanhMucDAO.class, new DanhMucDAO());
        container.registerSingleton(ITaiKhoanDAO.class, new TaiKhoanDAO());
        container.registerSingleton(INhanVienDAO.class, new NhanVienDAO());
        container.registerSingleton(IKhachHangDAO.class, new KhachHangDAO());
        container.registerSingleton(IHoaDonDAO.class, new HoaDonDAO());
        container.registerSingleton(IChiTietHoaDonDAO.class, new ChiTietHoaDonDAO());
        container.registerSingleton(IBienTheSanPhamDAO.class, new BienTheSanPhamDAO());
        container.registerSingleton(IMauSacDAO.class, new MauSacDAO());
        container.registerSingleton(IKichThuocDAO.class, new KichThuocDAO());
        
        LOGGER.fine("DAOs registered successfully");
    }
    
    /**
     * Register all Service implementations
     */
    private static void registerServices(ServiceContainer container) {
        LOGGER.fine("Registering Services...");
        
        // Register services using factory methods for dependency injection
        container.registerSingletonFactory(ISanPhamService.class, () -> {
            ISanPhamDAO sanPhamDAO = container.getService(ISanPhamDAO.class);
            IDanhMucDAO danhMucDAO = container.getService(IDanhMucDAO.class);
            return new SanPhamServiceImpl(sanPhamDAO, danhMucDAO);
        });
        
        container.registerSingletonFactory(ITaiKhoanService.class, () -> {
            ITaiKhoanDAO taiKhoanDAO = container.getService(ITaiKhoanDAO.class);
            INhanVienDAO nhanVienDAO = container.getService(INhanVienDAO.class);
            return new TaiKhoanServiceImpl(taiKhoanDAO, nhanVienDAO);
        });
        
        // Add more services as they are implemented
        // container.registerSingletonFactory(IKhachHangService.class, () -> { ... });
        // container.registerSingletonFactory(IHoaDonService.class, () -> { ... });
        
        LOGGER.fine("Services registered successfully");
    }
    
    /**
     * Register validators
     */
    private static void registerValidators(ServiceContainer container) {
        LOGGER.fine("Registering Validators...");
        
        // Validators can be registered as singletons since they're stateless
        container.registerSingleton(validation.SanPhamValidator.class, new validation.SanPhamValidator());
        container.registerSingleton(validation.TaiKhoanValidator.class, new validation.TaiKhoanValidator());
        
        LOGGER.fine("Validators registered successfully");
    }
    
    /**
     * Get service from container
     */
    public static <T> T getService(Class<T> serviceClass) {
        if (!initialized) {
            throw new IllegalStateException("Application not initialized. Call initialize() first.");
        }
        
        return ServiceContainer.getInstance().getService(serviceClass);
    }
    
    /**
     * Check if application is initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Shutdown application and cleanup resources
     */
    public static void shutdown() {
        if (!initialized) {
            return;
        }
        
        LOGGER.info("Shutting down application...");
        
        try {
            // Close EntityManagerFactory
            util.EntityManagerUtil.closeEntityManagerFactory();
            
            // Clear service container
            ServiceContainer.getInstance().clear();
            
            initialized = false;
            LOGGER.info("Application shutdown completed");
            
        } catch (Exception e) {
            LOGGER.severe("Error during application shutdown: " + e.getMessage());
        }
    }
}
