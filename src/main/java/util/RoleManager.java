package util;

public class RoleManager {

    // Role constants
    public static final String ADMIN = "ADMIN";
    public static final String MANAGER = "MANAGER";
    public static final String STAFF = "STAFF";

    // Check if current user has admin privileges
    public static boolean isAdmin() {
        String role = SessionManager.getInstance().getCurrentUserRole();
        return ADMIN.equalsIgnoreCase(role);
    }

    // Check if current user has manager privileges or higher
    public static boolean isManagerOrHigher() {
        String role = SessionManager.getInstance().getCurrentUserRole();
        return ADMIN.equalsIgnoreCase(role) || MANAGER.equalsIgnoreCase(role);
    }

    // Check if current user has staff privileges or higher (basically any logged in user)
    public static boolean isStaffOrHigher() {
        return SessionManager.getInstance().isLoggedIn();
    }

    // Check if user can access account management (Admin only)
    public static boolean canAccessAccountManagement() {
        return isAdmin();
    }

    // Check if user can access employee management (Admin only)
    public static boolean canAccessEmployeeManagement() {
        return isAdmin();
    }

    // Check if user can access product configuration (Admin and Manager)
    public static boolean canAccessProductConfiguration() {
        return isManagerOrHigher();
    }

    // Check if user can access customer management (Admin, Manager, Staff can view/add)
    public static boolean canAccessCustomerManagement() {
        return isStaffOrHigher();
    }

    // Check if user can access sales management
    public static boolean canAccessSalesManagement() {
        return isStaffOrHigher();
    }

    // Check if user can modify products (Admin and Manager only)
    public static boolean canModifyProducts() {
        return isManagerOrHigher();
    }

    // Check if user can view all invoices (Admin and Manager)
    public static boolean canViewAllInvoices() {
        return isManagerOrHigher();
    }
    
    // New feature permissions
    
    // Reports & Analytics
    public static boolean canAccessReports() {
        return isManagerOrHigher(); // Admin and Manager can access reports
    }
    
    public static boolean canCreateReports() {
        return isManagerOrHigher();
    }
    
    public static boolean canExportReports() {
        return isManagerOrHigher();
    }
    
    // Supplier Management  
    public static boolean canAccessSupplierManagement() {
        return isManagerOrHigher(); // Admin and Manager can manage suppliers
    }
    
    public static boolean canCreateSuppliers() {
        return isManagerOrHigher();
    }
    
    public static boolean canEditSuppliers() {
        return isManagerOrHigher();
    }
    
    public static boolean canDeleteSuppliers() {
        return isAdmin(); // Only Admin can delete suppliers
    }
    
    // Purchase Orders
    public static boolean canAccessPurchaseOrders() {
        return isManagerOrHigher(); // Admin and Manager can manage purchase orders
    }
    
    public static boolean canCreatePurchaseOrders() {
        return isManagerOrHigher();
    }
    
    public static boolean canApprovePurchaseOrders() {
        return isManagerOrHigher();
    }
    
    public static boolean canCancelPurchaseOrders() {
        return isManagerOrHigher();
    }
    
    // Promotions
    public static boolean canAccessPromotions() {
        return isManagerOrHigher(); // Admin and Manager can manage promotions
    }
    
    public static boolean canCreatePromotions() {
        return isManagerOrHigher();
    }
    
    public static boolean canActivatePromotions() {
        return isManagerOrHigher();
    }
    
    // Payment Methods
    public static boolean canAccessPaymentMethods() {
        return isManagerOrHigher(); // Admin and Manager can manage payment methods
    }
    
    public static boolean canConfigurePaymentMethods() {
        return isAdmin(); // Only Admin can configure payment methods
    }
    
    // Returns & Exchanges
    public static boolean canAccessReturns() {
        return isStaffOrHigher(); // All staff can handle returns
    }
    
    public static boolean canCreateReturns() {
        return isStaffOrHigher();
    }
    
    public static boolean canApproveReturns() {
        return isManagerOrHigher(); // Only Manager and above can approve returns
    }
    
    public static boolean canProcessRefunds() {
        return isManagerOrHigher();
    }
    
    // Loyalty System
    public static boolean canAccessLoyalty() {
        return isStaffOrHigher(); // All staff can access loyalty features
    }
    
    public static boolean canCreateLoyaltyCards() {
        return isStaffOrHigher();
    }
    
    public static boolean canManageLoyaltyProgram() {
        return isManagerOrHigher(); // Only Manager and above can manage loyalty program
    }
    
    public static boolean canAdjustPoints() {
        return isManagerOrHigher(); // Only Manager and above can manually adjust points
    }
    
    // Show access denied message with specific feature and required role
    public static void showAccessDeniedMessage(java.awt.Component parent, String feature, String requiredRole) {
        String message = String.format(
            "Bạn không có quyền truy cập tính năng '%s'.\nYêu cầu quyền: %s\nQuyền hiện tại: %s",
            feature, 
            requiredRole,
            getCurrentUserRoleText()
        );
        javax.swing.JOptionPane.showMessageDialog(parent, message, "Không có quyền truy cập", 
                                                 javax.swing.JOptionPane.WARNING_MESSAGE);
    }
    
    // Enhanced access denied message (overload existing method)
    public static void showAccessDeniedMessage(java.awt.Component parent, String requiredRole) {
        showAccessDeniedMessage(parent, "này", requiredRole);
    }
    
    private static String getCurrentUserRoleText() {
        String role = SessionManager.getInstance().getCurrentUserRole();
        if (role == null) return "Chưa đăng nhập";
        
        switch (role.toUpperCase()) {
            case "ADMIN": return "Quản trị viên";
            case "MANAGER": return "Quản lý";
            case "STAFF": return "Nhân viên";
            default: return role;
        }
    }

    // Show access denied message
    public static void showAccessDeniedMessage(java.awt.Component parent) {
        showAccessDeniedMessage(parent, "này", "Admin");
    }
}
