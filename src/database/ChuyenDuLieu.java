package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class ChuyenDuLieu {

    public static void main(String[] args) {
        System.out.println(">>> DANG BAT DAU CHUYEN DU LIEU (FULL COLUMNS)...");
        
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
            connSQLite.setAutoCommit(false); 

            Statement stmt = connSQLite.createStatement();
            stmt.execute("DROP TABLE IF EXISTS TaiKhoan");
            stmt.execute("DROP TABLE IF EXISTS NhanVien");
            stmt.execute("DROP TABLE IF EXISTS PhongBan");
            stmt.execute("DROP TABLE IF EXISTS BaoLoi");
            stmt.execute("DROP TABLE IF EXISTS LichSuHoatDong");

            stmt.execute("CREATE TABLE PhongBan (MaPB TEXT PRIMARY KEY, TenPB TEXT)");
            
            stmt.execute("CREATE TABLE NhanVien (" +
                         "MaNV TEXT PRIMARY KEY, HoTen TEXT, MaPB TEXT, " +
                         "LuongCoBan INTEGER, HeSoLuong REAL, " +
                         "NgaySinh TEXT, GioiTinh TEXT, SDT TEXT, PhuCap INTEGER, " + 
                         "NgayVaoLam TEXT, SoNgayDiTre INTEGER DEFAULT 0, " +
                         "TienPhat INTEGER DEFAULT 0, TienThuong INTEGER DEFAULT 0, " +
                         "DaXoa INTEGER DEFAULT 0, " + 
                         "FOREIGN KEY(MaPB) REFERENCES PhongBan(MaPB))");
                          
            stmt.execute("CREATE TABLE TaiKhoan (Username TEXT PRIMARY KEY, Password TEXT, Role TEXT, FOREIGN KEY(Username) REFERENCES NhanVien(MaNV))");
            
            stmt.execute("CREATE TABLE BaoLoi (MaLoi INTEGER PRIMARY KEY AUTOINCREMENT, TieuDe TEXT, NoiDung TEXT, NgayBao TEXT, TrangThai TEXT)");
            stmt.execute("CREATE TABLE LichSuHoatDong (MaLog INTEGER PRIMARY KEY AUTOINCREMENT, MaNV TEXT, HanhDong TEXT, ChiTiet TEXT, NgayThucHien TEXT, NguoiThucHien TEXT)");
            
            stmt.close();
            System.out.println(">>> Da tao bang day du.");

            String queryPB = "SELECT * FROM PhongBan";
            PreparedStatement pstSQL_PB = connSQL.prepareStatement(queryPB);
            ResultSet rsPB = pstSQL_PB.executeQuery();
            
            String insertPB = "INSERT INTO PhongBan (MaPB, TenPB) VALUES (?, ?)";
            PreparedStatement pstLite_PB = connSQLite.prepareStatement(insertPB);

            while (rsPB.next()) {
                pstLite_PB.setString(1, rsPB.getString("MaPB"));
                pstLite_PB.setString(2, rsPB.getString("TenPB"));
                pstLite_PB.executeUpdate();
            }
            pstLite_PB.close();
            pstSQL_PB.close();

            String queryNV = "SELECT * FROM NhanVien";
            PreparedStatement pstSQL_NV = connSQL.prepareStatement(queryNV);
            ResultSet rsNV = pstSQL_NV.executeQuery();

            String insertNV = "INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, NgaySinh, GioiTinh, SDT, PhuCap, NgayVaoLam, SoNgayDiTre, TienPhat, TienThuong, DaXoa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstLite_NV = connSQLite.prepareStatement(insertNV);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int countNV = 0;
            while (rsNV.next()) {
                pstLite_NV.setString(1, rsNV.getString("MaNV"));
                pstLite_NV.setString(2, rsNV.getString("HoTen"));
                pstLite_NV.setString(3, rsNV.getString("MaPB"));
                pstLite_NV.setLong(4, rsNV.getLong("LuongCoBan"));
                pstLite_NV.setFloat(5, rsNV.getFloat("HeSoLuong"));
                
                try { pstLite_NV.setString(6, rsNV.getDate("NgaySinh") != null ? sdf.format(rsNV.getDate("NgaySinh")) : null); } catch (Exception e) { pstLite_NV.setString(6, null); }
                pstLite_NV.setString(7, rsNV.getString("GioiTinh"));
                pstLite_NV.setString(8, rsNV.getString("SDT"));
                pstLite_NV.setLong(9, rsNV.getLong("PhuCap"));

                try { pstLite_NV.setString(10, sdf.format(rsNV.getDate("NgayVaoLam"))); } catch (Exception e) { pstLite_NV.setString(10, "2024-01-01"); }
                
                pstLite_NV.setInt(11, rsNV.getInt("SoNgayDiTre"));
                pstLite_NV.setLong(12, rsNV.getLong("TienPhat"));
                pstLite_NV.setLong(13, rsNV.getLong("TienThuong"));
                pstLite_NV.setInt(14, rsNV.getBoolean("DaXoa") ? 1 : 0); 

                pstLite_NV.addBatch();
                countNV++;
            }
            pstLite_NV.executeBatch();
            pstLite_NV.close();
            pstSQL_NV.close();
            System.out.println(">>> Da chuyen " + countNV + " Nhan vien.");
            
            String queryTK = "SELECT * FROM TaiKhoan";
            PreparedStatement pstSQL_TK = connSQL.prepareStatement(queryTK);
            ResultSet rsTK = pstSQL_TK.executeQuery();

            String insertTK = "INSERT INTO TaiKhoan (Username, Password, Role) VALUES (?, ?, ?)";
            PreparedStatement pstLite_TK = connSQLite.prepareStatement(insertTK);
            
            int countTK = 0;
            while (rsTK.next()) {
                pstLite_TK.setString(1, rsTK.getString("Username"));
                pstLite_TK.setString(2, rsTK.getString("Password"));
                pstLite_TK.setString(3, rsTK.getString("Role")); 
                pstLite_TK.addBatch();
                countTK++;
            }
            pstLite_TK.executeBatch();
            pstLite_TK.close();
            pstSQL_TK.close();
            System.out.println(">>> Da chuyen " + countTK + " Tai khoan.");

            connSQLite.commit();
            System.out.println(">>> XONG! File konami_data.db da san sang de gui di.");

        } catch (Exception e) {
            e.printStackTrace();
            try { if (connSQLite != null) connSQLite.rollback(); } catch (Exception ex) {}
        } finally {
            try { if (connSQL != null) connSQL.close(); } catch (Exception ex) {}
            try { if (connSQLite != null) connSQLite.close(); } catch (Exception ex) {}
        }
    }
}