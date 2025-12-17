# =====================================
# 🎓 PHẦN MỀM QUẢN LÝ LƯƠNG (VKU PROJECT)
# =====================================

> **⚠️ LƯU Ý CHO THÀNH VIÊN NHÓM:**
> Vui lòng tắt kiểm tra chính tả (Spell Check) trong Eclipse để code không bị gạch đỏ.
> (Window -> Preferences -> General -> Editors -> Text Editors -> Spelling -> Bỏ tích Enable).

## 1. 📖 GIỚI THIỆU
Phần mềm hỗ trợ quản lý hồ sơ nhân viên và tính lương tự động.
- **Công nghệ:** Java Swing (Giao diện) + SQL Server (Dữ liệu).
- **Layout:** Sử dụng kết hợp Absolute Layout (Kéo thả) và FlowLayout.

## 2. ✨ TÍNH NĂNG HOT
* ✅ **Đăng nhập Konami:** Gõ `↑ ↑ ↓ ↓ ← → ← → B A` để vào thẳng Admin.
* ✅ **Quản lý nhân sự:** Thêm, Sửa, Xóa, Tìm kiếm nhân viên.
* ✅ **Tính lương tự động:** Lương = (Lương cứng * Hệ số) + Thưởng - Phạt.
* ✅ **Phạt đi trễ:** Tự động trừ 100k/ngày đi trễ.

## 3. 👥 PHÂN CÔNG (CREDITS)

| Tên | Vai Trò | Nhiệm vụ chính |
| :--- | :--- | :--- |
| **Việt** | Trưởng nhóm | Login, CRUD Nhân viên, Database |
| **Quốc** | Thành viên | Chức năng Tăng lương |
| **Đồng** | Thành viên | Tính lương, Giao diện tính lương |
| **Tùng** | Thành viên | Công cụ thuế, Nút tải lại |
| **Hướng** | Thành viên | Thống kê báo cáo |

## 4. 🛠️ CÀI ĐẶT
Chạy script SQL bên dưới để tạo dữ liệu trước khi chạy Java:

