package ui;
																		// Giao diện Tính lương - Đồng
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import logic.MayTinhLuong;

public class TinhLuongUI extends JFrame {
    
    private static final long serialVersionUID = 1L;

    // Các ô nhập liệu
    private JTextField txtHoTen, txtLuongCung, txtLuongMotGio, txtGioLamChuan, txtGioTangCa, txtHeSoTangCa;
    private JCheckBox chkNghiThaiSan;
    private JTextArea txtKetQua;
    private JButton btnTinhLuong;
    
    // Biến lưu dữ liệu truyền từ bảng chính sang
    private String hoTenNV;
    private long luongCoBan;

    // Constructor mặc định (ít dùng)
    public TinhLuongUI() {
        this("Nhân viên vãng lai", 0);
    }

    // Constructor CÓ THAM SỐ (Dùng cái này để nhận dữ liệu)
    public TinhLuongUI(String hoTen, long luongCoBan) {
        this.hoTenNV = hoTen;
        this.luongCoBan = luongCoBan;
        initUI();
        initEvents();
        
        // Tự động điền dữ liệu ngay khi mở lên
        dienDuLieuTuDong();
    }

    private void initUI() {
        setTitle("Phiếu Lương Chi Tiết - Konami Enterprise");
        setSize(500, 600); // Tăng chiều cao để chứa nhiều thông tin hơn
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(null); 
        setLocationRelativeTo(null); 

        int xLabel = 30, xText = 150, wText = 300, h = 30, gap = 40;
        int y = 20;

        // 1. Họ tên (Chỉ xem, không sửa)
        add(createLabel("Họ tên NV:", xLabel, y));
        txtHoTen = new JTextField(hoTenNV);
        txtHoTen.setEditable(false); // Khóa lại
        txtHoTen.setBackground(Color.WHITE);
        txtHoTen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtHoTen.setBounds(xText, y, wText, h);
        add(txtHoTen);
        y += gap;

        // 2. Lương cứng (Chỉ xem)
        add(createLabel("Lương cứng:", xLabel, y));
        txtLuongCung = new JTextField(String.format("%,d", luongCoBan));
        txtLuongCung.setEditable(false);
        txtLuongCung.setBounds(xText, y, wText, h);
        add(txtLuongCung);
        y += gap;

        // 3. Giờ làm chuẩn (Mặc định 160)
        add(createLabel("Giờ chuẩn/tháng:", xLabel, y));
        txtGioLamChuan = new JTextField("160");
        txtGioLamChuan.setBounds(xText, y, wText, h);
        add(txtGioLamChuan);
        y += gap;

        // 4. Lương 1 giờ (Tự tính)
        add(createLabel("Lương 1 giờ:", xLabel, y));
        txtLuongMotGio = new JTextField();
        txtLuongMotGio.setEditable(false); // Tự tính nên khóa lại cho chuẩn
        txtLuongMotGio.setForeground(Color.BLUE);
        txtLuongMotGio.setBounds(xText, y, wText, h);
        add(txtLuongMotGio);
        y += gap;

        // 5. Giờ tăng ca (Nhập tay)
        add(createLabel("Giờ tăng ca:", xLabel, y));
        txtGioTangCa = new JTextField("0");
        txtGioTangCa.setBounds(xText, y, wText, h);
        add(txtGioTangCa);
        y += gap;

        // 6. Hệ số tăng ca
        add(createLabel("Hệ số tăng ca:", xLabel, y));
        txtHeSoTangCa = new JTextField("1.5");
        txtHeSoTangCa.setBounds(xText, y, wText, h);
        add(txtHeSoTangCa);
        y += gap;

        // 7. Thai sản
        chkNghiThaiSan = new JCheckBox("Đang nghỉ thai sản?");
        chkNghiThaiSan.setBounds(xText, y, wText, h);
        add(chkNghiThaiSan);
        y += gap + 10;

        // 8. Nút tính
        btnTinhLuong = new JButton("💵 TÍNH LƯƠNG & IN PHIẾU");
        btnTinhLuong.setFont(new Font("Dialog", Font.BOLD, 14)); 
        btnTinhLuong.setBackground(new Color(46, 204, 113)); // Màu xanh lá đẹp
        btnTinhLuong.setForeground(Color.WHITE);
        btnTinhLuong.setBounds(100, y, 300, 40);
        add(btnTinhLuong);
        y += 50;

        // 9. Kết quả
        txtKetQua = new JTextArea();
        txtKetQua.setEditable(false); 
        txtKetQua.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtKetQua.setBackground(new Color(245, 245, 245));           
        txtKetQua.setBorder(BorderFactory.createTitledBorder("Chi Tiết Phiếu Lương")); 
        
        JScrollPane sp = new JScrollPane(txtKetQua);
        sp.setBounds(30, y, 420, 180);
        add(sp);
    }
    
