package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyLuongVKU;encrypt=true;trustServerCertificate=true;";
            
            String user = "admin_luong";
            String pass = "123456";
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            conn = DriverManager.getConnection(dbURL, user, pass);
            System.out.println(">>> CHÚC MỪNG! Kết nối SQL Server thành công!");
            
        } catch (Exception ex) {
            System.out.println(">>> THẤT BẠI! Không kết nối được.");
            ex.printStackTrace();
        }
        return conn;
    }
    
    public static void main(String[] args) {
        getConnection();
    }
}