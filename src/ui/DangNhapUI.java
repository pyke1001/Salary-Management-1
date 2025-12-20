package ui;
																	// Login - Việt
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
    private JButton btnLogin, btnThoat;
    
    private final int[] KONAMI_CODE = {
        KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, 
        KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, 
        KeyEvent.VK_B, KeyEvent.VK_A
    };
    private int currentPosition = 0;
    private static java.util.Set<String> unlockedAchievements = new java.util.HashSet<>();
    
    public DangNhapUI() {																	// Hàm khởi tạo
        initUI();
        initEvents();
    }

    private void initUI() {																	// Hàm 'Giao diện đăng nhập'
        ToolTipManager.sharedInstance().setInitialDelay(2000);
        ToolTipManager.sharedInstance().setDismissDelay(4000);
        setTitle("Đăng Nhập Konami Enterprise");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(240, 248, 255));

        JLabel lblTitle = new JLabel("LOGIN KONAMI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(0, 20, 400, 40);
        getContentPane().add(lblTitle);

        JLabel lblUser = new JLabel("👤 Tài khoản:");
        lblUser.setBounds(40, 80, 80, 25);
        getContentPane().add(lblUser);
        
        txtUser = new JTextField();
        txtUser.setBounds(120, 80, 200, 25);
        getContentPane().add(txtUser);

        JLabel lblPass = new JLabel("🔒  Mật khẩu:");
        lblPass.setBounds(40, 120, 80, 25);
        getContentPane().add(lblPass);
        
        txtPass = new JPasswordField();
        txtPass.setBounds(120, 120, 200, 25);
        getContentPane().add(txtPass);

        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBounds(120, 170, 100, 30);
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        getContentPane().add(btnLogin);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(230, 170, 90, 30);
        btnThoat.setBackground(new Color(231, 76, 60));
        btnThoat.setForeground(Color.WHITE);
        getContentPane().add(btnThoat);
        
        JButton btnQuenMK = new JButton("Quên MK?");
        btnQuenMK.setBounds(176, 210, 100, 30);
        btnQuenMK.setBackground(new Color(255, 182, 193));
        btnQuenMK.setFont(new Font("Segoe UI", Font.BOLD, 11));
        getContentPane().add(btnQuenMK);

        btnQuenMK.addActionListener(e -> {
            String thongBao = "Quên mật khẩu?\n" +
                              "Vui lòng liên hệ Admin qua số XXXX-XXX-772 hoặc user 'pyke1001' tại Discord!\n" +
                              "(Warning: Đừng thắc mắc về tên Discord của Admin)";
            
            JOptionPane.showMessageDialog(this, thongBao, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        JLabel lblHint = new JLabel("HINT: ↑ ↑ ↓ ↓ ← → ← → B A", SwingConstants.CENTER);
        lblHint.setBounds(0, 56, 400, 20);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblHint.setForeground(new Color(150, 150, 150));
        lblHint.setToolTipText("Try it!");
        getContentPane().add(lblHint);
        
        JLabel lblVersion = new JLabel("v1.0.0");									// Cập nhật Version tại đây
        lblVersion.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblVersion.setForeground(Color.GRAY);
        lblVersion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblVersion.setBounds(315, 243, 69, 20); 
        
        getContentPane().add(lblVersion);
    }

    private void initEvents() {																// Hàm 'Xử lí sự kiện'
        btnLogin.addActionListener(e -> xuLyDangNhap());
        btnThoat.addActionListener(e -> System.exit(0));
        txtPass.addActionListener(e -> xuLyDangNhap());

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

    private void xuLyDangNhap() {															// Hàm 'Đăng nhập'
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());

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
    private void checkKonamiCode(int keyCode) {												// Hàm 'Konami - Kiểm tra'
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

    private void kichHoatKonami() {															// Hàm 'Konami - Kích hoạt'
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this, 
            "㊙️ KONAMI CODE ACTIVATED! ㊙️\n Bạn đã nhận được quyền Admin!", 
            "Cheat Code", JOptionPane.INFORMATION_MESSAGE);
        moGiaoDienChinh();
    }

    private void moGiaoDienChinh() {														// Hàm 'Mở giao diện phần mềm'
        this.dispose();
        new QuanLyNhanVien("K_Hashimoto", "Admin").setVisible(true);
    }
    
 // Hàm kiểm tra và trao giải thưởng (Đã căn giữa đẹp mắt)
    public static void checkAndUnlock(java.awt.Component parent, String eggName, String desc) {
        if (!unlockedAchievements.contains(eggName)) {
            unlockedAchievements.add(eggName);
            
            // --- DÙNG HTML ĐỂ CĂN GIỮA VÀ ĐỊNH DẠNG CHỮ ---
            String msg = "<html><div style='text-align: center; width: 250px;'>" + // Định độ rộng để ép xuống dòng đẹp
                         "<font size='5' color='#E67E22'><b>🏆 THÀNH TỰU MỚI!</b></font><br><br>" + // Tiêu đề màu cam to
                         "<font size='4' color='#2980B9'><b>" + eggName + "</b></font><br>" +     // Tên trứng màu xanh
                         "<i>" + desc + "</i>" + // Mô tả in nghiêng
                         "</div></html>";
            // -----------------------------------------------

            // Nếu tìm đủ 3 trứng thì thêm lời chúc mừng
            if (unlockedAchievements.size() >= 3) { 
                msg = msg.replace("</div></html>", 
                      "<br><br><font color='red'><b>🎁 HUYỀN THOẠI KONAMI ĐÃ ĐƯỢC MỞ KHÓA!</b></font></div></html>");
                
                // Kích hoạt giao diện vàng (nếu đang ở màn hình chính)
                if (parent instanceof QuanLyNhanVien) {
                    ((QuanLyNhanVien) parent).kichHoatGiaoDienHoangKim();
                }
            }
            
            javax.swing.JOptionPane.showMessageDialog(parent, msg, "Achievement Unlocked", javax.swing.JOptionPane.PLAIN_MESSAGE);
        }
    }
    public static void main(String[] args) {												// Hàm main
        DangNhapUI loginScreen = new DangNhapUI();
        loginScreen.setVisible(true);
        loginScreen.setLocationRelativeTo(null);
    }
}