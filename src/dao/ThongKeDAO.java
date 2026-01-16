package dao;
														
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.ConnectDB;

public class ThongKeDAO {

    public int tongNhanVien() {
        String sql = "SELECT COUNT(*) FROM NhanVien";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long tongQuyLuong() {
        String sql = "SELECT SUM(LuongCoBan * HeSoLuong + TienThuong - TienPhat) FROM NhanVien";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ResultSet layThongKePhongBan() {
        String sql = "SELECT pb.TenPB, COUNT(nv.MaNV) as SoNV, SUM(nv.LuongCoBan * nv.HeSoLuong + nv.TienThuong - nv.TienPhat) as TongLuong " +
                     "FROM PhongBan pb LEFT JOIN NhanVien nv ON pb.MaPB = nv.MaPB " +
                     "GROUP BY pb.TenPB";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            return stmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}