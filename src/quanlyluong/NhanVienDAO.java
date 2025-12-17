package quanlyluong;
																		// Model (Data Access) - Việt, Quốc
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.ConnectDB;

public class NhanVienDAO {

	public List<NhanVien> layDanhSachNhanVien(String orderBy) {					// Hàm 'Lấy Danh Sách' - Việt
        List<NhanVien> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            
            String sql = "SELECT NV.MaNV, NV.HoTen, NV.MaPB, PB.TenPB, NV.LuongCoBan, NV.HeSoLuong, NV.TienThuong, NV.SoNgayDiTre, " +
                         "(NV.SoNgayDiTre * 100000) AS TienPhat, " +
                         "((NV.LuongCoBan * NV.HeSoLuong) + NV.TienThuong - (NV.SoNgayDiTre * 100000)) AS ThucLinh " +
                         "FROM NhanVien NV " +
                         "LEFT JOIN PhongBan PB ON NV.MaPB = PB.MaPB " + 
                         "ORDER BY " + orderBy;
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("MaPB"),
                    rs.getLong("LuongCoBan"),
                    rs.getFloat("HeSoLuong"),
                    rs.getLong("TienThuong"),
                    rs.getInt("SoNgayDiTre"),
                    rs.getLong("TienPhat"),
                    rs.getLong("ThucLinh")
                );
                
                nv.setTenPB(rs.getString("TenPB")); 
                
                list.add(nv);
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
	
