package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;
import entity.NhanVien;

public class NhanVienDAO {

    private java.util.Date layNgayTuResultSet(ResultSet rs, String tenCot) {
        try {
            // Cách 1: Lấy dạng String trước để né lỗi Parse
            String strDate = rs.getString(tenCot);
            if (strDate == null || strDate.trim().isEmpty()) {
                return new java.util.Date(); // Trả về ngày hiện tại nếu null
            }
            
            // Nếu chuỗi dài (vd: 2025-01-01 12:00:00.0) hoặc ngắn (2025-01-01) -> Cắt lấy 10 ký tự đầu
            if (strDate.length() >= 10) {
                String shortDate = strDate.substring(0, 10);
                return java.sql.Date.valueOf(shortDate); // Chuyển đổi an toàn
            }
            
            return new java.util.Date(); // Fallback
        } catch (Exception e) {
            // Nếu Cách 1 lỗi, thử Cách 2: Lấy theo kiểu truyền thống
            try {
                return rs.getDate(tenCot);
            } catch (Exception ex) {
                return new java.util.Date(); // Chịu thua -> Trả về ngày hiện tại
            }
        }
    }

    private String dateToString(java.util.Date date) {
        if (date == null) return new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public List<NhanVien> layDanhSachNhanVien(String orderBy) {
        List<NhanVien> list = new java.util.ArrayList<>();
        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            
            // Câu lệnh SQL lấy tất cả các cột
            String sql = "SELECT NV.*, PB.TenPB " +
                         "FROM NhanVien NV " +
                         "JOIN PhongBan PB ON NV.MaPB = PB.MaPB " +
                         "WHERE NV.DaXoa = 0 " +
                         "AND NV.MaNV NOT IN ('admin', 'pyke1001') " +
                         "ORDER BY " + orderBy; 
            
            java.sql.Statement stmt = con.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("MaPB"),
                    rs.getLong("LuongCoBan"),
                    rs.getFloat("HeSoLuong")
                );
                
                nv.setTenPB(rs.getString("TenPB")); 
                nv.setPhuCap(rs.getLong("PhuCap"));
                nv.setTienThuong(rs.getLong("TienThuong"));
                nv.setTienPhat(rs.getLong("TienPhat"));
                nv.setSoNgayDiTre(rs.getInt("SoNgayDiTre"));
                
                // --- [QUAN TRỌNG] THÊM ĐOẠN NÀY ĐỂ LẤY DỮ LIỆU MỚI ---
                try {
                    nv.setGioTangCa(rs.getDouble("GioTangCa"));
                    nv.setHeSoTangCa(rs.getDouble("HeSoTangCa"));
                    nv.setThucLinh(rs.getLong("ThucLinh"));
                    nv.setLyDoThuongPhat(rs.getString("LyDoThuongPhat"));
                } catch (Exception e) {
                    // Nếu cột chưa có trong DB thì bỏ qua, không crash
                }
                // -----------------------------------------------------
                
                nv.setNgayVaoLam(layNgayTuResultSet(rs, "NgayVaoLam")); 
                
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean themNhanVien(NhanVien nv) {
    // [FIX] Gán cứng lương cơ bản trong code, bất chấp UI gửi xuống cái gì
    String sql = "INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, NgayVaoLam) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement pstm = conn.prepareStatement(sql)) {
        
        pstm.setString(1, nv.getMaNV());
        pstm.setString(2, nv.getHoTen());
        pstm.setString(3, nv.getMaPB());
        
        // [QUAN TRỌNG] Gán cứng 2tr340k ở đây
        pstm.setLong(4, 2340000); 
        
        pstm.setFloat(5, nv.getHeSoLuong());
        pstm.setString(6, dateToString(nv.getNgayVaoLam())); 
        
        return pstm.executeUpdate() > 0;
    } catch (Exception ex) {
        ex.printStackTrace();
        return false;
    }
    }
    
