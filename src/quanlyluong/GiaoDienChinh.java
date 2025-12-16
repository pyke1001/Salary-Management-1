package quanlyluong;
																	// Giao diện Tính Lương - Quốc, Tùng
import javax.swing.*;
import java.awt.*;

public class GiaoDienChinh extends JFrame {
    
    private static final long serialVersionUID = 1L;

    private JTextField txtLuongMotGio, txtGioLamChuan, txtGioTangCa, txtHeSoTangCa;
    private JCheckBox chkNghiThaiSan;
    private JTextArea txtKetQua;
    private JButton btnTinhLuong;

    public GiaoDienChinh() {
        initUI();
        initEvents();
    }

    // 1. Hàm khởi tạo giao diện
    private void initUI() {
        setTitle("Tính Lương Nhân Viên");
        setSize(1000, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Chỉ đóng cửa sổ này, không tắt app
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20)); // Căn giữa, khoảng cách thoáng hơn
        setLocationRelativeTo(null); 

        // Thêm các thành phần nhập liệu
        add(new JLabel("Lương 1 giờ:"));
        txtLuongMotGio = new JTextField("100000", 8); // Cho sẵn giá trị mặc định test cho lẹ
        add(txtLuongMotGio);

        add(new JLabel("Giờ làm chuẩn:"));
        txtGioLamChuan = new JTextField("160", 5);
        add(txtGioLamChuan);

        add(new JLabel("Giờ tăng ca:"));
        txtGioTangCa = new JTextField("0", 5);
        add(txtGioTangCa);

        add(new JLabel("Hệ số tăng ca:"));
        txtHeSoTangCa = new JTextField("1.5", 5);
        add(txtHeSoTangCa);

        chkNghiThaiSan = new JCheckBox("Nghỉ thai sản?");
        add(chkNghiThaiSan);

        // Nút tính lương
        btnTinhLuong = new JButton("💵 Tính Lương");
        // Dùng font Segoe UI cho đồng bộ với mấy file kia
        btnTinhLuong.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        add(btnTinhLuong);

        // Ô kết quả
        txtKetQua = new JTextArea(3, 40);
        txtKetQua.setEditable(false); // Chỉ cho xem, không cho sửa
        add(new JScrollPane(txtKetQua));
    }

    // 2. Hàm bắt sự kiện
    private void initEvents() {
        btnTinhLuong.addActionListener(e -> xuLyTinhLuong());
    }

    // 3. Hàm xử lý logic tính toán
    private void xuLyTinhLuong() {
        try {
            // Lấy dữ liệu từ ô nhập
            double luong1Gio = Double.parseDouble(txtLuongMotGio.getText());
            double gioChuan  = Double.parseDouble(txtGioLamChuan.getText());
            double gioTangCa = Double.parseDouble(txtGioTangCa.getText());
            double heSo      = Double.parseDouble(txtHeSoTangCa.getText());
            boolean dangNghiThaiSan = chkNghiThaiSan.isSelected();

            //Gọi class xử lí Logic
            
            double tongLuongGross = MayTinhTienLuong.tinhTongLuong(luong1Gio, gioChuan, gioTangCa, heSo, dangNghiThaiSan);

            int soNguoiPhuThuoc = 0; 
            double tienBaoHiem = CongCuThue.tinhBaoHiem(tongLuongGross);
            double tienThue = CongCuThue.tinhThueTNCN(tongLuongGross, soNguoiPhuThuoc);
            double thucLinh = tongLuongGross - tienBaoHiem - tienThue;
            
            // Format hiển thị
            String chiTiet = String.format(
                "Tổng lương (Gross): %,.0f VNĐ\n" +
                "Bảo hiểm (10.5%%): -%,.0f VNĐ\n" +
                "Thuế TNCN: -%,.0f VNĐ\n" +
                "------------------------\n" +
                "THỰC LĨNH: %,.0f VNĐ",
                tongLuongGross, tienBaoHiem, tienThue, thucLinh
            );

            // Hiện popup thông báo
            JOptionPane.showMessageDialog(this, chiTiet, "Kết Quả Tính Lương", JOptionPane.INFORMATION_MESSAGE);

            // Ghi vào ô text area
            txtKetQua.setText(chiTiet);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}