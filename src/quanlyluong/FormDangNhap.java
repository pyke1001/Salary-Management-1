package quanlyluong;
																		//Login - Việt
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import database.ConnectDB;

public class FormDangNhap extends JFrame {
	
	private static final long serialVersionUID = 1L;

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnThoat;
    
    // Logic Konami Code
    private final int[] KONAMI_CODE = {
        KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_B, KeyEvent.VK_A
    };
    private int currentPosition = 0;

    public FormDangNhap() {
        initUI();
        initEvents();
    }

    private void initUI() {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        setTitle("Đăng Nhập Hệ Thống");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        // Tiêu đề
        JLabel lblTitle = new JLabel("LOGIN SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(0, 20, 400, 40);
        add(lblTitle);

        // Username
        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setBounds(40, 80, 80, 25);
        add(lblUser);
        
        txtUser = new JTextField();
        txtUser.setBounds(120, 80, 200, 25);
        add(txtUser);

        // Password
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(40, 120, 80, 25);
        add(lblPass);
        
        txtPass = new JPasswordField();
        txtPass.setBounds(120, 120, 200, 25);
        add(txtPass);

        // Buttons
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBounds(120, 170, 100, 30);
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        add(btnLogin);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(230, 170, 90, 30);
        btnThoat.setBackground(new Color(231, 76, 60));
        btnThoat.setForeground(Color.WHITE);
        add(btnThoat);

        // Hint Label
        JLabel lblHint = new JLabel("HINT: ↑ ↑ ↓ ↓ ← → ← → B A", SwingConstants.CENTER);
        lblHint.setBounds(0, 230, 400, 20);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblHint.setForeground(new Color(150, 150, 150));
        lblHint.setToolTipText("Try it!");
        add(lblHint);
    }

    private void initEvents() {
        btnLogin.addActionListener(e -> xuLyDangNhap());
        btnThoat.addActionListener(e -> System.exit(0));
        txtPass.addActionListener(e -> xuLyDangNhap()); // Enter để login

        // Konami Code Listener
        KeyListener konamiListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkKonamiCode(e.getKeyCode());
            }
        };
        txtUser.addKeyListener(konamiListener);
        txtPass.addKeyListener(konamiListener);
        this.addKeyListener(konamiListener);

        // Focus logic
        this.setFocusable(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                txtUser.requestFocusInWindow();
            }
        });
    }

    private void xuLyDangNhap() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            Connection conn = ConnectDB.getConnection();
            String sql = "SELECT * FROM TaiKhoan WHERE Username = ? AND Password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u);
            ps.setString(2, p);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                moGiaoDienChinh();
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL!");
        }
    }

    private void checkKonamiCode(int keyCode) {
        if (keyCode == KONAMI_CODE[currentPosition]) {
            currentPosition++;
            if (currentPosition == KONAMI_CODE.length) {
                kichHoatKonami();
                currentPosition = 0;
            }
        } else {
            currentPosition = 0;
        }
    }

    private void kichHoatKonami() {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this, 
            "㊙️ KONAMI CODE ACTIVATED! ㊙️\n Bạn đã nhận được quyền Admin!", 
            "Cheat Code", JOptionPane.INFORMATION_MESSAGE);
        moGiaoDienChinh();
    }

    private void moGiaoDienChinh() {
        this.dispose();
        new FormNhanVien().setVisible(true);
    }

    public static void main(String[] args) {
        new FormDangNhap().setVisible(true);
    }
}