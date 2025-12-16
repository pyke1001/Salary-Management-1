package quanlyluong;
																	// Giao diện Thống Kê - Hướng
import javax.swing.*;
import java.awt.*;

public class FormThongKe extends JFrame {
	
	private static final long serialVersionUID = 1L;

    public FormThongKe() {
        initUI();
        loadAndShowData();
    }

    private void initUI() {
        setTitle("Thống Kê Tình Hình Nhân Sự");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        // Tiêu đề
        JLabel lblTitle = new JLabel("BẢNG THỐNG KÊ TỔNG HỢP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(0, 20, 600, 30);
        add(lblTitle);

        // Nút Đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.setBounds(250, 310, 100, 30);
        btnClose.addActionListener(e -> dispose());
        add(btnClose);
    }

    private void loadAndShowData() {
        // 1. Gọi DAO lấy số liệu
        ThongKeDAO dao = new ThongKeDAO();
        int tongNV = dao.tongNhanVien();
        double tongTien = dao.tongLuongCoBan();
        double maxLuong = dao.luongCaoNhat();
        double tbLuong = dao.luongTrungBinh();

        // 2. Format tiền tệ cho đẹp
        String strTongTien = String.format("%,.0f VNĐ", tongTien);
        String strMax = String.format("%,.0f VNĐ", maxLuong);
        String strTB = String.format("%,.0f VNĐ", tbLuong);

        // 3. Vẽ 4 hộp thống kê (Gọi hàm phụ trợ)
        // Hộp: Tổng NV (Cam)
        add(createStatPanel("Tổng Nhân Viên", tongNV + " người", Color.ORANGE, 50, 80));
        
        // Hộp: Tổng Quỹ Lương (Xanh lá)
        add(createStatPanel("Tổng Quỹ Lương", strTongTien, new Color(46, 204, 113), 300, 80));
        
        // Hộp: Lương Cao Nhất (Xanh dương)
        add(createStatPanel("Lương Cao Nhất", strMax, new Color(52, 152, 219), 50, 200));
        
        // Hộp: Lương Trung Bình (Tím)
        add(createStatPanel("Lương Trung Bình", strTB, new Color(155, 89, 182), 300, 200));
    }

    // Hàm phụ trợ tạo Panel thống kê (Code Factory)
    private JPanel createStatPanel(String title, String value, Color bgColor, int x, int y) {
        JPanel pn = new JPanel(null);
        pn.setBackground(bgColor);
        pn.setBounds(x, y, 220, 100);
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBounds(0, 15, 220, 20);
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setBounds(0, 45, 220, 30);
        
        pn.add(lblTitle);
        pn.add(lblValue);
        return pn;
    }
    
    public static void main(String[] args) {
        // Fix font hiển thị đẹp
        try {
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception e) {}

        new FormThongKe().setVisible(true);
    }
}