package main;

import jakarta.persistence.*;

public class Health {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("QuanLyCuaHangPU");
            EntityManager em = emf.createEntityManager();

            System.out.println("✅ Kết nối JPA thành công!");

            em.close();
            emf.close();
        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối JPA: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
