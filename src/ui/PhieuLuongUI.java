package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;
import logic.MayTinhLuong;

public class PhieuLuongUI extends JFrame {
    
    private static final long serialVersionUID = 2L;

    private JTextField txtHoTen, txtLuongCung, txtLuongMotGio, txtGioLamChuan;
    private JCheckBox chkNghiThaiSan;
    private JTextArea txtKetQua;
    private JButton btnTinhLuong;
    private JSpinner spnOT15, spnOT20, spnOT30;
    
    private String hoTenNV;
    private long luongCoBan;
    private String gioiTinh;

    public PhieuLuongUI() {
        this("Nhân viên vãng lai", 0, "Nam");
    }

    public PhieuLuongUI(String hoTen, long luongCoBan, String gioiTinh) {
        this.hoTenNV = hoTen;
        this.luongCoBan = luongCoBan;
        this.gioiTinh = gioiTinh;
        initUI();
        initEvents();
        dienDuLieuTuDong();
        
        xyLyCheDoThaiSan();
    }
    
    private void xyLyCheDoThaiSan() {
        if (gioiTinh != null && gioiTinh.equalsIgnoreCase("Nam")) {
            chkNghiThaiSan.setSelected(false);
            chkNghiThaiSan.setVisible(false); 
        } else {
            chkNghiThaiSan.setVisible(true);
        }
    }

