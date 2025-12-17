package dao;
																	// Thống Kê Database - Hướng
import java.sql.*;
import database.ConnectDB;

public class ThongKeDAO {

    public int tongNhanVien() {
        int soLuong = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM NhanVien");
            if (rs.next()) soLuong = rs.getInt(1);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return soLuong;
    }

    public double tongLuongCoBan() {
        double tong = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT SUM(LuongCoBan) FROM NhanVien");
            if (rs.next()) tong = rs.getDouble(1);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tong;
    }

    public double luongCaoNhat() {
        double max = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(LuongCoBan) FROM NhanVien");
            if (rs.next()) max = rs.getDouble(1);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return max;
    }

    public double luongTrungBinh() {
        double avg = 0;
        try {
            Connection conn = ConnectDB.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT AVG(LuongCoBan) FROM NhanVien");
            if (rs.next()) avg = rs.getDouble(1);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return avg;
    }
}