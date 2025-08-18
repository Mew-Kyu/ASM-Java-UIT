# 🏪 Quản Lý Cửa Hàng Quần Áo (Clothing Store Management System)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JPA](https://img.shields.io/badge/JPA-3.2.0-blue.svg)](https://jakarta.ee/specifications/persistence/)
[![Hibernate](https://img.shields.io/badge/Hibernate-7.0.8-green.svg)](https://hibernate.org/)

## 📋 Table of Contents

- [Overview](#overview)
- [🏗️ Architecture](#architecture)
- [🎯 Design Patterns](#design-patterns)
- [🚀 Features](#features)
- [📁 Project Structure](#project-structure)
- [⚙️ Setup & Installation](#setup--installation)
- [🔧 Configuration](#configuration)
- [📊 Database Schema](#database-schema)
- [🎨 User Interface](#user-interface)
- [🔐 Security & Authentication](#security--authentication)


## Overview

This is a clothing store management system designed to streamline retail operations for clothing businesses. The application provides comprehensive tools for inventory management, sales processing, customer relationship management, and employee administration, all built with  and modern software development practices.

## 🏗️ Architecture

### **Clean Architecture Implementation**

The application follows **Clean Architecture** principles with clear separation of concerns across multiple layers:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │   Swing UI      │───▶│      Controllers            │ │
│  │   Components    │    │   (UI-Service Bridge)       │ │
│  └─────────────────┘    └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────┐
│                  Business Logic Layer                   │
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │    Services     │───▶│       Validators            │ │
│  │ (Business Logic)│    │   (Business Rules)          │ │
│  └─────────────────┘    └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────┐
│                  Data Access Layer                      │
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │  DAO Interfaces │───▶│    DAO Implementations      │ │
│  │   (Contracts)   │    │     (Data Access)           │ │
│  └─────────────────┘    └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                 │
│  ┌─────────────────┐    ┌─────────────────────────────┐ │
│  │  DI Container   │    │   EntityManager Utility     │ │
│  │   Exception     │    │     Database Config         │ │
│  │   Framework     │    │     Session Management      │ │
│  └─────────────────┘    └─────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────┐
│                      Domain Layer                       │
│  ┌─────────────────────────────────────────────────────┐ │
│  │              JPA Entities (Domain Models)           │ │
│  │   SanPham │ TaiKhoan │ NhanVien │ HoaDon │ etc.    │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## 🎯 Design Patterns

This project implements **8 professional design patterns** following Gang of Four and enterprise patterns:

### **1. 🔧 Singleton Pattern**
**Implementation**: `ServiceContainer`, `EntityManagerUtil`, `SessionManager`

```java
public class ServiceContainer {
    private static ServiceContainer instance;
    
    public static synchronized ServiceContainer getInstance() {
        if (instance == null) {
            instance = new ServiceContainer();
        }
        return instance;
    }
}
```

### **2. 📋 Template Method Pattern**
**Implementation**: `BaseDAO<T, ID>` abstract class

```java
public abstract class BaseDAO<T, ID> {
    // Template method defining algorithm structure
    public void insert(T entity) {
        validateEntity(entity);  // Hook method
        EntityManagerUtil.executeInTransaction(em -> {
            em.persist(entity);
        });
    }
    
    // Hook method for subclasses to override
    protected void validateEntity(T entity) {
        // Default validation
    }
}
```

### **3. 🎭 Strategy Pattern**
**Implementation**: `Validator<T>` interface with multiple implementations

```java
public interface Validator<T> {
    ValidationResult validate(T object);
}

public class SanPhamValidator implements Validator<SanPham> {
    public ValidationResult validate(SanPham product) {
        // Product-specific validation logic
    }
}
```

### **4. 🏭 Factory Pattern**
**Implementation**: `ApplicationConfig` for service creation

```java
public class ApplicationConfig {
    private static void registerServices(ServiceContainer container) {
        container.registerSingletonFactory(ISanPhamService.class, () -> {
            ISanPhamDAO dao = container.getService(ISanPhamDAO.class);
            return new SanPhamServiceImpl(dao);
        });
    }
}
```

### **5. 📦 Repository Pattern**
**Implementation**: DAO layer with interfaces and implementations

```java
public interface ISanPhamDAO {
    void insert(SanPham sp);
    Optional<SanPham> findById(int id);
    List<SanPham> findAll();
}

public class SanPhamDAO extends BaseDAO<SanPham, Integer> implements ISanPhamDAO {
    // Repository implementation
}
```

### **6. 💉 Dependency Injection Pattern**
**Implementation**: Custom DI container with lifecycle management

```java
// Registration
container.registerSingleton(ISanPhamDAO.class, new SanPhamDAO());

// Injection
public class SanPhamController {
    private final ISanPhamService service;
    
    public SanPhamController() {
        this.service = ApplicationConfig.getService(ISanPhamService.class);
    }
}
```

### **7. 🏛️ MVC (Model-View-Controller) Pattern**
**Implementation**: Separation between UI, controllers, and models

```java
// Model (Domain)
@Entity public class SanPham { /* JPA Entity */ }

// View (Presentation)
public class SanPhamUI extends JFrame { /* Swing UI */ }

// Controller (Coordination)
public class SanPhamController { /* UI-Service Bridge */ }
```

### **8. 🎯 Command Pattern**
**Implementation**: Service methods as commands with validation and error handling

```java
public class SanPhamServiceImpl implements ISanPhamService {
    public void createProduct(SanPham product) throws ValidationException, BusinessException {
        // Validation Command
        ValidationResult result = validator.validate(product);
        if (!result.isValid()) {
            throw new ValidationException(result.getErrors());
        }
        
        // Business Logic Command
        if (isProductNameExists(product.getName())) {
            throw new BusinessException("Product name already exists");
        }
        
        // Persistence Command
        productDAO.insert(product);
    }
}
```

## ✅ SOLID Principles

### **S - Single Responsibility Principle**
✅ **Implemented**: Each class has one reason to change
- **Controllers**: Only handle UI-Service communication
- **Services**: Only contain business logic
- **DAOs**: Only handle data access
- **Validators**: Only handle validation logic

### **O - Open/Closed Principle**
✅ **Implemented**: Open for extension, closed for modification
- `BaseDAO` can be extended without modification
- New validators can be added without changing existing code
- Service interfaces allow new implementations

### **L - Liskov Substitution Principle**
✅ **Implemented**: Derived classes are substitutable for base classes
- All DAO implementations are interchangeable through interfaces
- Service implementations follow their contracts exactly

### **I - Interface Segregation Principle**
✅ **Implemented**: Clients depend only on interfaces they use
- Focused interfaces: `ISanPhamDAO`, `ITaiKhoanDAO`, etc.
- No fat interfaces with unused methods

### **D - Dependency Inversion Principle**
✅ **Implemented**: Depend on abstractions, not concretions
- High-level modules depend on interfaces
- Dependencies injected through constructor
- Easy to swap implementations

## 🚀 Features

### **👥 User Management**
- **Multi-role authentication** (Admin, Manager, Staff)
- **Secure password hashing** with BCrypt
- **Session management** with automatic timeout
- **Role-based access control**

### **📦 Product Management**
- **CRUD operations** for products
- **Category management** with hierarchical structure
- **Product variant management** (size, color, quantity)
- **Advanced search and filtering**

### **🧾 Sales Management**
- **Invoice creation and management**
- **Real-time inventory tracking**
- **Customer information management**
- **PDF invoice generation**

### **👔 Employee Management**
- **Employee profile management**
- **Role assignment and management**
- **Work status tracking**

### **🔐 Security Features**
- **Authentication and authorization**
- **Session management**
- **Input validation and sanitization**
- **SQL injection prevention**

## 📁 Project Structure

```
src/main/java/
├── 📋 config/
│   └── ApplicationConfig.java          # 🏭 Factory pattern for DI setup
├── 🎮 controller/
│   ├── SanPhamController.java          # 🎯 Improved with service layer
│   ├── TaiKhoanController.java         # 👤 Account management
│   ├── HoaDonController.java           # 🧾 Invoice operations
│   └── ...                             # Other controllers
├── 🗄️ dao/
│   ├── base/
│   │   └── BaseDAO.java                # 📋 Template Method pattern
│   ├── interfaces/
│   │   ├── ISanPhamDAO.java            # 📦 Repository pattern
│   │   ├── ITaiKhoanDAO.java           # 👤 Account data access
│   │   └── ...                         # Other DAO interfaces
│   └── impl/
│       ├── SanPhamDAO.java             # 🏪 Product data implementation
│       ├── TaiKhoanDAO.java            # 🔑 Account data implementation
│       └── ...                         # Other implementations
├── 💉 di/
│   └── ServiceContainer.java           # 🔧 Singleton DI container
├── ⚠️ exception/
│   ├── BusinessException.java          # 📋 Business logic errors
│   ├── DAOException.java              # 🗄️ Data access errors
│   └── ValidationException.java        # ✅ Validation errors
├── 🏢 service/
│   ├── interfaces/
│   │   ├── ISanPhamService.java        # 🏪 Product business logic
│   │   ├── ITaiKhoanService.java       # 👤 Account business logic
│   │   └── ...                         # Other service interfaces
│   └── impl/
│       ├── SanPhamServiceImpl.java     # 🏪 Product service implementation
│       ├── TaiKhoanServiceImpl.java    # 👤 Account service implementation
│       └── ...                         # Other implementations
├── 🛠️ util/
│   ├── EntityManagerUtil.java          # 🔧 Centralized EM management
│   ├── SessionManager.java             # 🔒 Session management
│   ├── RoleManager.java                # 👥 Role-based access control
│   ├── PasswordUtils.java              # 🔐 Password encryption
│   └── PDFInvoiceGenerator.java        # 📄 PDF generation
├── ✅ validation/
│   ├── Validator.java                  # 🎭 Strategy pattern interface
│   ├── ValidationResult.java           # 📋 Validation response
│   ├── SanPhamValidator.java          # 🏪 Product validation
│   ├── TaiKhoanValidator.java         # 👤 Account validation
│   └── ...                             # Other validators
├── 🎨 view/
│   ├── BaseAuthenticatedUI.java        # 🔒 Base UI with authentication
│   ├── LoginUI.java                    # 🚪 Login interface
│   ├── MainMenuUI.java                 # 🏠 Main dashboard
│   ├── SanPhamUI.java                  # 🏪 Product management UI
│   ├── TaiKhoanUI.java                 # 👤 Account management UI
│   └── ...                             # Other UI components
├── 📊 model/
│   ├── SanPham.java                    # 🏪 Product entity
│   ├── TaiKhoan.java                   # 👤 Account entity
│   ├── NhanVien.java                   # 👔 Employee entity
│   ├── HoaDon.java                     # 🧾 Invoice entity
│   └── ...                             # Other domain models
└── 🚀 main/
    └── Main.java                       # 🎯 Application entry point
```

## ⚙️ Setup & Installation

### **📋 Prerequisites**
- **Java 21** or higher
- **Maven 3.8+**
- **SQL Server** (or compatible database)
- **IDE** with Java support (IntelliJ IDEA, Eclipse, VS Code)

### **🚀 Installation Steps**

1. **Clone the repository**
```bash
git clone <repository-url>
cd clothing-store-management
```

2. **Configure Database**
```bash
# Create database
CREATE DATABASE QuanLyCuaHangQuanAo;

# Run SQL script
sqlcmd -i script.sql
```

3. **Update Database Configuration**
Edit `src/main/resources/META-INF/persistence.xml`:
```xml
<property name="jakarta.persistence.jdbc.url" 
          value="jdbc:sqlserver://localhost:1433;databaseName=QuanLyCuaHangQuanAo;trustServerCertificate=true"/>
<property name="jakarta.persistence.jdbc.user" value="your_username"/>
<property name="jakarta.persistence.jdbc.password" value="your_password"/>
```

4. **Build the Project**
```bash
mvn clean compile
```

5. **Run the Application**
```bash
mvn exec:java -Dexec.mainClass="main.Main"
```

### **🐳 Docker Setup (Optional)**
```bash
# Build Docker image
docker build -t clothing-store-app .

# Run with Docker Compose
docker-compose up -d
```

## 🔧 Configuration

### **Database Configuration**
The application uses **HikariCP** connection pooling for optimal performance:

```xml
<property name="hibernate.connection.provider_class" 
          value="org.hibernate.hikaricp.internal.HikariCPConnectionProvider"/>
<property name="hibernate.hikari.minimumIdle" value="3"/>
<property name="hibernate.hikari.maximumPoolSize" value="10"/>
<property name="hibernate.hikari.connectionTimeout" value="30000"/>
```

### **Logging Configuration**
Comprehensive logging is implemented using `java.util.logging`:

```java
# Application logs all operations with different levels:
# - INFO: Application lifecycle events
# - FINE: Business operations
# - WARNING: Recoverable errors
# - SEVERE: Critical errors
```

### **Security Configuration**
- **Password Hashing**: BCrypt with salt
- **Session Timeout**: Configurable timeout settings
- **Role-Based Access**: Granular permission system

## 📊 Database Schema

### **Core Entities**

```sql
-- Products
SanPham (MaSP, TenSP, MaDM, MoTa)

-- Categories  
DanhMuc (MaDM, TenDM)

-- Product Variants
BienTheSanPham (MaBienThe, MaSP, MaMauSac, MaKichThuoc, SoLuong, GiaBan)

-- Employees
NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, DienThoai, Email, DiaChi, TrangThai)

-- Accounts
TaiKhoan (TenDangNhap, MatKhau, MaNV, Quyen)

-- Customers
KhachHang (MaKH, HoTen, DienThoai, Email, DiaChi)

-- Invoices
HoaDon (MaHD, MaKH, MaNV, NgayLap, TongTien)

-- Invoice Details
ChiTietHoaDon (MaHD, MaBienThe, SoLuong, GiaBan)
```

### **Relationships**
- **One-to-Many**: DanhMuc → SanPham
- **One-to-Many**: SanPham → BienTheSanPham  
- **One-to-One**: NhanVien → TaiKhoan
- **One-to-Many**: NhanVien → HoaDon
- **Many-to-Many**: HoaDon ↔ BienTheSanPham (through ChiTietHoaDon)

## 🎨 User Interface

### **🎯 Design Principles**
- **Responsive Layout**: Adapts to different screen sizes
- **Intuitive Navigation**: Clear menu structure
- **Role-Based UI**: Different interfaces for different user roles
- **Consistent Styling**: Uniform look and feel across all screens

### **🔐 Authentication Flow**
```
Login Screen → Role Verification → Main Dashboard → Feature Access
```

### **👥 Role-Based Features**

| Role | Product Mgmt | Employee Mgmt | Account Mgmt | Sales | Reports |
|------|--------------|---------------|--------------|-------|---------|
| **Admin** | ✅ Full | ✅ Full | ✅ Full | ✅ Full | ✅ Full |
| **Manager** | ✅ Full | ❌ View Only | ❌ None | ✅ Full | ✅ Full |
| **Staff** | 👁️ View Only | ❌ None | ❌ None | ✅ Limited | 👁️ View Only |

## 🔐 Security & Authentication

### **🔒 Authentication System**
- **Secure Login**: Username/password with encryption
- **Session Management**: Automatic timeout and cleanup
- **Password Security**: BCrypt hashing with salt
- **Brute Force Protection**: Account lockout mechanisms

### **👥 Authorization System**
- **Role-Based Access Control (RBAC)**
- **Granular Permissions**: Feature-level access control
- **UI Security**: Dynamic button enabling/disabling
- **API Security**: Service-level permission checks

### **🛡️ Security Best Practices**
- **Input Validation**: Comprehensive validation at all layers
- **SQL Injection Prevention**: Parameterized queries
- **XSS Prevention**: Input sanitization
- **Error Handling**: No sensitive information in error messages

