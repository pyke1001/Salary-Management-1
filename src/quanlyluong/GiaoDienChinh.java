//Cửa sổ tính lương - Đồng
package quanlyluong;

import javax.swing.*;
import java.awt.*; // Để dùng FlowLayout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiaoDienChinh extends JFrame {
    
    // Khai báo biến
    private JTextField txtLuongMotGio;
    private JTextField txtGioLamChuan;
    private JTextField txtGioTangCa;
    private JTextField txtHeSoTangCa;
    
    private JCheckBox chkNghiThaiSan;
    private JTextArea txtKetQua;
    private JButton btnTinhLuong;

    public GiaoDienChinh() {
        // 1. Cài đặt cửa sổ
        setTitle("Tính Lương Nhân Viên");
        setSize(1000, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout()); // Xếp các nút nối đuôi nhau cho đơn giản

        // 2. Khởi tạo các thành phần (Phải có đoạn này mới chạy được)
        add(new JLabel("Lương 1 giờ:"));
        txtLuongMotGio = new JTextField(10);
        add(txtLuongMotGio);

        add(new JLabel("Giờ làm chuẩn:"));
        txtGioLamChuan = new JTextField(10);
        add(txtGioLamChuan);

        add(new JLabel("Giờ tăng ca:"));
        txtGioTangCa = new JTextField(10);
        add(txtGioTangCa);

        add(new JLabel("Hệ số tăng ca:"));
        txtHeSoTangCa = new JTextField(10);
        add(txtHeSoTangCa);

        chkNghiThaiSan = new JCheckBox("Nghỉ thai sản?");
        add(chkNghiThaiSan);

        btnTinhLuong = new JButton("Tính Lương");
        add(btnTinhLuong);

        txtKetQua = new JTextArea(5, 30);
        add(new JScrollPane(txtKetQua));

        // 3. Gắn sự kiện bấm nút
        btnTinhLuong.addActionListener(e -> xuLyTinhLuong());
        
        // Hiển thị lên
        setVisible(true);
    }
    
    private void xuLyTinhLuong() {
        try {
            // Lấy dữ liệu từ giao diện
            double luong1Gio = Double.parseDouble(txtLuongMotGio.getText());
            double gioChuan  = Double.parseDouble(txtGioLamChuan.getText());
            double gioTangCa = Double.parseDouble(txtGioTangCa.getText());
            double heSo      = Double.parseDouble(txtHeSoTangCa.getText());
            
            boolean dangNghiThaiSan = chkNghiThaiSan.isSelected();

            // GỌI FILE MayTinhTienLuong ĐỂ TÍNH
            double tongLuong = MayTinhTienLuong.tinhTongLuong(luong1Gio, gioChuan, gioTangCa, heSo, dangNghiThaiSan);
                               
            txtKetQua.setText("Tổng lương nhận được: " + String.format("%,.0f", tongLuong) + " VNĐ");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!");
        }
    }

    // Hàm main để chạy thử giao diện này luôn
    public static void main(String[] args) {
        new GiaoDienChinh();
    }
}