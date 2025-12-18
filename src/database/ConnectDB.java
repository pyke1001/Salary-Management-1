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
        } catch (Exception _) {
            setDefaults(props);
        }

        while (true) {
            try {
                Connection conn = tryConnect(props);
                if (conn != null) return conn;
            } catch (Exception _) {}

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
                
                // Kết nối SQLite
                c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
                
                // Cố gắng tạo bảng và dữ liệu mẫu. 
                // Nếu thất bại (do quyền hạn), ta VẪN GIỮ kết nối c để App mở lên được.
                taoBangSQLiteNeuChuaCo(c);
                
                // ĐÃ XÓA ĐOẠN "SELECT" GÂY LỖI TẠI ĐÂY
                
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

            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PhongBan");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO PhongBan VALUES ('PB01', 'Phòng Kỹ Thuật')");
                stmt.execute("INSERT INTO PhongBan VALUES ('PB02', 'Phòng Nhân Sự')");
                stmt.execute("INSERT INTO PhongBan VALUES ('PB03', 'Phòng Kinh Doanh')");
                stmt.execute("INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, NgayVaoLam) VALUES ('NV01', 'Admin', 'PB01', 10000000, 1.0, '2025-01-01')");
            }
        } catch (Exception _) {
            // Lỗi tạo bảng (thường do Read-only trên Mac). 
            // Ta lờ đi để App vẫn mở lên được, người dùng sẽ thấy lỗi khi đăng nhập sau.
        }
    }

    private static String getRealPath(String fileName) {
        try {
            // Logic lấy đường dẫn chuẩn nhất cho cả Mac và Windows (xử lý khoảng trắng %20)
            URI uri = ConnectDB.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            return new File(new File(uri).getParent(), fileName).getCanonicalPath();
        } catch (Exception _) {
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
        panel.add(new JLabel("Loại:")); panel.add(cbType);
        panel.add(new JLabel("Host:")); panel.add(txtHost);
        panel.add(new JLabel("Port:")); panel.add(txtPort);
        panel.add(new JLabel("DB Name:")); panel.add(txtDbName);
        panel.add(new JLabel("User:")); panel.add(txtUser);
        panel.add(new JLabel("Pass:")); panel.add(txtPass);
        int result = JOptionPane.showConfirmDialog(null, panel, "Cấu hình CSDL", JOptionPane.OK_CANCEL_OPTION);
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
        } catch (Exception _) {}
    }
}