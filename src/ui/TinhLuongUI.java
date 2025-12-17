package ui;
																		// Giao diện Tính lương - Đồng
import javax.swing.*;
import java.awt.*;
import logic.MayTinhLuong;

public class TinhLuongUI extends JFrame {
    
    private static final long serialVersionUID = 1L;

    private JTextField txtLuongMotGio, txtGioLamChuan, txtGioTangCa, txtHeSoTangCa;
    private JCheckBox chkNghiThaiSan;
    private JTextArea txtKetQua;
    private JButton btnTinhLuong;

    public TinhLuongUI() {
        initUI();
        initEvents();
    }

    private void initUI() {
        setTitle("Tính Lương Nhân Viên");
        setSize(1000, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20)); 
        setLocationRelativeTo(null); 

        add(new JLabel("Lương 1 giờ:"));
        txtLuongMotGio = new JTextField("100000", 8); 
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

        btnTinhLuong = new JButton("💵 Tính Lương");
        btnTinhLuong.setFont(new Font("Dialog", Font.BOLD, 14)); 
        add(btnTinhLuong);

        txtKetQua = new JTextArea(3, 40);
        txtKetQua.setEditable(false); 
        add(new JScrollPane(txtKetQua));
    }

    private void initEvents() {
        btnTinhLuong.addActionListener(e -> xuLyTinhLuong());
    }

    private void xuLyTinhLuong() {
        try {
            double luong1Gio = Double.parseDouble(txtLuongMotGio.getText());
            double gioChuan  = Double.parseDouble(txtGioLamChuan.getText());
            double gioTangCa = Double.parseDouble(txtGioTangCa.getText());
            double heSo      = Double.parseDouble(txtHeSoTangCa.getText());
            boolean dangNghiThaiSan = chkNghiThaiSan.isSelected();

            double tongLuongGross = MayTinhLuong.tinhTongLuong(luong1Gio, gioChuan, gioTangCa, heSo, dangNghiThaiSan);

            int soNguoiPhuThuoc = 0; 
            double tienBaoHiem = MayTinhLuong.tinhBaoHiem(tongLuongGross);
            double tienThue = MayTinhLuong.tinhThueTNCN(tongLuongGross, soNguoiPhuThuoc);
            double thucLinh = tongLuongGross - tienBaoHiem - tienThue;
            
            String chiTiet = String.format(
                "Tổng lương (Gross): %,.0f VNĐ\n" +
                "Bảo hiểm (10.5%%): -%,.0f VNĐ\n" +
                "Thuế TNCN: -%,.0f VNĐ\n" +
                "------------------------\n" +
                "THỰC LĨNH: %,.0f VNĐ",
                tongLuongGross, tienBaoHiem, tienThue, thucLinh
            );

            JOptionPane.showMessageDialog(this, chiTiet, "Kết Quả Tính Lương", JOptionPane.INFORMATION_MESSAGE);
            txtKetQua.setText(chiTiet);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}