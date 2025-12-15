/*
File này hiển thị thông tin ra màn hình
*/
package quanlyluong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FormThongKe extends JFrame {

    public FormThongKe() {
        setTitle("Thống Kê Tình Hình Nhân Sự");
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(null); // Ra giữa màn hình
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Đóng form này không tắt form chính

        // Tiêu đề
        JLabel lblTitle = new JLabel("BẢNG THỐNG KÊ TỔNG HỢP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.BLUE);
        lblTitle.setBounds(0, 20, 600, 30);
        add(lblTitle);

        // Gọi DAO để lấy số liệu thực tế
        ThongKeDAO dao = new ThongKeDAO();
        int tongNV = dao.tongNhanVien();
        double tongTien = dao.tongLuongCoBan();
        double maxLuong = dao.luongCaoNhat();
        double tbLuong = dao.luongTrungBinh();

        // --- VẼ 4 CÁI HỘP HIỂN THỊ ---

        // Hộp 1: Tổng nhân viên
        JPanel pnNV = taoPanel("Tổng Nhân Viên", String.valueOf(tongNV) + " người", Color.ORANGE, 50, 80);
        add(pnNV);

        // Hộp 2: Tổng quỹ lương
        String strTongTien = String.format("%,.0f VNĐ", tongTien);
        JPanel pnTien = taoPanel("Tổng Quỹ Lương", strTongTien, new Color(46, 204, 113), 300, 80); // Màu xanh lá
        add(pnTien);

        // Hộp 3: Lương cao nhất
        String strMax = String.format("%,.0f VNĐ", maxLuong);
        JPanel pnMax = taoPanel("Lương Cao Nhất", strMax, new Color(52, 152, 219), 50, 200); // Màu xanh dương
        add(pnMax);

        // Hộp 4: Lương trung bình
        String strTB = String.format("%,.0f VNĐ", tbLuong);
        JPanel pnTB = taoPanel("Lương Trung Bình", strTB, new Color(155, 89, 182), 300, 200); // Màu tím
        add(pnTB);

        // Nút Đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.setBounds(250, 310, 100, 30);
        btnClose.addActionListener(e -> dispose());
        add(btnClose);
    }

    // Hàm phụ trợ để vẽ mấy cái hộp màu cho nhanh (đỡ phải copy paste code)
    private JPanel taoPanel(String tieuDe, String giaTri, Color mauNen, int x, int y) {
        JPanel pn = new JPanel();
        pn.setLayout(null);
        pn.setBackground(mauNen);
        pn.setBounds(x, y, 220, 100);
        
        JLabel lblHeader = new JLabel(tieuDe, SwingConstants.CENTER);
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 14));
        lblHeader.setBounds(0, 10, 220, 20);
        pn.add(lblHeader);
        
        JLabel lblValue = new JLabel(giaTri, SwingConstants.CENTER);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.BOLD, 18));
        lblValue.setBounds(0, 40, 220, 30);
        pn.add(lblValue);
        
        return pn;
    }
    
    // Hàm main để test thử riêng form này
    public static void main(String[] args) {
        new FormThongKe().setVisible(true);
    }
}