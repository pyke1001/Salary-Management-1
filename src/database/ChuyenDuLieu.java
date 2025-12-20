package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class ChuyenDuLieu {

    public static void main(String[] args) {
        System.out.println(">>> DANG BAT DAU CHUYEN DU LIEU (BAN FINAL - FIX LOCK)...");
        
        // Khai báo biến connection ở ngoài để dễ quản lý đóng/mở
        Connection connSQL = null;
        Connection connSQLite = null;

        try {
            String sqlUrl = "jdbc:sqlserver://localhost:1433;databaseName=Konami;encrypt=true;trustServerCertificate=true";
            String sqlUser = "sa";
            String sqlPass = "123456"; 

            String sqliteUrl = "jdbc:sqlite:konami_data.db?date_string_format=yyyy-MM-dd";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connSQL = DriverManager.getConnection(sqlUrl, sqlUser, sqlPass);
            
            Class.forName("org.sqlite.JDBC");
            connSQLite = DriverManager.getConnection(sqliteUrl);
            connSQLite.setAutoCommit(false); // Bắt đầu transaction

            // --- 1. TẠO BẢNG & XÓA DỮ LIỆU CŨ ---
            Statement stmt = connSQLite.createStatement();
            stmt.execute("DROP TABLE IF EXISTS TaiKhoan");
            stmt.execute("DROP TABLE IF EXISTS NhanVien");
            stmt.execute("DROP TABLE IF EXISTS PhongBan");

            stmt.execute("CREATE TABLE PhongBan (MaPB TEXT PRIMARY KEY, TenPB TEXT)");
            stmt.execute("CREATE TABLE NhanVien (MaNV TEXT PRIMARY KEY, HoTen TEXT, MaPB TEXT, LuongCoBan INTEGER, HeSoLuong REAL, NgayVaoLam TEXT, SoNgayDiTre INTEGER DEFAULT 0, TienPhat INTEGER DEFAULT 0, TienThuong INTEGER DEFAULT 0, FOREIGN KEY(MaPB) REFERENCES PhongBan(MaPB))");
            stmt.execute("CREATE TABLE TaiKhoan (Username TEXT PRIMARY KEY, Password TEXT, Role TEXT, FOREIGN KEY(Username) REFERENCES NhanVien(MaNV))");
            
            stmt.close(); // Đóng ngay Statement sau khi dùng
            System.out.println(">>> Da xay lai nha moi.");

            // --- 2. CHUYỂN PHÒNG BAN ---
            String queryPB = "SELECT * FROM PhongBan";
            PreparedStatement pstSQL_PB = connSQL.prepareStatement(queryPB);
            ResultSet rsPB = pstSQL_PB.executeQuery();
            
            String insertPB = "INSERT INTO PhongBan (MaPB, TenPB) VALUES (?, ?)";
            PreparedStatement pstLite_PB = connSQLite.prepareStatement(insertPB);

            int countPB = 0;
            while (rsPB.next()) {
                pstLite_PB.setString(1, rsPB.getString("MaPB"));
                pstLite_PB.setString(2, rsPB.getString("TenPB"));
                pstLite_PB.executeUpdate();
                countPB++;
            }
            pstLite_PB.close(); // <--- QUAN TRỌNG: Đóng ngay
            pstSQL_PB.close();
            System.out.println(">>> Da chuyen " + countPB + " Phong ban.");

            // --- 3. CHUYỂN NHÂN VIÊN ---
            String queryNV = "SELECT * FROM NhanVien";
            PreparedStatement pstSQL_NV = connSQL.prepareStatement(queryNV);
            ResultSet rsNV = pstSQL_NV.executeQuery();

            String insertNV = "INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, NgayVaoLam, SoNgayDiTre, TienPhat, TienThuong) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstLite_NV = connSQLite.prepareStatement(insertNV);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int countNV = 0;
            while (rsNV.next()) {
                pstLite_NV.setString(1, rsNV.getString("MaNV"));
                pstLite_NV.setString(2, rsNV.getString("HoTen"));
                pstLite_NV.setString(3, rsNV.getString("MaPB"));
                pstLite_NV.setLong(4, rsNV.getLong("LuongCoBan"));
                pstLite_NV.setFloat(5, rsNV.getFloat("HeSoLuong"));
                try {
                    pstLite_NV.setString(6, sdf.format(rsNV.getDate("NgayVaoLam")));
                } catch (Exception e) {
                    pstLite_NV.setString(6, "2024-01-01"); 
                }
                pstLite_NV.setInt(7, rsNV.getInt("SoNgayDiTre"));
                pstLite_NV.setLong(8, rsNV.getLong("TienPhat"));
                pstLite_NV.setLong(9, rsNV.getLong("TienThuong"));
                pstLite_NV.addBatch();
                countNV++;
            }
            pstLite_NV.executeBatch();
            pstLite_NV.close(); // <--- QUAN TRỌNG: Đóng ngay
            pstSQL_NV.close();
            System.out.println(">>> Da chuyen " + countNV + " Nhan vien.");
            
            // --- 4. CHUYỂN TÀI KHOẢN (KÈM ROLE) ---
            String queryTK = "SELECT * FROM TaiKhoan";
            PreparedStatement pstSQL_TK = connSQL.prepareStatement(queryTK);
            ResultSet rsTK = pstSQL_TK.executeQuery();

            String insertTK = "INSERT INTO TaiKhoan (Username, Password, Role) VALUES (?, ?, ?)";
            PreparedStatement pstLite_TK = connSQLite.prepareStatement(insertTK);
            
            int countTK = 0;
            while (rsTK.next()) {
                pstLite_TK.setString(1, rsTK.getString("Username"));
                pstLite_TK.setString(2, rsTK.getString("Password"));
                try {
                    // Nếu chưa có cột Role ở SQL Server thì tạm set là Admin để test
                     pstLite_TK.setString(3, "Admin"); 
                    // pstLite_TK.setString(3, rsTK.getString("Role")); 
                } catch (Exception e) {
                    pstLite_TK.setString(3, "Admin"); 
                }
                pstLite_TK.addBatch();
                countTK++;
            }
            pstLite_TK.executeBatch();
            pstLite_TK.close(); // <--- QUAN TRỌNG: Đóng ngay
            pstSQL_TK.close();
            System.out.println(">>> Da chuyen " + countTK + " Tai khoan.");

            // CHỐT ĐƠN
            connSQLite.commit();
            System.out.println(">>> THANH CONG! KHONG CON LOI LOCKED.");

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi thì Rollback để không hỏng file
            try { if (connSQLite != null) connSQLite.rollback(); } catch (Exception ex) {}
        } finally {
            // Đảm bảo đóng kết nối cuối cùng
            try { if (connSQL != null) connSQL.close(); } catch (Exception ex) {}
            try { if (connSQLite != null) connSQLite.close(); } catch (Exception ex) {}
        }
    }
}