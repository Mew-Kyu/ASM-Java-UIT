# 🔄 HƯỚNG DẪN QUẢN LÝ ĐỔI TRẢ HÀNG

## 📋 Tổng quan chức năng

Hệ thống quản lý đổi trả hàng cho phép:
- **Tạo phiếu đổi/trả** từ hóa đơn gốc
- **Quản lý quy trình phê duyệt** (Pending → Approved → Completed)
- **Tính toán tự động** giá trị hoàn trả/bổ sung
- **Cập nhật tồn kho** tự động
- **Theo dõi lịch sử** đổi trả

---

## 🎯 Quy trình đổi trả hàng

### 1. **TẠO PHIẾU ĐỔI TRẢ MỚI**

#### Bước 1: Kiểm tra điều kiện
```java
// Điều kiện hóa đơn có thể đổi trả:
- Hóa đơn đã thanh toán (COMPLETED)
- Trong thời hạn đổi trả (30 ngày)
- Chưa có phiếu đổi trả trước đó
```

#### Bước 2: Nhập thông tin phiếu
- **Mã hóa đơn**: Nhập và kiểm tra tính hợp lệ
- **Loại phiếu**: 
  - `DOI` - Đổi sản phẩm khác
  - `TRA` - Trả hàng lấy tiền
- **Lý do đổi trả**: Bắt buộc nhập
- **Khách hàng**: Tự động load từ hóa đơn

#### Bước 3: Thêm sản phẩm đổi/trả
```java
// Thông tin chi tiết sản phẩm:
- Mã biến thể sản phẩm
- Loại: "TRA" (trả) hoặc "DOI" (đổi)
- Số lượng
- Đơn giá (theo hóa đơn gốc)
- Tình trạng: MOI, DA_SU_DUNG, LOI, HONG
- Lý do chi tiết
```

#### Bước 4: Tính toán tự động
```java
// Hệ thống tự động tính:
tongGiaTriTra = sum(sanPhamTra.thanhTien)
tongGiaTriDoi = sum(sanPhamDoi.thanhTien)
chenhLech = tongGiaTriTra - tongGiaTriDoi

if (chenhLech > 0) {
    soTienHoanLai = chenhLech;  // Khách được hoàn tiền
    soTienBoSung = 0;
} else if (chenhLech < 0) {
    soTienHoanLai = 0;
    soTienBoSung = -chenhLech;  // Khách trả thêm
} else {
    soTienHoanLai = soTienBoSung = 0;  // Bằng nhau
}
```

---

### 2. **SỬA PHIẾU ĐỔI TRẢ**

#### Điều kiện:
- Chỉ sửa được phiếu ở trạng thái `PENDING` (Chờ xử lý)
- Có quyền Staff trở lên

#### Thao tác:
1. Chọn phiếu trong danh sách
2. Click "Sửa" 
3. Cập nhật thông tin
4. Lưu thay đổi

---

### 3. **XÓA PHIẾU ĐỔI TRẢ**

#### Điều kiện:
- Chỉ xóa được phiếu ở trạng thái `PENDING`
- Xác nhận trước khi xóa

#### Code xử lý:
```java
public void xoaPhieuDoiTra(int maPhieuDT) throws BusinessException {
    // Kiểm tra trạng thái
    PhieuDoiTra phieu = phieuDoiTraDAO.findById(maPhieuDT);
    if (!"PENDING".equals(phieu.getTrangThai())) {
        throw new BusinessException("Chỉ có thể xóa phiếu ở trạng thái chờ xử lý");
    }
    
    // Xóa chi tiết trước, sau đó xóa phiếu
    chiTietDAO.deleteByPhieuDoiTra(maPhieuDT);
    phieuDoiTraDAO.delete(maPhieuDT);
}
```

---

### 4. **PHÊ DUYỆT PHIẾU**

#### Quyền truy cập:
- **Manager** hoặc **Admin** mới có quyền phê duyệt
- Kiểm tra: `RoleManager.canApproveReturns()`

#### Quy trình:
```java
// PENDING → APPROVED
public void pheDuyetPhieu(int maPhieuDT, int nguoiDuyet, String ghiChu) {
    phieu.setTrangThai("APPROVED");
    phieu.setNguoiDuyet(nguoiDuyet);
    phieu.setNgayDuyet(LocalDateTime.now());
    phieu.setGhiChu(ghiChu);
    
    phieuDoiTraDAO.update(phieu);
}
```

#### UI thao tác:
1. Chọn phiếu có trạng thái "Chờ xử lý"
2. Click "Phê duyệt"
3. Nhập ghi chú (không bắt buộc)
4. Xác nhận

---

### 5. **TỪ CHỐI PHIẾU**

#### Quy trình:
```java
// PENDING → REJECTED
public void tuChoiPhieu(int maPhieuDT, int nguoiDuyet, String lyDoTuChoi) {
    phieu.setTrangThai("REJECTED");
    phieu.setNguoiDuyet(nguoiDuyet);
    phieu.setNgayDuyet(LocalDateTime.now());
    phieu.setLyDoTuChoi(lyDoTuChoi);
    
    phieuDoiTraDAO.update(phieu);
}
```

#### UI thao tác:
1. Chọn phiếu "Chờ xử lý"
2. Click "Từ chối"
3. **Bắt buộc** nhập lý do từ chối
4. Xác nhận

---

### 6. **HOÀN THÀNH PHIẾU**

#### Điều kiện:
- Phiếu ở trạng thái `APPROVED`
- Quyền xử lý hoàn tiền: `RoleManager.canProcessRefunds()`