    private void initUI() {
        setTitle("Phiếu Lương Chi Tiết - Konami Enterprise");
        setSize(500, 750); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(null); 
        setLocationRelativeTo(null); 

        int xLabel = 30, xText = 150, wText = 300, h = 30, gap = 40;
        int y = 20;

        add(createLabel("Họ tên NV:", xLabel, y));
        txtHoTen = new JTextField(hoTenNV);
        txtHoTen.setEditable(false); 
        txtHoTen.setFocusable(false);
        txtHoTen.setBackground(new Color(240, 240, 240)); 
        txtHoTen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtHoTen.setBounds(xText, y, wText, h);
        add(txtHoTen);
        y += gap;

        add(createLabel("Lương cứng:", xLabel, y));
        txtLuongCung = new JTextField(String.format("%,d", luongCoBan));
        txtLuongCung.setEditable(false);
        txtLuongCung.setFocusable(false);
        txtLuongCung.setBackground(new Color(240, 240, 240));
        txtLuongCung.setBounds(xText, y, wText, h);
        add(txtLuongCung);
        y += gap;

        add(createLabel("Giờ chuẩn/tháng:", xLabel, y));
        txtGioLamChuan = new JTextField("160");
        txtGioLamChuan.setEditable(false); 
        txtGioLamChuan.setFocusable(false);
        txtGioLamChuan.setBackground(new Color(240, 240, 240)); 
        txtGioLamChuan.setBounds(xText, y, wText, h);
        add(txtGioLamChuan);
        y += gap;

        add(createLabel("Lương 1 giờ:", xLabel, y));
        txtLuongMotGio = new JTextField();
        txtLuongMotGio.setEditable(false);
        txtLuongMotGio.setFocusable(false);
        txtLuongMotGio.setForeground(Color.BLUE);
        txtLuongMotGio.setBackground(new Color(240, 240, 240));
        txtLuongMotGio.setBounds(xText, y, wText, h);
        add(txtLuongMotGio);
        y += gap + 10;

        JPanel pnlTangCa = new JPanel();
        pnlTangCa.setLayout(new GridLayout(3, 2, 10, 10)); 
        pnlTangCa.setBounds(xLabel, y, 420, 130);
        
        pnlTangCa.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLUE), 
            "Chi Tiết Tăng Ca (Giờ)", 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("Arial", Font.BOLD, 14), 
            Color.BLUE
        ));
        pnlTangCa.setBackground(Color.WHITE); 

        JLabel lblOT15 = new JLabel("Ngày thường (150%):");
        lblOT15.setFont(new Font("Arial", Font.PLAIN, 14));
        spnOT15 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5)); 
        spnOT15.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblOT20 = new JLabel("Cuối tuần (200%):");
        lblOT20.setFont(new Font("Arial", Font.PLAIN, 14));
        spnOT20 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));
        spnOT20.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblOT30 = new JLabel("Lễ / Tết (300%):");
        lblOT30.setFont(new Font("Arial", Font.BOLD, 14));
        lblOT30.setForeground(Color.RED); 
        spnOT30 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5));
        spnOT30.setFont(new Font("Arial", Font.BOLD, 14));

        pnlTangCa.add(lblOT15); pnlTangCa.add(spnOT15);
        pnlTangCa.add(lblOT20); pnlTangCa.add(spnOT20);
        pnlTangCa.add(lblOT30); pnlTangCa.add(spnOT30);

        add(pnlTangCa);
        y += 150;

        chkNghiThaiSan = new JCheckBox("Đang nghỉ thai sản (Bảo hiểm chi trả)?");
        chkNghiThaiSan.setFont(new Font("Arial", Font.ITALIC, 13));
        chkNghiThaiSan.setForeground(new Color(200, 0, 0));
        chkNghiThaiSan.setBounds(xText, y, wText, h);
        add(chkNghiThaiSan);
        y += gap + 10;

        btnTinhLuong = new JButton("💵 TÍNH LƯƠNG & IN PHIẾU");
        btnTinhLuong.setFont(new Font("Dialog", Font.BOLD, 14)); 
        btnTinhLuong.setBackground(new Color(46, 204, 113)); 
        btnTinhLuong.setForeground(Color.WHITE);
        btnTinhLuong.setBounds(100, y, 300, 40);
        add(btnTinhLuong);
        y += 50;

        txtKetQua = new JTextArea();
        txtKetQua.setEditable(false); 
        txtKetQua.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtKetQua.setBackground(new Color(245, 245, 245));             
        txtKetQua.setBorder(BorderFactory.createTitledBorder("Chi Tiết Phiếu Lương")); 
        
        JScrollPane sp = new JScrollPane(txtKetQua);
        sp.setBounds(30, y, 420, 200);
        add(sp);
    }
    
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 120, 30);
        return lbl;
    }

    private void dienDuLieuTuDong() {
        try {
            double gioChuan = Double.parseDouble(txtGioLamChuan.getText());
            double luong1Gio = (double) luongCoBan / gioChuan;
            txtLuongMotGio.setText(String.format("%.0f", luong1Gio));
        } catch (Exception e) {
            txtLuongMotGio.setText("0");
        }
    }

    private void initEvents() {
        btnTinhLuong.addActionListener(e -> xuLyTinhLuong());
    }

    private void xuLyTinhLuong() {
        try {
            double luong1Gio = Double.parseDouble(txtLuongMotGio.getText());
            
            double gio15 = (double) spnOT15.getValue();
            double gio20 = (double) spnOT20.getValue();
            double gio30 = (double) spnOT30.getValue();
            
            boolean dangNghiThaiSan = chkNghiThaiSan.isVisible() && chkNghiThaiSan.isSelected();

            long tienOT15 = (long) (luong1Gio * gio15 * 1.5);
            long tienOT20 = (long) (luong1Gio * gio20 * 2.0);
            long tienOT30 = (long) (luong1Gio * gio30 * 3.0);
            long tongTienOT = tienOT15 + tienOT20 + tienOT30;

            double tongLuongGross = luongCoBan + tongTienOT;
            
            if (dangNghiThaiSan) {
                tongLuongGross = 0; 
            }

            int soNguoiPhuThuoc = 0; 
            double tienBaoHiem = MayTinhLuong.tinhBaoHiem(tongLuongGross);
            double tienThue = MayTinhLuong.tinhThueTNCN(tongLuongGross, soNguoiPhuThuoc);
            double thucLinh = tongLuongGross - tienBaoHiem - tienThue;
            
            DecimalFormat df = new DecimalFormat("#,###");

            String chiTietOT = "";
            if (tongTienOT > 0) {
                chiTietOT = "--- CHI TIẾT TĂNG CA ---\n";
                if (gio15 > 0) chiTietOT += String.format(" > Ngày thường (%.1fh): %11s\n", gio15, df.format(tienOT15));
                if (gio20 > 0) chiTietOT += String.format(" > Cuối tuần    (%.1fh): %11s\n", gio20, df.format(tienOT20));
                if (gio30 > 0) chiTietOT += String.format(" > Lễ/Tết       (%.1fh): %11s\n", gio30, df.format(tienOT30));
                chiTietOT += "----------------------------------------\n";
            }
            
            String trangThaiThaiSan = "";
            if (dangNghiThaiSan) {
                trangThaiThaiSan = "\n(ĐANG NGHỈ THAI SẢN - LƯƠNG DO BHXH CHI TRẢ)\n";
            }

            String chiTiet = String.format(
                " NHÂN VIÊN: %s (%s)\n" +
                "========================================\n" +
                " (+) Lương Cứng:        %15s VNĐ\n" +
                " (+) Tổng Tiền Tăng Ca:%15s VNĐ\n" +
                "%s" +
                "%s" + 
                " TỔNG LƯƠNG (GROSS):    %15s VNĐ\n" +
                " (-) Bảo Hiểm (10.5%%):%15s VNĐ\n" +
                " (-) Thuế TNCN:         %15s VNĐ\n" +
                "========================================\n" +
                " THỰC LĨNH:             %15s VNĐ",
                hoTenNV.toUpperCase(), gioiTinh,
                df.format(luongCoBan),
                df.format(tongTienOT),
                chiTietOT,
                trangThaiThaiSan,
                df.format(tongLuongGross),
                df.format(tienBaoHiem),
                df.format(tienThue),
                df.format(thucLinh)
            );

            txtKetQua.setText(chiTiet);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra dữ liệu đầu vào!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}