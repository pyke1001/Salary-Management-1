package database;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnectDB {
    private static final String CONFIG_FILE = "database.config";

    public static Connection getConnection() {
        Properties props = new Properties();
        boolean coFileCauHinh = false; 
        
        try {
            File f = new File(getRealPath(CONFIG_FILE));
            if (f.exists()) {
                try (FileInputStream in = new FileInputStream(f)) { 
                    props.load(in); 
                    coFileCauHinh = true; 
                }
            } else {
                setDefaults(props);
            }
        } catch (Exception ex) {
            setDefaults(props);
        }

        if (!coFileCauHinh) {
            if (showConfigDialog(props)) System.exit(0);
        }

        while (true) {
            try {
                Connection conn = tryConnect(props);
                if (conn != null) {
                    kiemTraVaCapNhatCauTruc(conn); 
                    return conn;
                }
            } catch (Exception ex) {}

            if (showConfigDialog(props)) System.exit(0);
        }
    }

    private static void setDefaults(Properties props) {
        props.setProperty("dbType", "SQLite");
        props.setProperty("host", "localhost");
        props.setProperty("port", "1433");
        props.setProperty("dbName", "Konami");
        props.setProperty("user", "sa");
        props.setProperty("pass", "");
    }

    private static Connection tryConnect(Properties props) {
        try {
            String type = props.getProperty("dbType", "SQLite");
            Connection c = null;

            if ("SQLite".equals(type)) {
                Class.forName("org.sqlite.JDBC");
                String dbPath = getRealPath("konami_data.db");
                c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            } else {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String url = "jdbc:sqlserver://" + props.getProperty("host") + ":" + props.getProperty("port") +
                             ";databaseName=" + props.getProperty("dbName") + ";encrypt=true;trustServerCertificate=true;";
                c = DriverManager.getConnection(url, props.getProperty("user"), props.getProperty("pass"));
            }

            if (c != null) saveConfig(props);
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    private static void kiemTraVaCapNhatCauTruc(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            String dbType = conn.getMetaData().getDatabaseProductName();
            
            if (dbType.contains("SQLite")) {
                String[] cols = {
                    "ALTER TABLE NhanVien ADD COLUMN GioTangCa DOUBLE DEFAULT 0",
                    "ALTER TABLE NhanVien ADD COLUMN HeSoTangCa DOUBLE DEFAULT 1.5",
                    "ALTER TABLE NhanVien ADD COLUMN ThucLinh BIGINT DEFAULT 0",
                    "ALTER TABLE NhanVien ADD COLUMN LyDoThuongPhat TEXT DEFAULT ''"
                };
                for (String sql : cols) try { stmt.executeUpdate(sql); } catch (Exception e) {}
            } else {
                // SQL Server
                String[] cols = {
                    "ALTER TABLE NhanVien ADD GioTangCa FLOAT DEFAULT 0",
                    "ALTER TABLE NhanVien ADD HeSoTangCa FLOAT DEFAULT 1.5",
                    "ALTER TABLE NhanVien ADD ThucLinh BIGINT DEFAULT 0",
                    "ALTER TABLE NhanVien ADD LyDoThuongPhat NVARCHAR(MAX) DEFAULT ''"
                };
                for (String sql : cols) try { stmt.executeUpdate(sql); } catch (Exception e) {}
            }

            if (dbType.contains("SQLite")) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS HopThu (ID INTEGER PRIMARY KEY AUTOINCREMENT, MaNV TEXT, TieuDe TEXT, NoiDung TEXT, NgayGui TEXT DEFAULT (datetime('now', 'localtime')), DaXem INTEGER DEFAULT 0)");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS BangLuongLuuTru (ID INTEGER PRIMARY KEY AUTOINCREMENT, MaNV TEXT, HoTen TEXT, Thang INTEGER, Nam INTEGER, LuongCung BIGINT, TienThuong BIGINT, TienPhat BIGINT, ThucLinh BIGINT, LyDoGhiChu TEXT)");
            } else {

                try {
                    stmt.executeUpdate("CREATE TABLE HopThu (ID INT PRIMARY KEY IDENTITY(1,1), MaNV VARCHAR(50), TieuDe NVARCHAR(255), NoiDung NVARCHAR(MAX), NgayGui DATETIME DEFAULT GETDATE(), DaXem INT DEFAULT 0)");
                } catch (Exception e) {}
                
                try {
                    stmt.executeUpdate("CREATE TABLE BangLuongLuuTru (ID INT PRIMARY KEY IDENTITY(1,1), MaNV VARCHAR(50), HoTen NVARCHAR(100), Thang INT, Nam INT, LuongCung BIGINT, TienThuong BIGINT, TienPhat BIGINT, ThucLinh BIGINT, LyDoGhiChu NVARCHAR(MAX))");
                } catch (Exception e) {}
            }
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRealPath(String fileName) {
        try {
            URI uri = ConnectDB.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            return new File(new File(uri).getParent(), fileName).getCanonicalPath();
        } catch (Exception ex) {
            return fileName;
        }
    }

    private static boolean showConfigDialog(Properties props) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        String[] types = {"SQL Server", "SQLite"};
        JComboBox<String> cbType = new JComboBox<>(types);
        cbType.setSelectedItem(props.getProperty("dbType"));
        JTextField txtHost = new JTextField(props.getProperty("host"));
        JTextField txtPort = new JTextField(props.getProperty("port"));
        JTextField txtDbName = new JTextField(props.getProperty("dbName"));
        JTextField txtUser = new JTextField(props.getProperty("user"));
        JPasswordField txtPass = new JPasswordField(props.getProperty("pass"));
        
        panel.add(new JLabel("Loại CSDL:")); panel.add(cbType);
        panel.add(new JLabel("IP Host:")); panel.add(txtHost);
        panel.add(new JLabel("Port:")); panel.add(txtPort);
        panel.add(new JLabel("Tên DB:")); panel.add(txtDbName);
        panel.add(new JLabel("User:")); panel.add(txtUser);
        panel.add(new JLabel("Pass:")); panel.add(txtPass);
        
        int result = JOptionPane.showConfirmDialog(null, panel, "Cấu hình Kết Nối", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            props.setProperty("dbType", cbType.getSelectedItem().toString());
            props.setProperty("host", txtHost.getText());
            props.setProperty("port", txtPort.getText());
            props.setProperty("dbName", txtDbName.getText());
            props.setProperty("user", txtUser.getText());
            props.setProperty("pass", new String(txtPass.getPassword()));
            saveConfig(props);
            return false;
        }
        return true;
    }

    private static void saveConfig(Properties props) {
        try (FileOutputStream out = new FileOutputStream(getRealPath(CONFIG_FILE))) {
            props.store(out, null);
        } catch (Exception ex) {}
    }
}