#### Thông tin cần nhập:
```java
// Hình thức hoàn tiền:
- CASH: Tiền mặt
- BANK_TRANSFER: Chuyển khoản
- CARD_REFUND: Hoàn về thẻ
- STORE_CREDIT: Tín dụng cửa hàng

// Thông tin bổ sung:
- Số tài khoản (nếu chuyển khoản)
- Tên chủ tài khoản
```

#### Xử lý tồn kho:
```java
private void capNhatTonKho(int maPhieuDT) {
    for (ChiTietPhieuDoiTra chiTiet : chiTietList) {
        BienTheSanPham bienThe = bienTheDAO.findById(chiTiet.getMaBienThe());
        
        if ("TRA".equals(chiTiet.getLoaiChiTiet())) {
            // Trả hàng: TĂNG tồn kho
            bienThe.setSoLuong(bienThe.getSoLuong() + chiTiet.getSoLuong());
        } else if ("DOI".equals(chiTiet.getLoaiChiTiet())) {
            // Đổi hàng: GIẢM tồn kho
            int soLuongConLai = bienThe.getSoLuong() - chiTiet.getSoLuong();
            if (soLuongConLai < 0) {
                throw new BusinessException("Không đủ tồn kho");
            }
            bienThe.setSoLuong(soLuongConLai);
        }
        
        bienTheDAO.update(bienThe);
    }
}
```

---

## 🔍 TÌM KIẾM VÀ LỌC DỮ LIỆU

### Bộ lọc có sẵn:
1. **Theo trạng thái**: PENDING, APPROVED, REJECTED, COMPLETED
2. **Theo loại phiếu**: DOI, TRA
3. **Theo mã hóa đơn**: Nhập số để tìm

### Code tìm kiếm:
```java
// Service methods:
List<PhieuDoiTra> layPhieuTheoTrangThai(String trangThai);
List<PhieuDoiTra> layPhieuTheoLoai(String loaiPhieu);
List<PhieuDoiTra> layPhieuTheoHoaDon(int maHD);
```

---

## 🛡️ PHÂN QUYỀN HỆ THỐNG

### Quyền truy cập:
```java
// Staff trở lên:
- Xem danh sách phiếu
- Tạo phiếu mới
- Sửa/xóa phiếu PENDING

// Manager trở lên:
- Phê duyệt/từ chối phiếu
- Xử lý hoàn tiền
- Điều chỉnh điểm thủ công
```

### Kiểm tra quyền:
```java
RoleManager.canAccessReturns()     // Staff+
RoleManager.canApproveReturns()    // Manager+
RoleManager.canProcessRefunds()    // Manager+
```

---

## 🚀 CÁCH SỬ DỤNG TRONG THỰC TẾ

### Scenario 1: Khách trả hàng lỗi
1. **Staff**: Tạo phiếu TRA, ghi lý do "Sản phẩm bị lỗi"
2. **Manager**: Phê duyệt ngay
3. **Manager**: Hoàn thành → Hoàn tiền cho khách

### Scenario 2: Khách đổi size
1. **Staff**: Tạo phiếu DOI, trả size M, đổi size L
2. **Hệ thống**: Tính toán tự động (có thể cần bổ sung tiền)
3. **Manager**: Phê duyệt sau khi kiểm tra
4. **Staff**: Hoàn thành → Cập nhật tồn kho

### Scenario 3: Đổi trả ngoài thời hạn
1. **Hệ thống**: Tự động từ chối khi kiểm tra hóa đơn
2. **Staff**: Báo lại khách hàng không thể đổi trả

---

## 📊 BÁO CÁO VÀ THỐNG KÊ

### Thông tin theo dõi:
- Tổng số phiếu đổi trả theo ngày/tháng
- Tỷ lệ phê duyệt/từ chối
- Sản phẩm được đổi trả nhiều nhất
- Lý do đổi trả phổ biến
- Giá trị hoàn tiền theo thời gian

### Integration với báo cáo:
```java
// Có thể mở rộng BaoCaoService để bao gồm:
- Báo cáo đổi trả theo sản phẩm
- Phân tích lý do đổi trả
- Hiệu quả xử lý phiếu
```

---

## ⚠️ LƯU Ý QUAN TRỌNG

### 1. **Bảo mật dữ liệu:**
- Chỉ nhân viên có quyền mới truy cập được
- Log lại tất cả thao tác quan trọng
- Xác thực trước khi thay đổi trạng thái

### 2. **Tính toàn vẹn:**
- Luôn kiểm tra trạng thái trước khi thao tác
- Validate dữ liệu đầu vào kỹ lưỡng
- Transaction để đảm bảo consistency

### 3. **User Experience:**
- Thông báo rõ ràng về kết quả thao tác
- Hiển thị lý do khi không thể thực hiện
- Tự động refresh dữ liệu sau khi cập nhật

### 4. **Performance:**
- Sử dụng lazy loading cho chi tiết
- Cache danh sách tìm kiếm thường dùng
- Phân trang khi dữ liệu lớn

---

## 🎉 KẾT LUẬN

Hệ thống quản lý đổi trả hàng đã được thiết kế hoàn chỉnh với:

✅ **CRUD đầy đủ** cho phiếu đổi trả  
✅ **Quy trình phê duyệt** rõ ràng  
✅ **Phân quyền** chặt chẽ  
✅ **Tính toán tự động** chính xác  
✅ **Cập nhật tồn kho** real-time  
✅ **Validation** nghiệp vụ đầy đủ  

Hệ thống sẵn sàng triển khai và sử dụng trong môi trường production! 🚀