```sql
USE master;
GO

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'QuanLyLuongVKU')
    DROP DATABASE QuanLyLuongVKU;
GO

CREATE DATABASE QuanLyLuongVKU;
GO

USE QuanLyLuongVKU;
GO

CREATE TABLE PhongBan (
    MaPB VARCHAR(10) PRIMARY KEY,
    TenPB NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE NhanVien (
    MaNV VARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(50) NOT NULL,
    NgaySinh DATE,
    GioiTinh NVARCHAR(10),
    SDT VARCHAR(15),
    MaPB VARCHAR(10),
    LuongCoBan DECIMAL(18,0) DEFAULT 0,
    HeSoLuong FLOAT DEFAULT 1.0,
    PhuCap DECIMAL(18,0) DEFAULT 0,
    TienThuong DECIMAL(18,0) DEFAULT 0, 
    SoNgayDiTre INT DEFAULT 0,
    FOREIGN KEY (MaPB) REFERENCES PhongBan(MaPB)
);
GO

CREATE TABLE TaiKhoan (
    [Username] VARCHAR(50) NOT NULL PRIMARY KEY,
    [Password] VARCHAR(50) NOT NULL,
    [Role] VARCHAR(20)
);
GO

INSERT INTO TaiKhoan (Username, Password, Role) VALUES ('pyke1001', '31072007viet', 'Admin');
INSERT INTO TaiKhoan (Username, Password, Role) VALUES ('staff', '123', 'NhanVien');
INSERT INTO TaiKhoan (Username, Password, Role) VALUES ('admin', '123456', 'Admin');
INSERT INTO TaiKhoan (Username, Password, Role) VALUES ('user1', '1', 'NhanVien');

INSERT INTO PhongBan VALUES ('PB01', N'Phòng Kỹ Thuật');
INSERT INTO PhongBan VALUES ('PB02', N'Phòng Nhân Sự');
INSERT INTO PhongBan VALUES ('PB03', N'Phòng Kinh Doanh');

INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, SDT, MaPB, LuongCoBan, HeSoLuong, PhuCap, TienThuong, SoNgayDiTre)
VALUES 
    ('NV01', N'Nguyễn Văn An', '1990-01-15', N'Nam', '0905000001', 'PB01', 8000000, 2.5, 1000000, 0, 0),
    ('NV02', N'Trần Thị Bích', '1995-10-20', N'Nữ', '0905000002', 'PB02', 6000000, 1.8, 500000, 0, 0),
    ('NV03', N'Lê Văn Cường', '1998-05-05', N'Nam', '0905000003', 'PB03', 5000000, 1.2, 500000, 0, 0),
    ('NV04', N'Phạm Thị Duyên', '2000-12-12', N'Nữ', '0905000004', 'PB02', 5500000, 1.5, 300000, 0, 0),
    ('NV05', N'Hoàng Văn Em', '1992-03-08', N'Nam', '0905000005', 'PB01', 9000000, 2.8, 1500000, 0, 0),
    ('NV06', N'Đỗ Thị Mai', '1997-07-27', N'Nữ', '0905000006', 'PB03', 4500000, 1.0, 200000, 0, 0),
    ('NV07', N'Vũ Văn Giang', '1985-09-02', N'Nam', '0905000007', 'PB01', 12000000, 3.5, 2000000, 0, 0),
    ('NV08', N'Ngô Thị Hương', '1999-04-30', N'Nữ', '0905000008', 'PB02', 5800000, 1.6, 500000, 0, 0),
    ('NV09', N'Bùi Văn Hùng', '1993-11-20', N'Nam', '0905000009', 'PB01', 7500000, 2.2, 800000, 0, 0),
    ('NV10', N'Đặng Thị Kim', '2001-02-14', N'Nữ', '0905000010', 'PB03', 4800000, 1.1, 200000, 0, 0),
    ('NV11', N'Lý Văn Lâm', '1996-06-01', N'Nam', '0905000011', 'PB01', 7000000, 2.0, 600000, 0, 0),
    ('NV12', N'Trương Thị Mơ', '1994-08-19', N'Nữ', '0905000012', 'PB02', 6200000, 1.9, 500000, 0, 0),
    ('NV13', N'Dương Văn Nam', '1991-12-25', N'Nam', '0905000013', 'PB03', 5200000, 1.3, 300000, 0, 0),
    ('NV14', N'Hà Thị Ngọc', '2002-01-01', N'Nữ', '0905000014', 'PB02', 5000000, 1.0, 200000, 0, 0),
    ('NV15', N'Mai Văn Phúc', '1988-05-19', N'Nam', '0905000015', 'PB01', 11000000, 3.2, 1800000, 0, 0),
    ('NV16', N'Phan Thị Quyên', '1997-09-02', N'Nữ', '0905000016', 'PB03', 4900000, 1.2, 250000, 0, 0),
    ('NV17', N'Tạ Văn Quang', '1995-03-26', N'Nam', '0905000017', 'PB01', 8500000, 2.6, 1200000, 0, 0),
    ('NV18', N'Lương Thị Sương', '2000-11-20', N'Nữ', '0905000018', 'PB02', 5600000, 1.4, 400000, 0, 0),
    ('NV19', N'Cao Văn Tài', '1990-07-15', N'Nam', '0905000019', 'PB03', 6500000, 1.7, 500000, 0, 0),
    ('NV20', N'Đinh Thị Uyên', '1998-02-03', N'Nữ', '0905000020', 'PB01', 7200000, 2.1, 700000, 0, 0),
    ('NV21', N'Phạm Văn Khải', '1993-05-10', N'Nam', '0905000021', 'PB01', 7800000, 2.3, 850000, 0, 0),
    ('NV22', N'Lê Thị Lan', '1996-09-12', N'Nữ', '0905000022', 'PB02', 6100000, 1.7, 450000, 0, 0),
    ('NV23', N'Nguyễn Đức Minh', '1990-11-22', N'Nam', '0905000023', 'PB03', 5300000, 1.4, 350000, 0, 0),
    ('NV24', N'Trần Thị Nga', '1999-03-15', N'Nữ', '0905000024', 'PB01', 7100000, 2.0, 600000, 0, 0),
    ('NV25', N'Võ Văn Oanh', '1987-07-07', N'Nam', '0905000025', 'PB02', 9500000, 3.0, 1500000, 0, 0),
    ('NV26', N'Hoàng Thị Phương', '2001-12-30', N'Nữ', '0905000026', 'PB03', 4600000, 1.0, 200000, 0, 0),
    ('NV27', N'Đỗ Văn Quân', '1994-06-18', N'Nam', '0905000027', 'PB01', 8200000, 2.4, 900000, 0, 0),
    ('NV28', N'Lương Thị Rạng', '1997-02-28', N'Nữ', '0905000028', 'PB02', 5900000, 1.6, 500000, 0, 0),
    ('NV29', N'Ngô Văn Sơn', '1992-08-08', N'Nam', '0905000029', 'PB03', 5100000, 1.3, 300000, 0, 0),
    ('NV30', N'Bùi Thị Trang', '2000-05-05', N'Nữ', '0905000030', 'PB01', 6800000, 1.9, 550000, 0, 0),
    ('NV31', N'Phạm Văn Tú', '1995-10-10', N'Nam', '0905000031', 'PB02', 6300000, 1.8, 500000, 0, 0),
    ('NV32', N'Đặng Thị Vân', '1998-01-20', N'Nữ', '0905000032', 'PB03', 4700000, 1.1, 200000, 0, 0),
    ('NV33', N'Trương Văn Vỹ', '1991-04-14', N'Nam', '0905000033', 'PB01', 9200000, 2.9, 1300000, 0, 0),
    ('NV34', N'Lê Thị Xuyến', '1996-11-11', N'Nữ', '0905000034', 'PB02', 5700000, 1.5, 400000, 0, 0),
    ('NV35', N'Nguyễn Văn Ý', '1989-09-09', N'Nam', '0905000035', 'PB03', 5400000, 1.4, 300000, 0, 0),
    ('NV36', N'Trần Thị Yến', '2002-02-02', N'Nữ', '0905000036', 'PB01', 6500000, 1.8, 500000, 0, 0),
    ('NV37', N'Vũ Văn Zũng', '1993-03-31', N'Nam', '0905000037', 'PB02', 6000000, 1.7, 450000, 0, 0),
    ('NV38', N'Hà Thị Ánh', '1999-07-25', N'Nữ', '0905000038', 'PB03', 4800000, 1.1, 250000, 0, 0),
    ('NV39', N'Phan Văn Bình', '1990-12-15', N'Nam', '0905000039', 'PB01', 8800000, 2.7, 1100000, 0, 0),
    ('NV40', N'Cao Thị Chi', '1995-05-25', N'Nữ', '0905000040', 'PB02', 6400000, 1.9, 600000, 0, 0);
GO
