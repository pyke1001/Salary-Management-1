// Chức năng đăng nhập - Việt
package quanlyluong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.ConnectDB; // Import file kết nối của bạn

public class FormDangNhap extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnThoat;

    // K0N4M1
    private final int[] KONAMI_CODE = {
        KeyEvent.VK_UP, KeyEvent.VK_UP, 
        KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_B, KeyEvent.VK_A
    };
    private int currentPosition = 0;

    public FormDangNhap() {
    	ToolTipManager.sharedInstance().setInitialDelay(0);
    	
        setTitle("Đăng Nhập Hệ Thống");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255)); // Màu nền nhẹ

        // Logo hoặc Tiêu đề
        JLabel lblTitle = new JLabel("LOGIN SYSTEM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(0, 20, 400, 40);
        add(lblTitle);

        // Ô nhập User
        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setBounds(40, 80, 80, 25);
        add(lblUser);
        
        txtUser = new JTextField();
        txtUser.setBounds(120, 80, 200, 25);
        add(txtUser);

        // Ô nhập Password
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(40, 120, 80, 25);
        add(lblPass);
        
        txtPass = new JPasswordField();
        txtPass.setBounds(120, 120, 200, 25);
        add(txtPass);

        // Nút Đăng nhập
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBounds(120, 170, 100, 30);
        btnLogin.setBackground(new Color(46, 204, 113)); // Xanh lá
        btnLogin.setForeground(Color.WHITE);
        add(btnLogin);

        // Nút Thoát
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(230, 170, 90, 30);
        btnThoat.setBackground(new Color(231, 76, 60)); // Đỏ
        btnThoat.setForeground(Color.WHITE);
        add(btnThoat);

        // Sự kiện nút Đăng Nhập
        btnLogin.addActionListener(e -> xuLyDangNhap());

        // Sự kiện nút Thoát
        btnThoat.addActionListener(e -> System.exit(0));

        // Sự kiện nút...
        KeyListener konamiListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkKonamiCode(e.getKeyCode());
            }
        };
        //Thêm cái này để Enter cho lẹ
        txtPass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });
        
        txtUser.addKeyListener(konamiListener);
        txtPass.addKeyListener(konamiListener);
        this.addKeyListener(konamiListener);
        
        this.setFocusable(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtUser.requestFocusInWindow();
            }
        });
        JLabel lblHint = new JLabel("HINT: ↑ ↑ ↓ ↓ ← → ← → B A", SwingConstants.CENTER);
        
        lblHint.setBounds(0, 230, 400, 20); 
        
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 10)); 
        
        lblHint.setForeground(new Color(150, 150, 150)); 
        
        add(lblHint);

        lblHint.setToolTipText("Try it!");
    }

    // Hàm xử lí logic

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

    // Hàm kiểm tra K0N4M1
    private void checkKonamiCode(int keyCode) {
        // Nếu phím bấm trùng với ký tự tiếp theo trong chuỗi K0N4M1
        if (keyCode == KONAMI_CODE[currentPosition]) {
            currentPosition++; // Nhảy sang ký tự tiếp theo
            
            // Nếu đã bấm đúng hết toàn bộ chuỗi
            if (currentPosition == KONAMI_CODE.length) {
                kichHoatCheDoThanThanh();
                currentPosition = 0; // Reset lại từ đầu
            }
        } else {
            // Nếu bấm sai 1 phát là reset về 0 luôn (Phải bấm lại từ đầu)
            currentPosition = 0;
        }
    }

    private void kichHoatCheDoThanThanh() {
        // Hiệu ứng "Easter Egg"
        Toolkit.getDefaultToolkit().beep(); // Tiếng bíp hệ thống
        JOptionPane.showMessageDialog(this, 
            "㊙️ KONAMI CODE ACTIVATED! ㊙️\n Bạn đã nhận được quyền Admin!", 
            "Cheat Code", JOptionPane.INFORMATION_MESSAGE);
            
        moGiaoDienChinh();
    }

    private void moGiaoDienChinh() {
        this.dispose(); // Đóng form đăng nhập
        new FormNhanVien().setVisible(true); // Mở form chính của bạn
    }

    public static void main(String[] args) {
        new FormDangNhap().setVisible(true);
    }
}