    public boolean themNhanVien(NhanVien nv) {									// Hàm 'Thêm Nhân Viên' - Việt
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, nv.getMaNV());
            pstm.setString(2, nv.getHoTen());
            pstm.setString(3, nv.getMaPB());
            pstm.setLong(4, nv.getLuongCoBan());
            pstm.setFloat(5, nv.getHeSoLuong());
            pstm.executeUpdate();
            conn.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean suaNhanVien(NhanVien nv) {									// Hàm 'Sửa Nhân Viên' - Việt
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "UPDATE NhanVien SET HoTen=?, MaPB=?, LuongCoBan=?, HeSoLuong=? WHERE MaNV=?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, nv.getHoTen());
            pstm.setString(2, nv.getMaPB());
            pstm.setLong(3, nv.getLuongCoBan());
            pstm.setFloat(4, nv.getHeSoLuong());
            pstm.setString(5, nv.getMaNV());
            pstm.executeUpdate();
            conn.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean xoaNhanVien(String maNV) {									// Hàm 'Xóa Nhân Viên' - Việt
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "DELETE FROM NhanVien WHERE MaNV=?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maNV);
            pstm.executeUpdate();
            conn.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void capNhatPhat(String maNV, int soNgay) throws Exception {			// Hàm 'Cập Nhật Phạt' - Việt
        Connection conn = ConnectDB.getConnection();
        String sql = "UPDATE NhanVien SET SoNgayDiTre = ? WHERE MaNV = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, soNgay);
        pst.setString(2, maNV);
        pst.executeUpdate();
        conn.close();
    }

    public int tangLuong(String maNV, double phanTram) throws Exception {		// Hàm 'Tăng Lương' - Quốc
        Connection conn = ConnectDB.getConnection();
        String sql = "UPDATE NhanVien SET LuongCoBan = LuongCoBan * (1 + ? / 100) WHERE MaNV = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDouble(1, phanTram);
        ps.setString(2, maNV);
        int kq = ps.executeUpdate();
        conn.close();
        return kq;
    }
 
    public List<NhanVien> timKiemDaNang(String ma, String ten, String phong, String luong,String orderBy) {		// Hàm 'Tìm kiếm' - Việt
        List<NhanVien> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            
            String sql = "SELECT NV.*, PB.TenPB, " +
                         "(NV.SoNgayDiTre * 100000) AS TienPhat, " +
                         "((NV.LuongCoBan * NV.HeSoLuong) + NV.PhuCap + NV.TienThuong - (NV.SoNgayDiTre * 100000)) AS ThucLinh " +
                         "FROM NhanVien NV " +
                         "LEFT JOIN PhongBan PB ON NV.MaPB = PB.MaPB " +
                         "WHERE 1=1"; 
            // --------------------
            
            if (!ma.isEmpty()) {
                sql += " AND NV.MaNV LIKE ?";
            }
            if (!ten.isEmpty()) {
                sql += " AND NV.HoTen LIKE ?";
            }
            if (!phong.isEmpty()) {
                sql += " AND (PB.TenPB LIKE ? OR NV.MaPB LIKE ?)";
            }
            if (!luong.isEmpty()) {
                sql += " AND CAST(NV.LuongCoBan AS VARCHAR) LIKE ?";
            }
            
            sql += " ORDER BY " + orderBy;
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            int index = 1;
            
            if (!ma.isEmpty()) pstm.setString(index++, "%" + ma + "%");
            if (!ten.isEmpty()) pstm.setString(index++, "%" + ten + "%");
            if (!phong.isEmpty()) {
                pstm.setString(index++, "%" + phong + "%");
                pstm.setString(index++, "%" + phong + "%");
            }
            if (!luong.isEmpty()) pstm.setString(index++, "%" + luong + "%");

            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("MaPB"),
                    rs.getLong("LuongCoBan"),
                    rs.getFloat("HeSoLuong"),
                    rs.getLong("TienThuong"),
                    rs.getInt("SoNgayDiTre"),
                    rs.getLong("TienPhat"), 
                    rs.getLong("ThucLinh") 
                );
                nv.setTenPB(rs.getString("TenPB"));
                list.add(nv);
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public NhanVien getNhanVienTheoMa(String maNV) {							// Hàm 'Hồ sơ cá nhân' - Việt
        NhanVien nv = null;
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT * FROM NhanVien WHERE MaNV = ?";
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, maNV);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                nv = new NhanVien(
                    rs.getString("MaNV"),
                    rs.getString("HoTen"),
                    rs.getString("MaPB"),
                    rs.getLong("LuongCoBan"),
                    rs.getFloat("HeSoLuong"),
                    rs.getLong("TienThuong"),
                    rs.getInt("SoNgayDiTre"),
                    0, 0
                );
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nv;
    }
 
    public List<String> layDanhSachPhongBan() {									// Hàm 'Danh sách Phòng Ban' - Việt
        List<String> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            
            String sql = "SELECT DISTINCT TenPB FROM PhongBan";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("TenPB"));
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NhanVien> layNhanVienTheoPhong(String tenPhong) { 				// Hàm 'Lọc Nhân Viên theo Phòng Ban' - Việt
        List<NhanVien> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT NV.MaNV, NV.HoTen, PB.TenPB " +
                         "FROM NhanVien NV " +
                         "JOIN PhongBan PB ON NV.MaPB = PB.MaPB " +
                         "WHERE PB.TenPB LIKE ?";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            
            if (tenPhong.equals("Tất cả")) {
                pstm.setString(1, "%");
            } else {
                pstm.setString(1, "%" + tenPhong + "%");
            }
            
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setTenPB(rs.getString("TenPB"));
                list.add(nv);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<NhanVien> timKiemDanhBa(String tenPhong, String tuKhoa) {		// Hàm 'Tìm kiếm - Hồ sơ cá nhân' - Việt
        List<NhanVien> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT NV.MaNV, NV.HoTen, PB.TenPB " +
                         "FROM NhanVien NV " +
                         "JOIN PhongBan PB ON NV.MaPB = PB.MaPB " +
                         "WHERE PB.TenPB LIKE ? " +
                         "AND (NV.HoTen LIKE ? OR NV.MaNV LIKE ?)";

            PreparedStatement pstm = conn.prepareStatement(sql);

            if (tenPhong.equals("Tất cả")) {
                pstm.setString(1, "%");
            } else {
                pstm.setString(1, "%" + tenPhong + "%");
            }

            String searchPattern = "%" + tuKhoa + "%";
            pstm.setString(2, searchPattern);
            pstm.setString(3, searchPattern);

            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setHoTen(rs.getString("HoTen"));
                nv.setTenPB(rs.getString("TenPB"));
                list.add(nv);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean doiMatKhau(String username, String matKhauCu, String matKhauMoi) {  // Hàm 'Đổi mật khẩu' - Việt
        int result = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "UPDATE TaiKhoan SET Password = ? WHERE Username = ? AND Password = ?";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            pstm.setString(1, matKhauMoi);
            pstm.setString(2, username);
            pstm.setString(3, matKhauCu);
            
            result = pstm.executeUpdate();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result > 0;
    }

    public List<String[]> layDanhSachTaiKhoan() {								// Hàm 'Danh sách tài khoản - Admin' - Việt
        List<String[]> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT NV.MaNV, NV.HoTen, PB.TenPB, TK.Username, TK.Password " +
                         "FROM TaiKhoan TK " +
                         "JOIN NhanVien NV ON TK.Username = NV.MaNV " +
                         "LEFT JOIN PhongBan PB ON NV.MaPB = PB.MaPB";
            
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                String[] row = new String[5];
                row[0] = rs.getString("MaNV");
                row[1] = rs.getString("HoTen");
                row[2] = rs.getString("TenPB");
                row[3] = rs.getString("Username");
                row[4] = rs.getString("Password");
                list.add(row);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}