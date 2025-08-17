package util;

import javax.swing.*;

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

    // Show access denied message
    public static void showAccessDeniedMessage(java.awt.Component parent) {
        JOptionPane.showMessageDialog(parent,
            "Bạn không có quyền truy cập chức năng này!\n" +
            "Chỉ Admin mới có thể thực hiện thao tác này.",
            "Từ chối truy cập",
            JOptionPane.ERROR_MESSAGE);
    }

    // Show access denied message with custom text
    public static void showAccessDeniedMessage(java.awt.Component parent, String requiredRole) {
        JOptionPane.showMessageDialog(parent,
            "Bạn không có quyền truy cập chức năng này!\n" +
            "Yêu cầu quyền: " + requiredRole,
            "Từ chối truy cập",
            JOptionPane.ERROR_MESSAGE);
    }
}