    public boolean suaNhanVien(NhanVien nv) {
    String sql = "UPDATE NhanVien SET HoTen=?, MaPB=?, HeSoLuong=? WHERE MaNV=?";
    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement pstm = conn.prepareStatement(sql)) {
        
        pstm.setString(1, nv.getHoTen());
        pstm.setString(2, nv.getMaPB());
        // pstm.setLong(3, nv.getLuongCoBan()); <-- XÓA DÒNG NÀY (Dòng gây lỗi về 0)
        pstm.setFloat(3, nv.getHeSoLuong()); // Đẩy index lên (4 -> 3)
        pstm.setString(4, nv.getMaNV());     // Đẩy index lên (5 -> 4)
        
        return pstm.executeUpdate() > 0;
    } catch (Exception ex) {
        return false;
    }
    }
    
    public boolean xoaNhanVien(String maNV) {
        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            String sql = "UPDATE NhanVien SET DaXoa = 1 WHERE MaNV = ?";
            java.sql.PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);
            int n = stmt.executeUpdate();
            return n > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean capNhatPhat(String maNV, int soNgay) {
        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            long tienPhat = soNgay * 100000L; 
            String sql = "UPDATE NhanVien SET SoNgayDiTre = ?, TienPhat = ? WHERE MaNV = ?";
            java.sql.PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, soNgay);
            stmt.setLong(2, tienPhat);
            stmt.setString(3, maNV);
            int n = stmt.executeUpdate();
            return n > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int tangLuong(String maNV, double phanTram) throws Exception {
        String sql = "UPDATE NhanVien SET LuongCoBan = LuongCoBan * (1 + ? / 100) WHERE MaNV = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, phanTram);
            ps.setString(2, maNV);
            return ps.executeUpdate();
        }
    }

    public List<NhanVien> timKiemDaNang(String ma, String ten, String phong, String luong, String heSo, String orderBy) {
        List<NhanVien> list = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT NV.*, PB.TenPB " +
            "FROM NhanVien NV " +
            "JOIN PhongBan PB ON NV.MaPB = PB.MaPB " +
            "WHERE NV.DaXoa = 0 " +
            "AND NV.MaNV NOT IN ('admin', 'pyke1001') "
        );
        
        if (!ma.isEmpty()) sql.append(" AND NV.MaNV LIKE ?");
        if (!ten.isEmpty()) sql.append(" AND NV.HoTen LIKE ?");
        if (!phong.isEmpty() && !phong.equals("Tất cả")) { 
             sql.append(" AND (NV.MaPB LIKE ? OR PB.TenPB LIKE ?)");
        }

        if (luong.equals("Dưới 5 triệu")) sql.append(" AND NV.LuongCoBan < 5000000");
        else if (luong.equals("5 triệu - 10 triệu")) sql.append(" AND NV.LuongCoBan BETWEEN 5000000 AND 10000000");
        else if (luong.equals("10 triệu - 20 triệu")) sql.append(" AND NV.LuongCoBan BETWEEN 10000000 AND 20000000");
        else if (luong.equals("Trên 20 triệu")) sql.append(" AND NV.LuongCoBan > 20000000");

        if (heSo.contains("Dưới 2.0")) sql.append(" AND NV.HeSoLuong < 2.0");
        else if (heSo.contains("2.0 - 3.0")) sql.append(" AND NV.HeSoLuong BETWEEN 2.0 AND 3.0");
        else if (heSo.contains("Trên 3.0")) sql.append(" AND NV.HeSoLuong > 3.0");

        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append(" ORDER BY ").append(orderBy);
        }

        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            java.sql.PreparedStatement stmt = con.prepareStatement(sql.toString());
            
            int index = 1;
            if (!ma.isEmpty()) stmt.setString(index++, "%" + ma + "%");
            if (!ten.isEmpty()) stmt.setString(index++, "%" + ten + "%");
            if (!phong.isEmpty() && !phong.equals("Tất cả")) {
                stmt.setString(index++, "%" + phong + "%");
                stmt.setString(index++, "%" + phong + "%");
            }

            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("MaPB"),
                    rs.getLong("LuongCoBan"),
                    rs.getFloat("HeSoLuong")
                );
                
                // [FIX] BỔ SUNG CÁC TRƯỜNG MỚI ĐỂ KHÔNG BỊ MẤT DỮ LIỆU KHI TÌM KIẾM
                nv.setGioTangCa(rs.getDouble("GioTangCa"));
                nv.setHeSoTangCa(rs.getDouble("HeSoTangCa"));
                nv.setTienThuong(rs.getLong("TienThuong"));
                nv.setTienPhat(rs.getLong("TienPhat"));
                nv.setThucLinh(rs.getLong("ThucLinh")); // Quan trọng
                nv.setLyDoThuongPhat(rs.getString("LyDoThuongPhat"));
                
                nv.setTenPB(rs.getString("TenPB")); 
                nv.setPhuCap(rs.getLong("PhuCap")); 
                nv.setSoNgayDiTre(rs.getInt("SoNgayDiTre"));
                
                nv.setNgayVaoLam(layNgayTuResultSet(rs, "NgayVaoLam"));
                
                list.add(nv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public NhanVien getNhanVienTheoMa(String maNV) {
        NhanVien nv = null;
        String sql = "SELECT NV.*, PB.TenPB FROM NhanVien NV LEFT JOIN PhongBan PB ON NV.MaPB = PB.MaPB WHERE NV.MaNV = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            
            pstm.setString(1, maNV);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    nv = new NhanVien();
                    nv.setMaNV(rs.getString("MaNV"));
                    nv.setHoTen(rs.getString("HoTen"));
                    nv.setMaPB(rs.getString("MaPB"));
                    nv.setGioiTinh(rs.getString("GioiTinh"));
                    nv.setLuongCoBan(rs.getLong("LuongCoBan"));
                    nv.setHeSoLuong(rs.getFloat("HeSoLuong"));
                    
                    // [FIX] Bổ sung map dữ liệu mới
                    nv.setGioTangCa(rs.getDouble("GioTangCa"));
                    nv.setHeSoTangCa(rs.getDouble("HeSoTangCa"));
                    nv.setThucLinh(rs.getLong("ThucLinh"));
                    nv.setLyDoThuongPhat(rs.getString("LyDoThuongPhat"));
                    
                    nv.setTienThuong(rs.getLong("TienThuong"));
                    nv.setSoNgayDiTre(rs.getInt("SoNgayDiTre"));
                    nv.setTienPhat(rs.getLong("TienPhat"));
                    nv.setPhuCap(rs.getLong("PhuCap"));     
                    
                    nv.setNgayVaoLam(layNgayTuResultSet(rs, "NgayVaoLam"));
                    nv.setTenPB(rs.getString("TenPB"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nv;
    }
    
    public List<String> layDanhSachPhongBan() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT TenPB FROM PhongBan";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) list.add(rs.getString("TenPB"));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<NhanVien> layNhanVienTheoPhong(String tenPhong) { 
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT NV.MaNV, NV.HoTen, PB.TenPB FROM NhanVien NV JOIN PhongBan PB ON NV.MaPB = PB.MaPB WHERE PB.TenPB LIKE ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, tenPhong.equals("Tất cả") ? "%" : "%" + tenPhong + "%");
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("MaNV"));
                    nv.setHoTen(rs.getString("HoTen"));
                    nv.setTenPB(rs.getString("TenPB"));
                    list.add(nv);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<NhanVien> timKiemDanhBa(String tenPhong, String tuKhoa) {
        List<NhanVien> list = new ArrayList<>();
        
        // [FIX] Thêm điều kiện: AND NV.MaNV NOT IN ('admin', 'pyke1001')
        String sql = "SELECT NV.MaNV, NV.HoTen, PB.TenPB " +
                     "FROM NhanVien NV " +
                     "JOIN PhongBan PB ON NV.MaPB = PB.MaPB " +
                     "WHERE PB.TenPB LIKE ? " +
                     "AND (NV.HoTen LIKE ? OR NV.MaNV LIKE ?) " +
                     "AND NV.MaNV NOT IN ('admin', 'pyke1001')"; // <--- Dòng quan trọng đây
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            
            pstm.setString(1, tenPhong.equals("Tất cả") ? "%" : "%" + tenPhong + "%");
            
            String searchPattern = "%" + tuKhoa + "%";
            pstm.setString(2, searchPattern);
            pstm.setString(3, searchPattern);
            
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("MaNV"));
                    nv.setHoTen(rs.getString("HoTen"));
                    nv.setTenPB(rs.getString("TenPB"));
                    list.add(nv);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean doiMatKhau(String username, String matKhauCu, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET Password = ? WHERE Username = ? AND Password = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, matKhauMoi);
            pstm.setString(2, username);
            pstm.setString(3, matKhauCu);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<String[]> layDanhSachTaiKhoan() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT NV.MaNV, NV.HoTen, PB.TenPB, TK.Username, TK.Password FROM TaiKhoan TK JOIN NhanVien NV ON TK.Username = NV.MaNV LEFT JOIN PhongBan PB ON NV.MaPB = PB.MaPB";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = rs.getString("MaNV");
                row[1] = rs.getString("HoTen");
                row[2] = rs.getString("TenPB");
                row[3] = rs.getString("Username");
                row[4] = rs.getString("Password");
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public boolean capNhatMatKhau(String username, String passMoi) {
        String sql = "UPDATE TaiKhoan SET Password = ? WHERE Username = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passMoi);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public boolean guiBaoLoi(String tieuDe, String noiDung) {
        String sql = "INSERT INTO BaoLoi (TieuDe, NoiDung) VALUES (?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tieuDe);
            ps.setString(2, noiDung);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public boolean congTienThuong(long soTien) {
        String sql = "UPDATE NhanVien SET TienThuong = TienThuong + ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, soTien);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean resetThangMoi() {
        String sql = "UPDATE NhanVien SET SoNgayDiTre = 0, TienPhat = 0, TienThuong = 0";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public boolean congTienThuongTheoPhong(String tenPhong, long soTien) {
        String sql = "UPDATE NhanVien SET TienThuong = TienThuong + ? WHERE MaPB IN (SELECT MaPB FROM PhongBan WHERE TenPB = ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, soTien);
            stmt.setString(2, tenPhong);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void congTienThuongCaNhan(String maNV, long soTien) {
        String sql = "UPDATE NhanVien SET TienThuong = TienThuong + ? WHERE MaNV = ?";
        try (java.sql.Connection conn = database.ConnectDB.getConnection();
             java.sql.PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setLong(1, soTien);
            pstm.setString(2, maNV);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String chuyenTenPhongThanhMa(String tenPhong) {
        String maPB = tenPhong; 
        String sql = "SELECT MaPB FROM PhongBan WHERE TenPB = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenPhong);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) maPB = rs.getString("MaPB");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return maPB;
    }
    
    public String kiemTraDangNhap(String username, String password) {
        String role = null;
        String sql = "SELECT Role FROM TaiKhoan WHERE LOWER(Username) = LOWER(?) AND Password = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, username);
            pstm.setString(2, password); 
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    role = rs.getString("Role");
                    if (role == null || role.trim().isEmpty()) {
                        if (username.equalsIgnoreCase("NV01")) return "Admin";
                        return "NhanVien";
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return role;
    }

    public void ghiLichSu(String maNV, String hanhDong, String chiTiet, String nguoiThucHien) {
        // [QUAN TRỌNG] Đổi GETDATE() thành CURRENT_TIMESTAMP để chạy được cả trên SQLite và SQL Server
        String sql = "INSERT INTO LichSuHoatDong (MaNV, HanhDong, ChiTiet, NguoiThucHien, NgayThucHien) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, maNV);
            pstm.setString(2, hanhDong);
            pstm.setString(3, chiTiet);
            pstm.setString(4, nguoiThucHien);
            pstm.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<String[]> layDanhSachLichSu() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT L.*, NV.HoTen FROM LichSuHoatDong L LEFT JOIN NhanVien NV ON L.MaNV = NV.MaNV ORDER BY L.NgayThucHien DESC";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] row = new String[6];
                row[0] = String.valueOf(rs.getInt("MaLog"));
                row[1] = rs.getString("MaNV") + " - " + (rs.getString("HoTen") != null ? rs.getString("HoTen") : "N/A");
                row[2] = rs.getString("HanhDong");
                row[3] = rs.getString("ChiTiet");
                row[4] = rs.getString("NguoiThucHien");
                row[5] = rs.getString("NgayThucHien"); // Lấy dạng String luôn cho an toàn
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public boolean xoaTaiKhoan(String maNV) {
        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            String sql = "DELETE FROM TaiKhoan WHERE Username = ?"; 
            java.sql.PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV); 
            int n = stmt.executeUpdate();
            return n > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public List<NhanVien> layDanhSachNhanVienDaXoa() {
        List<NhanVien> list = new java.util.ArrayList<>();
        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            String sql = "SELECT NV.*, PB.TenPB FROM NhanVien NV JOIN PhongBan PB ON NV.MaPB = PB.MaPB WHERE NV.DaXoa = 1";
            java.sql.Statement stmt = con.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("MaNV"), rs.getString("HoTen"),
                    rs.getString("MaPB"), rs.getLong("LuongCoBan"),
                    rs.getFloat("HeSoLuong")
                );
                nv.setTenPB(rs.getString("TenPB"));
                list.add(nv);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean khoiPhucNhanVien(String maNV) {
        try {
            java.sql.Connection con = database.ConnectDB.getConnection();
            String sql = "UPDATE NhanVien SET DaXoa = 0 WHERE MaNV = ?";
            java.sql.PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maNV);
            int n = stmt.executeUpdate();
            return n > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
    public List<String[]> layLichSuCuaNhanVien(String maNV) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT HanhDong, ChiTiet, NguoiThucHien, NgayThucHien FROM LichSuHoatDong WHERE MaNV = ? ORDER BY NgayThucHien DESC";
        try (Connection conn = database.ConnectDB.getConnection();
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, maNV);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[4];
                    row[0] = rs.getString("NgayThucHien"); // String an toàn
                    row[1] = rs.getString("HanhDong");
                    row[2] = rs.getString("ChiTiet");
                    row[3] = rs.getString("NguoiThucHien");
                    list.add(row);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public void taoLaiTaiKhoan(String maNV, String matKhauMacDinh) {
        String sql = "INSERT INTO TaiKhoan (Username, Password, Role) VALUES (?, ?, ?)";
        try (java.sql.Connection con = database.ConnectDB.getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ps.setString(2, matKhauMacDinh);
            ps.setString(3, "NhanVien");
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public boolean congTienThuongChoNhanVien(String maNV, long soTien) {
        String sql = "UPDATE NhanVien SET TienThuong = TienThuong + ? WHERE MaNV = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, soTien);
            stmt.setString(2, maNV);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
    
    public boolean capNhatHeSo(String maNV, float heSoMoi) {
        String sql = "UPDATE NhanVien SET HeSoLuong = ? WHERE MaNV = ?";
        try (java.sql.Connection conn = database.ConnectDB.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, heSoMoi);
            ps.setString(2, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean guiThuMoi(String maNV, String tieuDe, String noiDung) {
        // Lưu ý: Dùng CURRENT_TIMESTAMP để lấy giờ hiện tại
        String sql = "INSERT INTO HopThu (MaNV, TieuDe, NoiDung, NgayGui, DaXem) VALUES (?, ?, ?, CURRENT_TIMESTAMP, 0)";
        try (java.sql.Connection conn = database.ConnectDB.getConnection();
             java.sql.PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, maNV);
            pstm.setString(2, tieuDe);
            pstm.setString(3, noiDung);
            return pstm.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public java.util.List<String[]> layDanhSachThu(String maNV) {
        java.util.List<String[]> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM HopThu WHERE MaNV = ? ORDER BY NgayGui DESC";
        try (java.sql.Connection conn = database.ConnectDB.getConnection();
             java.sql.PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, maNV);
            try (java.sql.ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[5];
                    row[0] = rs.getString("MaThu");
                    row[1] = rs.getString("TieuDe");
                    row[2] = rs.getString("NoiDung");
                    row[3] = rs.getString("NgayGui");
                    row[4] = rs.getInt("DaXem") == 0 ? "Chưa xem" : "Đã xem";
                    list.add(row);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public void danhDauDaXem(String maThu) {
        String sql = "UPDATE HopThu SET DaXem = 1 WHERE MaThu = ?";
        try (java.sql.Connection conn = database.ConnectDB.getConnection();
             java.sql.PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, maThu);
            pstm.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    
}