    // Hàm phụ trợ tạo Label nhanh
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 120, 30);
        return lbl;
    }

    private void dienDuLieuTuDong() {
        try {
            double gioChuan = Double.parseDouble(txtGioLamChuan.getText());
            // Công thức: Lương 1 giờ = Lương Cứng / Giờ Chuẩn
            double luong1Gio = (double) luongCoBan / gioChuan;
            txtLuongMotGio.setText(String.format("%.0f", luong1Gio));
        } catch (Exception e) {
            txtLuongMotGio.setText("0");
        }
    }

    private void initEvents() {
        btnTinhLuong.addActionListener(e -> xuLyTinhLuong());
        
        // Khi sửa giờ chuẩn thì tự tính lại lương 1 giờ
        txtGioLamChuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dienDuLieuTuDong();
            }
        });
    }

    private void xuLyTinhLuong() {
        try {
            double luong1Gio = Double.parseDouble(txtLuongMotGio.getText());
            double gioChuan  = Double.parseDouble(txtGioLamChuan.getText());
            double gioTangCa = Double.parseDouble(txtGioTangCa.getText());
            double heSo      = Double.parseDouble(txtHeSoTangCa.getText());
            boolean dangNghiThaiSan = chkNghiThaiSan.isSelected();

            double tongLuongGross = MayTinhLuong.tinhTongLuong(luong1Gio, gioChuan, gioTangCa, heSo, dangNghiThaiSan);

            int soNguoiPhuThuoc = 0; // Có thể mở rộng thêm ô nhập này sau
            double tienBaoHiem = MayTinhLuong.tinhBaoHiem(tongLuongGross);
            double tienThue = MayTinhLuong.tinhThueTNCN(tongLuongGross, soNguoiPhuThuoc);
            double thucLinh = tongLuongGross - tienBaoHiem - tienThue;
            
            // Format số tiền đẹp
            DecimalFormat df = new DecimalFormat("#,###");

            String chiTiet = String.format(
                " NHÂN VIÊN: %s\n" +
                "========================================\n" +
                " (+) Lương Cứng:      %15s VNĐ\n" +
                " (+) Lương Tăng Ca:   %15s VNĐ\n" +
                "----------------------------------------\n" +
                " TỔNG LƯƠNG (GROSS):  %15s VNĐ\n" +
                " (-) Bảo Hiểm (10.5%%):%15s VNĐ\n" +
                " (-) Thuế TNCN:       %15s VNĐ\n" +
                "========================================\n" +
                " THỰC LĨNH:           %15s VNĐ",
                hoTenNV.toUpperCase(),
                df.format(luongCoBan),
                df.format(tongLuongGross - luongCoBan), // Tạm tính phần chênh lệch là tăng ca (nếu ko nghỉ thai sản)
                df.format(tongLuongGross),
                df.format(tienBaoHiem),
                df.format(tienThue),
                df.format(thucLinh)
            );

            txtKetQua.setText(chiTiet);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi Nhập Liệu", JOptionPane.ERROR_MESSAGE);
        }
    }
}