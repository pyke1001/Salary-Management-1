package database;

import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnectDB {
    
    private static final String CONFIG_FILE = "database.config";

    public static Connection getConnection() {
        Connection conn = null;
        Properties props = new Properties();
        
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (Exception e) {
            props.setProperty("host", "localhost");
            props.setProperty("port", "1433");
            props.setProperty("dbName", "Konami");
            props.setProperty("user", "sa");
            props.setProperty("pass", "");
        }

        conn = tryConnect(props);

        while (conn == null) {
            boolean userMuonThoat = showConfigDialog(props);
            if (userMuonThoat) {
                System.exit(0);
            }
            conn = tryConnect(props);
        }
        
        return conn;
    }

    private static Connection tryConnect(Properties props) {
        try {
            String dbURL = "jdbc:sqlserver://" + props.getProperty("host") + ":" + props.getProperty("port") +
                           ";databaseName=" + props.getProperty("dbName") +
                           ";encrypt=true;trustServerCertificate=true;";
            
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection c = DriverManager.getConnection(dbURL, props.getProperty("user"), props.getProperty("pass"));
            
            saveConfig(props);
            System.out.println(">>> KẾT NỐI THÀNH CÔNG!");
            return c;
        } catch (Exception e) {
            System.out.println(">>> Kết nối thất bại: " + e.getMessage());
            return null;
        }
    }

    private static boolean showConfigDialog(Properties props) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        JTextField txtHost = new JTextField(props.getProperty("host"));
        JTextField txtPort = new JTextField(props.getProperty("port"));
        JTextField txtDbName = new JTextField(props.getProperty("dbName"));
        JTextField txtUser = new JTextField(props.getProperty("user"));
        JPasswordField txtPass = new JPasswordField(props.getProperty("pass"));

        panel.add(new JLabel("Host (IP):"));    panel.add(txtHost);
        panel.add(new JLabel("Port:"));         panel.add(txtPort);
        panel.add(new JLabel("Database Name:"));panel.add(txtDbName);
        panel.add(new JLabel("SQL User:"));     panel.add(txtUser);
        panel.add(new JLabel("Password:"));     panel.add(txtPass);

        int result = JOptionPane.showConfirmDialog(null, panel, 
                "⚠️ Lỗi kết nối CSDL! Vui lòng thử lại", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            props.setProperty("host", txtHost.getText());
            props.setProperty("port", txtPort.getText());
            props.setProperty("dbName", txtDbName.getText());
            props.setProperty("user", txtUser.getText());
            props.setProperty("pass", new String(txtPass.getPassword()));
            return false; 
        }
        return true; 
    }

    private static void saveConfig(Properties props) {
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Konami App Database Configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}