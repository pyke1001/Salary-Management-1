/*
File này dùng để thống kê dữ liệu!
ThongKeDAO nghĩa là Thống Kê Data Access Object (Đối tượng truy cập dữ liệu)
Nếu Database lỗi thì mở file này ra sửa. 
*/

package quanlyluong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.ConnectDB; 

public class ThongKeDAO {

    // 1. Đếm tổng số nhân viên
    public int tongNhanVien() {
        int soLuong = 0;
        try {
            Connection conn = ConnectDB.getConnection(); // Gọi hàm kết nối của nhóm
            String sql = "SELECT COUNT(*) FROM NhanVien";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                soLuong = rs.getInt(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soLuong;
    }

    // 2. Tính tổng tiền lương cứng (Cả công ty)
    public double tongLuongCoBan() {
        double tong = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            // Lưu ý: Sửa 'LuongCung' thành tên cột chính xác trong SQL của bạn
            String sql = "SELECT SUM(LuongCoBan) FROM NhanVien"; 
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                tong = rs.getDouble(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tong;
    }

    // 3. Tìm mức lương cơ bản cao nhất
    public double luongCaoNhat() {
        double maxLuong = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT MAX(LuongCoBan) FROM NhanVien";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                maxLuong = rs.getDouble(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxLuong;
    }

    // 4. Tính lương trung bình
    public double luongTrungBinh() {
        double avgLuong = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT AVG(LuongCoBan) FROM NhanVien";
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                avgLuong = rs.getDouble(1);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avgLuong;
    }
}