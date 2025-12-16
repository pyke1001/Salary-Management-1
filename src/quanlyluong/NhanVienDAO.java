package quanlyluong;
																	// Controller - Việt, Quốc
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import database.ConnectDB;

public class NhanVienDAO {

    public List<NhanVien> layDanhSachNhanVien(String orderBy) {					// Hàm 'Lấy Danh Sách - Tính Toán' - Việt
        List<NhanVien> list = new ArrayList<>();
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, TienThuong, SoNgayDiTre, " +
                         "(SoNgayDiTre * 100000) AS TienPhat, " +
                         "((LuongCoBan * HeSoLuong) + PhuCap + TienThuong - (SoNgayDiTre * 100000)) AS ThucLinh " +
                         "FROM NhanVien ORDER BY " + orderBy;
            
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
}