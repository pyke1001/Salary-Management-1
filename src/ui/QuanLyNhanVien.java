package ui;
																						// Controller - Cả nhóm
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.NhanVienDAO;
import entity.NhanVien;
import logic.XuLyTangLuong;

public class QuanLyNhanVien extends NhanVienUI {

    private String lastMa = "";
    private String lastTen = "";
    private String lastPhong = "";
    private String lastLuong = "";
    private String taiKhoanHienTai;
    private String quyenHienTai;

    private NhanVienDAO dao = new NhanVienDAO();
    private static final long serialVersionUID = 2L;

    public QuanLyNhanVien(String username, String role) { 								// Hàm khởi tạo
        super();
        btnPhat.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        btnThuongNong.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        btnChotThang.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        this.taiKhoanHienTai = username;
        this.quyenHienTai = role;
        initEvents();
        phanQuyen();
    }

    private void phanQuyen() {															// Hàm 'Phân quyền' - Việt
        if (quyenHienTai.equalsIgnoreCase("Admin")) {
            btnQuanLyTK.addActionListener(_ -> hienThiDanhSachTaiKhoanAdmin());
            btnThuongNong.addActionListener(_ -> xuLyThuongNong());
            btnChotThang.addActionListener(_ -> xuLyChotThang());
            btnXuatExcel.addActionListener(_ -> xuLyXuatExcel()); 
            btnPhat.addActionListener(_ -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần phạt!", "Chưa chọn", javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String maNV = table.getValueAt(row, 0).toString();
                String hoTen = table.getValueAt(row, 1).toString();
                
                String input = javax.swing.JOptionPane.showInputDialog(this, 
                    "Nhập số ngày đi trễ của " + hoTen + ":\n(Ví dụ: 1, 2, 3...)", 
                    "Xử Lý Vi Phạm", 
                    javax.swing.JOptionPane.QUESTION_MESSAGE);
                    
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int soNgay = Integer.parseInt(input.trim());
                        if (soNgay < 0) {
                            javax.swing.JOptionPane.showMessageDialog(this, "Số ngày không được âm!");
                            return;
                        }
                        
                        // Gọi hàm cập nhật trong DAO (Cập nhật cột SoNgayDiTre và TienPhat)
                        // Giả sử mỗi ngày trễ phạt 100k (Hoặc logic cậu đã có)
                        dao.capNhatPhat(maNV, soNgay); 
                        
                        javax.swing.JOptionPane.showMessageDialog(this, "✅ Đã ghi nhận phạt cho: " + hoTen);
                        loadData("NV.MaNV ASC"); // Load lại bảng
                    } catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!");
                    }
                }
            });
            return;
        }

        if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
            setTitle("Hồ Sơ Cá Nhân - " + taiKhoanHienTai);

            table.getParent().getParent().setVisible(false);
            btnQuanLyTK.setVisible(false);
            btnThem.setVisible(false);
            btnSua.setVisible(false);
            btnXoa.setVisible(false);
            btnLamMoi.setVisible(false);
            
            btnPhat.setVisible(false);
            btnThuongNong.setVisible(false);
            btnChotThang.setVisible(false);
            btnXuatExcel.setVisible(false);
            
            btnTangLuong.setVisible(false);
            btnTimKiem.setVisible(false);
            btnLoad.setVisible(false);
            lblSort.setVisible(false);
            btnSortMa.setVisible(false);
            btnSortTen.setVisible(false);
            btnSortLuong.setVisible(false);
            btnThongKe.setVisible(false);

            JLabel lblAvatar = new JLabel();
            lblAvatar.setBounds(50, 50, 160, 160); 
            lblAvatar.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200), 5)); 
            lblAvatar.setOpaque(true); 
            lblAvatar.setBackground(Color.WHITE); 
            lblAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            
            try {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/icon/user.png"));
                java.awt.Image img = icon.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new javax.swing.ImageIcon(img));
            } catch (Exception ex) {
                lblAvatar.setText("NO IMAGE");
            }
            getContentPane().add(lblAvatar);

            lblMa.setVisible(true); txtMaNV.setVisible(true);
            lblTen.setVisible(true); txtHoTen.setVisible(true);
            lblPhong.setVisible(true); txtPhongBan.setVisible(true);
            lblLuong.setVisible(true); txtLuongCoBan.setVisible(true);
            lblHS.setVisible(true); txtHeSo.setVisible(true);

            Font fontLabel = new Font("Segoe UI", Font.PLAIN, 14);
            Font fontText = new Font("Segoe UI", Font.BOLD, 16);
            Color colorLabel = new Color(100, 100, 100);

            int labelX = 250; int textX = 350; int widthText = 350; int startY = 50; int gap = 55;

            lblMa.setBounds(labelX, startY, 100, 30); lblMa.setFont(fontLabel); lblMa.setForeground(colorLabel);
            txtMaNV.setBounds(textX, startY, widthText, 30); txtMaNV.setFont(fontText);

            lblTen.setBounds(labelX, startY + gap, 100, 30); lblTen.setFont(fontLabel); lblTen.setForeground(colorLabel);
            txtHoTen.setBounds(textX, startY + gap, widthText, 40); txtHoTen.setFont(fontText);

            lblPhong.setBounds(labelX, startY + gap * 2, 100, 30); lblPhong.setFont(fontLabel); lblPhong.setForeground(colorLabel);
            txtPhongBan.setBounds(textX, startY + gap * 2, widthText, 30); txtPhongBan.setFont(fontText);

            lblLuong.setBounds(labelX, startY + gap * 3, 100, 30); lblLuong.setFont(fontLabel); lblLuong.setForeground(colorLabel);
            txtLuongCoBan.setBounds(textX, startY + gap * 3, widthText, 30); txtLuongCoBan.setFont(fontText); txtLuongCoBan.setForeground(new Color(220, 53, 69));

            lblHS.setBounds(labelX, startY + gap * 4, 100, 30); lblHS.setFont(fontLabel); lblHS.setForeground(colorLabel);
            txtHeSo.setBounds(textX, startY + gap * 4, widthText, 30); txtHeSo.setFont(fontText);

            NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
            if (myProfile != null) {
                txtMaNV.setText(myProfile.getMaNV());
                txtHoTen.setText(myProfile.getHoTen());
                txtPhongBan.setText(myProfile.getTenPB() != null ? myProfile.getTenPB() : myProfile.getMaPB());
                txtLuongCoBan.setText(String.format("%,d", myProfile.getLuongCoBan()) + " VNĐ");
                txtHeSo.setText(String.valueOf(myProfile.getHeSoLuong()));
            }

            JTextField[] cacO = {txtMaNV, txtHoTen, txtPhongBan, txtLuongCoBan, txtHeSo};
            for (JTextField txt : cacO) {
                txt.setEditable(false); txt.setOpaque(false); txt.setFocusable(false);
                txt.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
            }

            JPanel pnlStats = new JPanel(new GridLayout(1, 3, 15, 0));
            pnlStats.setBounds(250, 330, 450, 70); pnlStats.setOpaque(false);
            
            String[] tieuDe = {"NGÀY ĐI TRỄ", "TIỀN THƯỞNG", "THÂM NIÊN"};
            String[] giaTri = {"0 ngày", "0 VNĐ", "Mới vào"};
            if(myProfile != null) {
                giaTri[0] = myProfile.getSoNgayDiTre() + " ngày";
                giaTri[1] = String.format("%,d", myProfile.getTienThuong());
                if (myProfile.getNgayVaoLam() != null) {
                    LocalDate start = new java.util.Date(myProfile.getNgayVaoLam().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    giaTri[2] = Period.between(start, LocalDate.now()).getYears() + " năm";
                }
            }
            Color[] bgColors = {new Color(255, 235, 238), new Color(232, 245, 233), new Color(227, 242, 253)};
            Color[] textColors = {new Color(198, 40, 40), new Color(46, 125, 50), new Color(21, 101, 192)};

            for (int i = 0; i < 3; i++) {
                JPanel pnlItem = new JPanel(new java.awt.BorderLayout());
                pnlItem.setBackground(bgColors[i]);
                pnlItem.setBorder(javax.swing.BorderFactory.createLineBorder(bgColors[i].darker(), 1));
                JLabel lblVal = new JLabel(giaTri[i], javax.swing.SwingConstants.CENTER);
                lblVal.setFont(new Font("Segoe UI", Font.BOLD, 15)); lblVal.setForeground(textColors[i]);
                JLabel lblTitle = new JLabel(tieuDe[i], javax.swing.SwingConstants.CENTER);
                lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10)); lblTitle.setForeground(Color.GRAY);
                lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 5, 0));
                pnlItem.add(lblVal, java.awt.BorderLayout.CENTER); pnlItem.add(lblTitle, java.awt.BorderLayout.SOUTH);
                pnlStats.add(pnlItem);
            }
            getContentPane().add(pnlStats);

            int btnY = 430;
            JButton btnDanhBa = new JButton("📖 Danh bạ");
            btnDanhBa.setBounds(250, btnY, 140, 40); 
            btnDanhBa.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnDanhBa.setBackground(Color.WHITE);
            getContentPane().add(btnDanhBa); btnDanhBa.setVisible(true);
            btnDanhBa.addActionListener(_ -> hienThiCuaSoDanhBa());

            btnMoTinhLuong.setText("💰 Phiếu Lương");
            btnMoTinhLuong.setBounds(405, btnY, 150, 40); 
            btnMoTinhLuong.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnMoTinhLuong.setBackground(Color.WHITE);
            btnMoTinhLuong.setVisible(true);

            JButton btnDoiMK = new JButton("🔒 Đổi Mật Khẩu");
            btnDoiMK.setBounds(570, btnY, 150, 40); 
            btnDoiMK.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnDoiMK.setBackground(Color.WHITE);
            getContentPane().add(btnDoiMK); btnDoiMK.setVisible(true);
            btnDoiMK.addActionListener(_ -> hienThiFormDoiMatKhau());
        }
    }


    
    private void initEvents() { 														// Hàm 'Xử lí sự kiện' - Cả nhóm

        btnSortMa.addActionListener(_ -> reloadTable("NV.MaNV ASC")); 					// Xử lí sự kiện: 'Sắp xếp Mã NV'
        btnSortTen.addActionListener(_ -> reloadTable("NV.HoTen ASC")); 				// Xử lí sự kiện: 'Sắp xếp Họ Tên' //Tóm gọn là Xử lí sự kiện: 'Sắp xếp' - Việt
        btnSortLuong.addActionListener(_ -> reloadTable("NV.LuongCoBan DESC")); 		// Xử lí sự kiện: 'Sắp xếp Lương'

        btnTimKiem.addActionListener(_ -> xuLyTimKiemDaNang()); 						// Xử lí sự kiện: 'Tìm kiếm' - Việt

        table.addMouseListener(new MouseAdapter() { 									// Xử lí sự kiện: 'Click - Chỉnh sửa' - Việt
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(table.getValueAt(row, 0).toString());
                    txtHoTen.setText(table.getValueAt(row, 1).toString());
                    txtPhongBan.setText(table.getValueAt(row, 2).toString());

                    String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "");
                    txtLuongCoBan.setText(luongStr);

                    // Cột 5 là Hệ số (Sau khi đã thêm cột Thâm niên vào vị trí 4)
                    txtHeSo.setText(table.getValueAt(row, 5).toString());
                    txtMaNV.setEditable(false);
                }
            }
        });

        btnThem.addActionListener(_ -> { 												// Xử lí sự kiện: 'Thêm' - Việt
            if (txtMaNV.getText().equals("") || txtHoTen.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            try {
                NhanVien nv = new NhanVien(
                    txtMaNV.getText(),
                    txtHoTen.getText(),
                    txtPhongBan.getText(),
                    Long.parseLong(txtLuongCoBan.getText()),
                    Float.parseFloat(txtHeSo.getText())
                );

                if (dao.themNhanVien(nv)) {
                    JOptionPane.showMessageDialog(null, "✅ Thêm thành công!");
                    loadData("NV.MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi: Mã nhân viên trùng hoặc sai định dạng số!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Lỗi nhập liệu!");
            }
        });

        btnSua.addActionListener(_ -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn sửa thông tin nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                // Lấy dữ liệu từ giao diện
                String maNV = txtMaNV.getText();
                String hoTen = txtHoTen.getText();
                
                // --- ĐOẠN XỬ LÝ THÔNG MINH ---
                String phongNhap = txtPhongBan.getText().trim();
                // Gọi hàm DAO để đổi tên phòng thành mã phòng
                String maPB = dao.chuyenTenPhongThanhMa(phongNhap); 
                // -----------------------------

                // Xử lý lương (bỏ dấu phẩy, bỏ chữ VNĐ)
                String luongStr = txtLuongCoBan.getText().replace(",", "").replace(" VNĐ", "").trim();
                long luongCoBan = Long.parseLong(luongStr);
                
                float heSo = Float.parseFloat(txtHeSo.getText());

                // Tạo đối tượng nhân viên mới
                NhanVien nv = new NhanVien(maNV, hoTen, maPB, luongCoBan, heSo);
                // Giữ nguyên các thông tin cũ (Ngày vào làm...) nếu cần thiết
                // Ở đây giả sử hàm update của cậu chỉ cần các thông tin cơ bản
                
                if (dao.suaNhanVien(nv)) {
                    JOptionPane.showMessageDialog(this, "✅ Sửa thành công!");
                    loadData("NV.MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Sửa thất bại! (Kiểm tra lại Mã Phòng Ban)");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        });

        btnXoa.addActionListener(_ -> { 												// Xử lí sự kiện: 'Xóa' - Việt
            if (txtMaNV.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            int hoi = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (hoi != JOptionPane.YES_OPTION) return;

            if (dao.xoaNhanVien(txtMaNV.getText())) {
                JOptionPane.showMessageDialog(null, "✅ Đã xóa thành công!");
                loadData("NV.MaNV ASC");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Lỗi: Không thể xóa!");
            }
        });

        btnLamMoi.addActionListener(_ -> { 												// Xử lí sự kiện: 'Làm mới' - Tùng
            resetForm();
            lastMa = "";
            lastTen = "";
            lastPhong = "";
            lastLuong = "";
            reloadTable("NV.MaNV ASC");
        });

        btnTangLuong.addActionListener(_ -> xuLyTangLuong()); 							// Xử lí sự kiện: 'Tăng lương' - Quốc

        btnGiamLuong.addActionListener(_ -> xuLyGiamLuong()); 							// Xử lí sự kiện: 'Giảm lương' - Việt

        btnBaoLoi.addActionListener(_ -> hienThiFormBaoLoi()); 							// Xử lí sự kiện: 'Báo lỗi' - Việt
        
        btnMoTinhLuong.addActionListener(_ -> {											// Xử lí sự kiện: 'Phiếu lương' - Đồng
            String hoTen = "";
            long luongCoBan = 0;
            
            if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
                hoTen = txtHoTen.getText();
                String luongStr = txtLuongCoBan.getText().replace(",", "").replace(" VNĐ", "").trim();
                try {
                    luongCoBan = Long.parseLong(luongStr);
                } catch (Exception ex) {
                    luongCoBan = 0;
                }
            } 
            
            else {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tính lương!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                hoTen = table.getValueAt(row, 1).toString();
                String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "").trim();
                luongCoBan = Long.parseLong(luongStr);
            }

            TinhLuongUI cuaSoTinhLuong = new TinhLuongUI(hoTen, luongCoBan);
            cuaSoTinhLuong.setVisible(true);
        });

        btnThongKe.addActionListener(_ -> { 											// Xử lí sự kiện: 'Thống Kê' - Hướng
        	ui.ThongKeUI thongKeForm = new ui.ThongKeUI(model);
            thongKeForm.setVisible(true);
        });

        setHienThi(false);

        btnLoad.addActionListener(_ -> { 												// Xử lí sự kiện: 'Tải danh sách' - Việt

            setHienThi(true);
            loadData("NV.MaNV ASC");
        });

    }

    private void resetForm() { 										// Hàm 'Lau bảng (Ô nhập liệu)' - Dùng trong Xử lí sự kiện: 'Thêm', 'Xóa', 'Làm mới' - Tùng
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhongBan.setText("");
        txtLuongCoBan.setText("");
        txtHeSo.setText("");
        txtMaNV.setEditable(true);
    }

    private void setHienThi(boolean hien) { 						// Hàm 'Hiển thị' - Dùng trong Xử lí sự kiện: 'Tải danh sách' - Việt
        lblMa.setVisible(hien);
        lblTen.setVisible(hien);
        lblPhong.setVisible(hien);
        lblLuong.setVisible(hien);
        lblHS.setVisible(hien);
        lblSort.setVisible(hien);

        txtMaNV.setVisible(hien);
        txtHoTen.setVisible(hien);
        txtPhongBan.setVisible(hien);
        txtLuongCoBan.setVisible(hien);
        txtHeSo.setVisible(hien);
        															// Vai trò: Công tắc ẩn/hiện tất cả các nút
        btnThem.setVisible(hien);
        btnSua.setVisible(hien);
        btnXoa.setVisible(hien);
        btnLamMoi.setVisible(hien);
        btnTangLuong.setVisible(hien);
        btnMoTinhLuong.setVisible(hien);
        btnThongKe.setVisible(hien);
        btnTimKiem.setVisible(hien);
        btnQuanLyTK.setVisible(hien);
        btnGiamLuong.setVisible(hien);
        btnBaoLoi.setVisible(hien);
        btnChotThang.setVisible(hien);
        btnXuatExcel.setVisible(hien);
        btnThuongNong.setVisible(hien);
        btnPhat.setVisible(hien);

        btnSortMa.setVisible(hien);
        btnSortTen.setVisible(hien);
        btnSortLuong.setVisible(hien);
    }

    private void xuLyTangLuong() {								 	// Hàm 'Click - Tăng lương' - Dùng trong Xử lí sự kiện: 'Tăng lương' - Quốc
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tăng lương!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = table.getValueAt(row, 0).toString();
        String hoTen = table.getValueAt(row, 1).toString();
        String luongCuStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "").trim();
        double luongCu = Double.parseDouble(luongCuStr);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Chọn hình thức tăng lương cho: " + hoTen));

        String[] options = { "KPI Loại A (Xuất sắc)", "KPI Loại B (Giỏi)", "KPI Loại C (Khá)", "Nhập tay %" };
        JComboBox < String > cboOption = new JComboBox < > (options);
        panel.add(cboOption);

        int result = JOptionPane.showConfirmDialog(this, panel, "Xét Duyệt Tăng Lương", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            double luongMoi = 0;
            double phanTram = 0;

            int selectedIndex = cboOption.getSelectedIndex();

            try {
                if (selectedIndex == 0) {
                    luongMoi = XuLyTangLuong.tinhLuongTheoKPI(luongCu, "A");
                } else if (selectedIndex == 1) {
                    luongMoi = XuLyTangLuong.tinhLuongTheoKPI(luongCu, "B");
                } else if (selectedIndex == 2) {
                    luongMoi = XuLyTangLuong.tinhLuongTheoKPI(luongCu, "C");
                } else {
                    String input = JOptionPane.showInputDialog(this, "Nhập % muốn tăng:", "5");

                    if (input == null || input.trim().isEmpty()) {
                        return;
                    }

                    try {
                        phanTram = Double.parseDouble(input);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
                        return;
                    }

                    luongMoi = luongCu * (1 + phanTram / 100);
                }

                if (selectedIndex <= 2) {
                    phanTram = ((luongMoi - luongCu) / luongCu) * 100;
                }

                String msg = String.format("Lương cũ: %,.0f VNĐ\nLương mới: %,.0f VNĐ\n(Tăng: %.1f%%)\n\nXác nhận cập nhật?",
                    luongCu, luongMoi, phanTram);

                int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác Nhận", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    NhanVienDAO dao = new NhanVienDAO();
                    dao.tangLuong(maNV, phanTram);

                    JOptionPane.showMessageDialog(this, "Đã tăng lương thành công!");
                    loadData("NV.MaNV ASC");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void fillTable(List < NhanVien > list) { 				// Hàm 'Vẽ bảng' - Dùng trong Hàm 'Lau bảng (Danh sách)' và Hàm 'Tiện ích' - Việt
        model.setRowCount(0);

        for (NhanVien nv: list) {
            java.util.Vector < Object > row = new java.util.Vector < > ();
            row.add(nv.getMaNV());
            row.add(nv.getHoTen());

            if (nv.getTenPB() != null) {
                row.add(nv.getTenPB());
            } else {
                row.add(nv.getMaPB());
            }

            row.add(String.format("%,d", nv.getLuongCoBan()));
            if (nv.getNgayVaoLam() != null) {
                LocalDate start = new java.util.Date(nv.getNgayVaoLam().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate now = LocalDate.now();
                int soNam = Period.between(start, now).getYears();
                row.add(soNam + " năm");
            } else {
                row.add("Mới vào");
            }
            row.add(nv.getHeSoLuong());
            row.add(String.format("%,d", nv.getTienThuong()));
            row.add(nv.getSoNgayDiTre() + " ngày");
            row.add(String.format("%,d", nv.getTienPhat()));
            row.add(String.format("%,d", nv.getThucLinh()));

            model.addRow(row);
        }
    }

	private void loadData(String orderBy) { 						// Hàm 'Lau bảng (Danh sách)' - Việt
	        String[] columns = { 				// Dùng trong Xử lí sự kiện: 'Click- Cập nhật Phạt', 'Thêm', 'Xóa', 'Sửa'
	            "Mã NV",
	            "Họ Tên",
            "Phòng Ban",
            "Lương Cứng",
            "Thâm Niên",
            "Hệ Số",
            "Thưởng",
            "Đi Trễ",
            "Tiền Phạt",
            "Thực Lĩnh"
        };
        model = new DefaultTableModel(columns, 0);
        table.setModel(model);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);

        List < NhanVien > list = dao.layDanhSachNhanVien(orderBy);
        fillTable(list);
    }

    private void reloadTable(String orderBy) { 						// Hàm 'Tiện ích'- Dùng trong Hàm 'Tìm thông tin' - Việt
        List < NhanVien > list = dao.timKiemDaNang(lastMa, lastTen, lastPhong, lastLuong, orderBy);

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu!");
            return;
        }

        if (orderBy.contains("HoTen")) {
            Collections.sort(list, new Comparator < NhanVien > () {
                @Override
                public int compare(NhanVien nv1, NhanVien nv2) {
                    String ten1 = getTen(nv1.getHoTen());
                    String ten2 = getTen(nv2.getHoTen());

                    Collator collator = Collator.getInstance(Locale.of("vi", "VN"));

                    int result = collator.compare(ten1, ten2);
                    if (result == 0) {
                        return collator.compare(nv1.getHoTen(), nv2.getHoTen());
                    }

                    return result;
                }
            });
        }

        fillTable(list);
    }

    private String getTen(String hoTen) { 							// Hàm 'Lấy chữ cái' - Dùng trong Hàm 'Tiện ích' - Việt
        if (hoTen == null || hoTen.trim().isEmpty()) return "";

        hoTen = hoTen.trim();

        String[] parts = hoTen.split("\\s+");

        return parts[parts.length - 1];
    }

    private void xuLyTimKiemDaNang() { 								// Hàm 'Tìm thông tin' - Dùng trong Xử lí sự kiện: 'Tìm Kiếm' - Việt
        lastMa = txtMaNV.getText().trim();
        lastTen = txtHoTen.getText().trim();
        lastPhong = txtPhongBan.getText().trim();
        lastLuong = txtLuongCoBan.getText().replace(",", "").replace(".", "").trim();

        if (lastMa.isEmpty() && lastTen.isEmpty() && lastPhong.isEmpty() && lastLuong.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ít nhất một thông tin để tìm kiếm!");
            return;
        }

        reloadTable("NV.MaNV ASC");
    }

    private void hienThiCuaSoDanhBa() { 							// Hàm 'Cửa sổ danh bạ'- Dùng trong Hàm 'Phân quyền' - Việt
        JDialog dialog = new JDialog(this, "Danh Bạ Nhân Viên", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblLoc = new JLabel("Lọc Phòng Ban:");
        lblLoc.setBounds(20, 20, 100, 30);
        dialog.getContentPane().add(lblLoc);

        JComboBox < String > cboPhong = new JComboBox < > ();
        cboPhong.setBounds(130, 20, 200, 30);
        cboPhong.addItem("Tất cả");
        for (String p: dao.layDanhSachPhongBan()) {
            cboPhong.addItem(p);
        }
        dialog.getContentPane().add(cboPhong);

        JLabel lblTim = new JLabel("🔍 Tìm nhanh:");
        lblTim.setBounds(20, 60, 100, 30);
        dialog.getContentPane().add(lblTim);

        JTextField txtTimDanhBa = new JTextField();
        txtTimDanhBa.setBounds(130, 60, 430, 30);
        txtTimDanhBa.setToolTipText("Nhập Tên hoặc Mã NV để tìm...");
        dialog.getContentPane().add(txtTimDanhBa);

        String[] cols = {
            "Mã NV",
            "Họ Tên",
            "Phòng Ban"
        };
        DefaultTableModel modelDanhBa = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tableDanhBa = new JTable(modelDanhBa);

        tableDanhBa.setFocusable(false);
        tableDanhBa.setRowSelectionAllowed(false);
        tableDanhBa.setColumnSelectionAllowed(false);
        tableDanhBa.setShowGrid(true);
        tableDanhBa.setGridColor(Color.LIGHT_GRAY);

        JScrollPane sp = new JScrollPane(tableDanhBa);
        sp.setBounds(20, 100, 540, 380);
        dialog.getContentPane().add(sp);

        Runnable napDuLieu = () -> {
            String phongDuocChon = cboPhong.getSelectedItem().toString();
            String tuKhoa = txtTimDanhBa.getText().trim();

            List < NhanVien > list = dao.timKiemDanhBa(phongDuocChon, tuKhoa);

            modelDanhBa.setRowCount(0);
            for (NhanVien nv: list) {
                modelDanhBa.addRow(new Object[] {
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getTenPB()
                });
            }
        };

        cboPhong.addActionListener(_ -> napDuLieu.run());

        txtTimDanhBa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                napDuLieu.run();
            }
        });

        napDuLieu.run();
        dialog.setVisible(true);
    }

    private void hienThiFormDoiMatKhau() { 							// Hàm 'Đổi mật khẩu' - Dùng trong Hàm 'Phân quyền' - Việt
        JDialog dialog = new JDialog(this, "Đổi Mật Khẩu", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblCu = new JLabel("Mật khẩu cũ:");
        lblCu.setBounds(30, 30, 100, 30);
        dialog.getContentPane().add(lblCu);
        JPasswordField txtPassCu = new JPasswordField();
        txtPassCu.setBounds(140, 30, 200, 30);
        dialog.getContentPane().add(txtPassCu);

        JLabel lblMoi = new JLabel("Mật khẩu mới:");
        lblMoi.setBounds(30, 80, 100, 30);
        dialog.getContentPane().add(lblMoi);
        JPasswordField txtPassMoi = new JPasswordField();
        txtPassMoi.setBounds(140, 80, 200, 30);
        dialog.getContentPane().add(txtPassMoi);

        JLabel lblXacNhan = new JLabel("Nhập lại MK:");
        lblXacNhan.setBounds(30, 130, 100, 30);
        dialog.getContentPane().add(lblXacNhan);
        JPasswordField txtPassXacNhan = new JPasswordField();
        txtPassXacNhan.setBounds(140, 130, 200, 30);
        dialog.getContentPane().add(txtPassXacNhan);

        JButton btnLuu = new JButton("💾 Lưu Thay Đổi");
        btnLuu.setBounds(100, 190, 180, 40);
        btnLuu.setBackground(Color.GREEN);
        dialog.getContentPane().add(btnLuu);

        btnLuu.addActionListener(_ -> { 							// Xử lí sự kiện: 'Lưu' 						
            String cu = new String(txtPassCu.getPassword());
            String moi = new String(txtPassMoi.getPassword());
            String xacNhan = new String(txtPassXacNhan.getPassword());

            if (cu.isEmpty() || moi.isEmpty() || xacNhan.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            if (!moi.equals(xacNhan)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không trùng khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dao.doiMatKhau(taiKhoanHienTai, cu, moi)) {
                JOptionPane.showMessageDialog(dialog, "✅ Đổi mật khẩu thành công!");
                dialog.dispose(); // Tắt cửa sổ
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void hienThiDanhSachTaiKhoanAdmin() { 					// Hàm 'Danh sách tài khoản - Admin' - Dùng trong Hàm 'Phân quyền' - Việt
        JDialog dialog = new JDialog(this, "Danh Sách Tài Khoản & Mật Khẩu", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("BẢNG THEO DÕI TÀI KHOẢN NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.RED);
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setBounds(0, 10, 780, 30);
        dialog.getContentPane().add(lblTitle);

        JLabel lblTim = new JLabel("🔍 Tìm nhanh:");
        lblTim.setBounds(30, 50, 100, 30);
        lblTim.setFont(new Font("Dialog", Font.BOLD, 12));
        dialog.getContentPane().add(lblTim);

        JTextField txtTimKiem = new JTextField();
        txtTimKiem.setBounds(120, 50, 630, 30);
        txtTimKiem.setToolTipText("Nhập Mã NV, Tên hoặc Tài khoản để tìm...");
        dialog.getContentPane().add(txtTimKiem);

        String[] cols = {
            "Mã NV",
            "Họ Tên",
            "Phòng Ban",
            "Tài Khoản",
            "Mật Khẩu"
        };

        DefaultTableModel modelTK = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable tableTK = new JTable(modelTK);
        tableTK.setRowHeight(25);
        tableTK.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTK.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTK.getColumnModel().getColumn(1).setPreferredWidth(150);

        tableTK.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JScrollPane sp = new JScrollPane(tableTK);
        sp.setBounds(30, 90, 720, 350);
        dialog.getContentPane().add(sp);

        List < String[] > listGoc = dao.layDanhSachTaiKhoan();

        modelTK.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 4 && row >= 0) {
                    String passMoi = modelTK.getValueAt(row, column).toString();
                    String username = modelTK.getValueAt(row, 3).toString();
                    String maNV = modelTK.getValueAt(row, 0).toString();

                    if (dao.capNhatMatKhau(username, passMoi)) {
                        for (String[] item: listGoc) {
                            if (item[0].equals(maNV)) {
                                item[4] = passMoi;
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        Runnable boLocDuLieu = () -> {
            String tuKhoa = txtTimKiem.getText().toLowerCase().trim();
            modelTK.setRowCount(0);

            for (String[] row: listGoc) {
                if (row[0].toLowerCase().contains(tuKhoa) ||
                    row[1].toLowerCase().contains(tuKhoa) ||
                    row[3].toLowerCase().contains(tuKhoa)) {

                    modelTK.addRow(row);
                }
            }
        };

        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                boLocDuLieu.run();
            }
        });

        boLocDuLieu.run();
        dialog.setVisible(true);
    }
    
    private void xuLyGiamLuong() {									// Hàm 'Giảm lương' - Dùng trong Xử lí sự kiện: 'Giảm lương' - Việt
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần giảm lương!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = table.getValueAt(row, 0).toString();
        String hoTen = table.getValueAt(row, 1).toString();
        String luongCuStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "").trim();
        double luongCu = Double.parseDouble(luongCuStr);

        String input = JOptionPane.showInputDialog(this, "Nhập % muốn giảm cho " + hoTen + ":", "10");

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        double phanTram = 0;
        try {
            phanTram = Double.parseDouble(input);
            if (phanTram <= 0 || phanTram >= 100) {
                JOptionPane.showMessageDialog(this, "Phần trăm giảm phải lớn hơn 0 và nhỏ hơn 100!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
            return;
        }

        double luongMoi = luongCu * (1 - phanTram / 100);

        String msg = String.format("CẢNH BÁO GIẢM LƯƠNG\n\nNhân viên: %s\nLương cũ: %,.0f VNĐ\nLương mới: %,.0f VNĐ\n(Giảm: %.1f%%)\n\nXác nhận thực hiện?",
            hoTen, luongCu, luongMoi, phanTram);

        int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác Nhận Giảm Lương", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                NhanVienDAO dao = new NhanVienDAO();
                dao.tangLuong(maNV, -phanTram);

                JOptionPane.showMessageDialog(this, "Đã giảm lương thành công!");

                loadData("NV.MaNV ASC");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void hienThiFormBaoLoi() {								// Hàm 'Giao diện Báo lỗi' - Việt
        JDialog dialog = new JDialog(this, "Gửi Báo Cáo Lỗi", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblTieuDe = new JLabel("Tiêu đề lỗi:");
        lblTieuDe.setBounds(20, 20, 100, 30);
        dialog.getContentPane().add(lblTieuDe);

        JTextField txtTieuDe = new JTextField();
        txtTieuDe.setBounds(120, 20, 240, 30);
        dialog.getContentPane().add(txtTieuDe);

        JLabel lblNoiDung = new JLabel("Mô tả chi tiết:");
        lblNoiDung.setBounds(20, 60, 100, 30);
        dialog.getContentPane().add(lblNoiDung);

        JTextArea txtNoiDung = new JTextArea();
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(txtNoiDung);
        sp.setBounds(20, 90, 340, 100);
        dialog.getContentPane().add(sp);

        JButton btnGui = new JButton("Gửi báo cáo");
        btnGui.setBounds(130, 210, 120, 30);

        btnGui.addActionListener(_ -> {
            String tieuDe = txtTieuDe.getText().trim();
            String noiDung = txtNoiDung.getText().trim();

            if (tieuDe.isEmpty() || noiDung.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            NhanVienDAO dao = new NhanVienDAO();
            dao.guiBaoLoi(tieuDe, noiDung);

            new Thread(() -> {
                logic.DiscordWebhook.guiThongBao(tieuDe, noiDung);
            }).start();

            JOptionPane.showMessageDialog(dialog, "Cảm ơn! Báo cáo của bạn đã được gửi.");
            dialog.dispose();
        });

        dialog.getContentPane().add(btnGui); 
        dialog.setVisible(true);
    }
    
    private void xuLyThuongNong() {									// Hàm 'Xử lí thưởng nóng' - Việt
        String[] options = {"Toàn Công Ty", "Theo Phòng Ban", "Hủy"};
        int choice = JOptionPane.showOptionDialog(this, 
                "Bạn muốn thưởng nóng cho đối tượng nào?", 
                "Chọn Chế Độ Thưởng", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, options, options[0]);

        if (choice == 2 || choice == -1) return;

        String moneyStr = JOptionPane.showInputDialog(this, "Nhập số tiền thưởng (VNĐ):", "500000");
        if (moneyStr == null || moneyStr.trim().isEmpty()) return;

        long tienThuong = 0;
        try {
            tienThuong = Long.parseLong(moneyStr.replace(",", "").replace(".", ""));
            if (tienThuong <= 0) {
                JOptionPane.showMessageDialog(this, "Tiền thưởng phải lớn hơn 0!");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Nhập tiền sai định dạng!");
            return;
        }

        if (choice == 0) {
            if (dao.congTienThuong(tienThuong)) {
                JOptionPane.showMessageDialog(this, "✅ Đã thưởng " + String.format("%,d", tienThuong) + " VNĐ cho TOÀN CÔNG TY!");
                loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi kết nối!");
            }
        } 
        else if (choice == 1) {
            java.util.Set<String> danhSachPhong = new java.util.HashSet<>();
            for (int i = 0; i < table.getRowCount(); i++) {
                danhSachPhong.add(table.getValueAt(i, 2).toString());
            }

            if (danhSachPhong.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Danh sách trống, không tìm thấy phòng ban nào!");
                return;
            }

            String[] cacPhong = danhSachPhong.toArray(new String[0]);
            
            String phongDuocChon = (String) JOptionPane.showInputDialog(this, 
                    "Chọn phòng ban cần thưởng:", 
                    "Danh Sách Phòng", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    cacPhong, 
                    cacPhong[0]);

            if (phongDuocChon != null) {
                if (dao.congTienThuongTheoPhong(phongDuocChon, tienThuong)) {
                    JOptionPane.showMessageDialog(this, "✅ Đã thưởng " + String.format("%,d", tienThuong) + " VNĐ cho " + phongDuocChon.toUpperCase() + "!");
                    loadData("NV.MaNV ASC");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Lỗi: Không cập nhật được (Có thể sai tên phòng trong DB)!");
                }
            }
        }
    }

    private void xuLyChotThang() {									// Hàm 'Chốt tháng' - Việt
        int confirm = JOptionPane.showConfirmDialog(this, 
            "BẠN CÓ CHẮC MUỐN CHỐT SỔ THÁNG NÀY?\n\nHành động này sẽ:\n- Xóa hết số ngày đi trễ.\n- Xóa hết tiền phạt.\n- Xóa hết tiền thưởng.\n\nĐể bắt đầu tính lương cho tháng mới.", 
            "Cảnh báo Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.resetThangMoi()) {
                JOptionPane.showMessageDialog(this, "✅ Đã reset dữ liệu cho tháng mới!");
                loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi hệ thống!");
            }
        }
    }

    private void xuLyXuatExcel() {									// Hàm 'Xuất Excel' - Việt
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel File (*.csv)", "csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
                bw.write("\uFEFF"); 
                
                for (int i = 0; i < table.getColumnCount(); i++) {
                    bw.write(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
                
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        String val = table.getValueAt(i, j).toString();
                        val = val.replace(",", ""); 
                        bw.write(val);
                        if (j < table.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
                
                JOptionPane.showMessageDialog(this, "✅ Xuất file Excel thành công!\n" + filePath);
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }
}