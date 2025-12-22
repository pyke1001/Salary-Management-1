package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

public class DangNhapUI extends JFrame {
    
    private static final long serialVersionUID = 2L;
    
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    
    private final int[] KONAMI_CODE = {
        KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_B, KeyEvent.VK_A
    };
    private int currentPosition = 0;
    private static java.util.Set<String> unlockedAchievements = new java.util.HashSet<>();
    
    public DangNhapUI() {
        initUI();
        initEvents();
    }

    private void initUI() {
        ToolTipManager.sharedInstance().setInitialDelay(2000);
        ToolTipManager.sharedInstance().setDismissDelay(4000);
        
        setTitle("Konami Enterprise System - Secure Login"); 
        setSize(450, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        Color bgMain      = new Color(45, 45, 48);   
        Color fgText      = new Color(240, 240, 240); 
        Color accentColor = new Color(0, 120, 215);   
        Color placeHolder = new Color(170, 170, 170);
        
        getContentPane().setBackground(bgMain);

        JLabel lblHeader = new JLabel("KONAMI ENTERPRISE", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(accentColor);
        lblHeader.setBounds(0, 40, 450, 30);
        getContentPane().add(lblHeader);
        
        JLabel lblSubHeader = new JLabel("Identity Management System", SwingConstants.CENTER);
        lblSubHeader.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubHeader.setForeground(placeHolder);
        lblSubHeader.setBounds(0, 70, 450, 20);
        getContentPane().add(lblSubHeader);

        JLabel lblIconUser = new JLabel("👤");
        lblIconUser.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIconUser.setForeground(placeHolder);
        lblIconUser.setBounds(50, 120, 30, 35);
        getContentPane().add(lblIconUser);
        
        txtUser = new JTextField();
        txtUser.setBounds(90, 120, 290, 35);
        styleMaterialInput(txtUser, "Tên đăng nhập / Mã NV", bgMain, fgText, accentColor);
        getContentPane().add(txtUser);

        JLabel lblIconPass = new JLabel("🔒");
        lblIconPass.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIconPass.setForeground(placeHolder);
        lblIconPass.setBounds(50, 180, 30, 35);
        getContentPane().add(lblIconPass);
        
        txtPass = new JPasswordField();
        txtPass.setBounds(90, 180, 290, 35);
        styleMaterialInput(txtPass, "Mật khẩu truy cập", bgMain, fgText, accentColor);
        getContentPane().add(txtPass);

        btnLogin = new JButton("TRUY CẬP HỆ THỐNG");
        btnLogin.setBounds(90, 250, 290, 45); 
        styleModernButton(btnLogin, accentColor, Color.WHITE);
        getContentPane().add(btnLogin);

        JButton btnQuenMK = new JButton("Quên mật khẩu?");
        btnQuenMK.setBounds(177, 305, 120, 20);
        btnQuenMK.setForeground(placeHolder);
        btnQuenMK.setContentAreaFilled(false);
        btnQuenMK.setBorderPainted(false);
        btnQuenMK.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnQuenMK.setHorizontalAlignment(SwingConstants.LEFT);
        btnQuenMK.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        getContentPane().add(btnQuenMK);
        
        JLabel lblVersion = new JLabel("v1.2.0");
        lblVersion.setFont(new Font("Consolas", Font.ITALIC, 10));
        lblVersion.setForeground(new Color(100, 100, 100));
        lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);
        lblVersion.setBounds(336, 373, 75, 20);
        getContentPane().add(lblVersion);

        JLabel lblHint = new JLabel("↑ ↑ ↓ ↓ ← → ← → B A", SwingConstants.RIGHT);
        lblHint.setBounds(177, 100, 106, 20);
        lblHint.setFont(new Font("Consolas", Font.ITALIC, 10));
        lblHint.setForeground(new Color(70, 70, 70)); 
        getContentPane().add(lblHint);
        
        JLabel lblLilith = new JLabel("Lilith?", SwingConstants.RIGHT);
        lblLilith.setBounds(10, 373, 45, 20);
        lblLilith.setFont(new Font("Consolas", Font.ITALIC, 10));
        lblLilith.setForeground(new Color(70, 70, 70)); 
        getContentPane().add(lblLilith);
        
        btnQuenMK.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, "Tính năng đang phát triển!", "Support", JOptionPane.INFORMATION_MESSAGE);
        });
    } 
    
    private void styleMaterialInput(javax.swing.text.JTextComponent txt, String placeHolderText, Color bg, Color fg, Color accent) {
        txt.setBackground(bg);
        txt.setForeground(fg);
        txt.setCaretColor(accent); 
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txt.setToolTipText(placeHolderText); 
        
        txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY),
            javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0)
        ));
        
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, accent), 
                    javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0)
                ));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY), 
                    javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0)
                ));
            }
        });
    }

    private void styleModernButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
    }

    private void initEvents() {
        btnLogin.addActionListener(e -> xuLyDangNhap());
        txtPass.addActionListener(e -> xuLyDangNhap());
        
        txtUser.addActionListener(e -> xuLyDangNhap());

        KeyListener konamiListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                checkKonamiCode(e.getKeyCode());
            }
        };
        txtUser.addKeyListener(konamiListener);
        txtPass.addKeyListener(konamiListener);
        this.addKeyListener(konamiListener);

        this.setFocusable(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                txtUser.requestFocusInWindow();
            }
        });
    }

    private void xuLyDangNhap() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.equalsIgnoreCase("Lilith")) {
             Lilith(); // Gọi hàm hiển thị đẹp mắt
             return; // Dừng lại
        }
        
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        try {
            dao.NhanVienDAO dao = new dao.NhanVienDAO();
            String role = dao.kiemTraDangNhap(u, p);
            if (role != null) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                this.dispose();
                new QuanLyNhanVien(u.toUpperCase(), role).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối hoặc lỗi dữ liệu!");
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
            "㊙️ KONAMI CODE ACTIVATED! ㊙️\n Bạn đã nhận được quyền K.Hashimoto!", 
            "Cheat Code", JOptionPane.INFORMATION_MESSAGE);
        moGiaoDienChinh();
    }

    private void moGiaoDienChinh() {
        this.dispose();
        new QuanLyNhanVien("K_Hashimoto", "Admin").setVisible(true);
    }
    
    public static void checkAndUnlock(java.awt.Component parent, String eggName, String desc) {
        if (!unlockedAchievements.contains(eggName)) {
            unlockedAchievements.add(eggName);
            
            String msg = "<html><div style='text-align: center; width: 250px;'>" + 
                         "<font size='5' color='#E67E22'><b>🏆 THÀNH TỰU MỚI!</b></font><br><br>" + 
                         "<font size='4' color='#2980B9'><b>" + eggName + "</b></font><br>" +     
                         "<i>" + desc + "</i>" + 
                         "</div></html>";

            if (unlockedAchievements.size() >= 3) { 
                msg = msg.replace("</div></html>", 
                      "<br><br><font color='red'><b>🎁 HUYỀN THOẠI KONAMI ĐÃ ĐƯỢC MỞ KHÓA!</b></font></div></html>");
                }
            
            javax.swing.JOptionPane.showMessageDialog(parent, msg, "Achievement Unlocked", javax.swing.JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void Lilith() {
        // 1. Định nghĩa đường dẫn đến file ảnh của cậu
        // Đảm bảo cậu đã chép file ảnh vào đúng thư mục package /icon/
        String imagePath = "/icon/Lilith.png"; // <-- Thay tên file ảnh của cậu vào đây
        
        javax.swing.ImageIcon icon = null;
        try {
            // 2. Tải ảnh từ resource của dự án
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                icon = new javax.swing.ImageIcon(imgURL);
                
                // [TÙY CHỌN] Nếu ảnh quá to, bỏ comment 2 dòng dưới để thu nhỏ lại (ví dụ rộng 400px)
                java.awt.Image img = icon.getImage().getScaledInstance(500, -1, java.awt.Image.SCALE_SMOOTH);
                icon = new javax.swing.ImageIcon(img);
                
            } else {
                // Thông báo lỗi nếu không tìm thấy file ảnh
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Chưa tìm thấy file ảnh tại: " + imagePath + "\nHãy kiểm tra lại thư mục /icon/ nhé!", 
                    "Lỗi tải ảnh", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JButton btnOK = new JButton("...");
        btnOK.setBackground(new Color(233, 30, 99)); // Màu hồng Lilith
        btnOK.setForeground(Color.WHITE);
        btnOK.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOK.setFocusPainted(false);
        btnOK.setBorderPainted(false);
        // Đóng hộp thoại khi bấm nút
        btnOK.addActionListener(e -> javax.swing.SwingUtilities.getWindowAncestor(btnOK).dispose());

        Object[] options = {btnOK};

        // 4. Hiển thị hộp thoại với NỘI DUNG LÀ ẢNH (truyền biến icon vào)
        javax.swing.JOptionPane.showOptionDialog(this,
            icon, // <-- Bí quyết là ở đây: truyền ảnh vào làm nội dung thông báo
            "Lilith's Message ✨", // Tiêu đề hộp thoại
            javax.swing.JOptionPane.DEFAULT_OPTION,
            javax.swing.JOptionPane.PLAIN_MESSAGE,
            null, // Không dùng icon mặc định của hệ thống
            options, // Dùng nút tùy chỉnh của mình
            btnOK);
    }
    
    public static void main(String[] args) {
        DangNhapUI loginScreen = new DangNhapUI();
        loginScreen.setVisible(true);
        loginScreen.setLocationRelativeTo(null);
    }
}