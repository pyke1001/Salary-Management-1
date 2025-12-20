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
        
        try {
            File f = new File(getRealPath(CONFIG_FILE));
            if (f.exists()) {
                try (FileInputStream in = new FileInputStream(f)) { props.load(in); }
            } else {
                setDefaults(props);
            }
        } catch (Exception ex) {
            setDefaults(props);
        }

        while (true) {
            try {
                Connection conn = tryConnect(props);
                if (conn != null) return conn;
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
                
                javax.swing.JOptionPane.showMessageDialog(null, "App đang tìm DB tại:\n" + dbPath);
                
                c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                
                taoBangSQLiteNeuChuaCo(c);
                
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

    private static void taoBangSQLiteNeuChuaCo(Connection c) {
        try (Statement stmt = c.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS PhongBan (MaPB TEXT PRIMARY KEY, TenPB TEXT)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS NhanVien (" +
                         "MaNV TEXT PRIMARY KEY, HoTen TEXT, MaPB TEXT, " +
                         "LuongCoBan INTEGER, HeSoLuong REAL, NgayVaoLam TEXT, " + 
                         "SoNgayDiTre INTEGER DEFAULT 0, TienPhat INTEGER DEFAULT 0, " +
                         "TienThuong INTEGER DEFAULT 0, " +
                         "FOREIGN KEY(MaPB) REFERENCES PhongBan(MaPB))");

            stmt.execute("CREATE TABLE IF NOT EXISTS TaiKhoan (" +
                         "Username TEXT PRIMARY KEY, " +
                         "Password TEXT, " +
                         "Role TEXT, " +
                         "FOREIGN KEY(Username) REFERENCES NhanVien(MaNV))");

            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PhongBan");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO PhongBan VALUES ('PB01', 'Phòng Admin')");
                stmt.execute("INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, NgayVaoLam) " +
                             "VALUES ('NV01', 'Super Admin', 'PB01', 99999999, 1.0, '2025-01-01')");
                stmt.execute("INSERT INTO TaiKhoan (Username, Password, Role) VALUES ('NV01', '123456', 'Admin')");
            }
        } catch (Exception ex) {}
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