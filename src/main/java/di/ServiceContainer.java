package di;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Simple Dependency Injection container following Singleton pattern
 * Provides service registration and retrieval with lifecycle management
 */
public class ServiceContainer {
    private static final Logger LOGGER = Logger.getLogger(ServiceContainer.class.getName());
    private static ServiceContainer instance;
    
    // Service instances (singletons)
    private final Map<Class<?>, Object> singletonServices = new ConcurrentHashMap<>();
    
    // Service factories for creating instances
    private final Map<Class<?>, Supplier<?>> serviceFactories = new ConcurrentHashMap<>();
    
    // Service metadata
    private final Map<Class<?>, ServiceLifecycle> serviceLifecycles = new ConcurrentHashMap<>();
    
    private ServiceContainer() {
        LOGGER.info("ServiceContainer initialized");
    }
    
    /**
     * Get ServiceContainer instance (Singleton)
     */
    public static synchronized ServiceContainer getInstance() {
        if (instance == null) {
            instance = new ServiceContainer();
        }
        return instance;
    }
    
    /**
     * Register a singleton service
     */
    public <T> void registerSingleton(Class<T> serviceClass, T serviceInstance) {
        if (serviceClass == null || serviceInstance == null) {
            throw new IllegalArgumentException("Service class and instance cannot be null");
        }
        
        singletonServices.put(serviceClass, serviceInstance);
        serviceLifecycles.put(serviceClass, ServiceLifecycle.SINGLETON);
        LOGGER.info("Registered singleton service: " + serviceClass.getSimpleName());
    }
    
    /**
     * Register a service factory for transient instances
     */
    public <T> void registerTransient(Class<T> serviceClass, Supplier<T> factory) {
        if (serviceClass == null || factory == null) {
            throw new IllegalArgumentException("Service class and factory cannot be null");
        }
        
        serviceFactories.put(serviceClass, factory);
        serviceLifecycles.put(serviceClass, ServiceLifecycle.TRANSIENT);
        LOGGER.info("Registered transient service: " + serviceClass.getSimpleName());
    }
    
    /**
     * Register a service factory for singleton instances (lazy initialization)
     */
    public <T> void registerSingletonFactory(Class<T> serviceClass, Supplier<T> factory) {
        if (serviceClass == null || factory == null) {
            throw new IllegalArgumentException("Service class and factory cannot be null");
        }
        
        serviceFactories.put(serviceClass, factory);
        serviceLifecycles.put(serviceClass, ServiceLifecycle.SINGLETON);
        LOGGER.info("Registered singleton factory: " + serviceClass.getSimpleName());
    }
    
    /**
     * Get service instance
     */
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        if (serviceClass == null) {
            throw new IllegalArgumentException("Service class cannot be null");
        }
        
        // Check if it's a registered singleton instance
        Object singletonInstance = singletonServices.get(serviceClass);
        if (singletonInstance != null) {
            return (T) singletonInstance;
        }
        
        // Check if there's a factory
        Supplier<?> factory = serviceFactories.get(serviceClass);
        if (factory != null) {
            ServiceLifecycle lifecycle = serviceLifecycles.get(serviceClass);
            
            if (lifecycle == ServiceLifecycle.SINGLETON) {
                // Create singleton instance and cache it
                synchronized (this) {
                    singletonInstance = singletonServices.get(serviceClass);
                    if (singletonInstance == null) {
                        singletonInstance = factory.get();
                        singletonServices.put(serviceClass, singletonInstance);
                        LOGGER.fine("Created singleton instance: " + serviceClass.getSimpleName());
                    }
                    return (T) singletonInstance;
                }
            } else {
                // Create transient instance
                T instance = (T) factory.get();
                LOGGER.fine("Created transient instance: " + serviceClass.getSimpleName());
                return instance;
            }
        }
        
        throw new ServiceNotFoundException("Service not registered: " + serviceClass.getName());
    }
    
    /**
     * Check if service is registered
     */
    public boolean isRegistered(Class<?> serviceClass) {
        return singletonServices.containsKey(serviceClass) || serviceFactories.containsKey(serviceClass);
    }
    
    /**
     * Get service lifecycle
     */
    public ServiceLifecycle getServiceLifecycle(Class<?> serviceClass) {
        return serviceLifecycles.get(serviceClass);
    }
    
    /**
     * Get all registered service classes
     */
    public Map<Class<?>, ServiceLifecycle> getRegisteredServices() {
        return new HashMap<>(serviceLifecycles);
    }
    
    /**
     * Clear all services (useful for testing)
     */
    public void clear() {
        singletonServices.clear();
        serviceFactories.clear();
        serviceLifecycles.clear();
        LOGGER.info("ServiceContainer cleared");
    }
    
    /**
     * Service lifecycle enumeration
     */
    public enum ServiceLifecycle {
        SINGLETON,  // One instance per container
        TRANSIENT   // New instance every time
    }
    
    /**
     * Exception thrown when service is not found
     */
    public static class ServiceNotFoundException extends RuntimeException {
        public ServiceNotFoundException(String message) {
            super(message);
        }
    }
}
