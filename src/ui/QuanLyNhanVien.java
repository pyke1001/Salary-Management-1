package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import dao.NhanVienDAO;
import entity.NhanVien;


public class QuanLyNhanVien extends AdminUI {
	
	Secret ui = new Secret(this);    
    NhanVienUI nvUI = new NhanVienUI(this);
    HienThiCuaSo view = new HienThiCuaSo(this);
    XuLySuKien solve = new XuLySuKien(this);
    NhanVienDAO dao = new NhanVienDAO();
    
    private static final long serialVersionUID = 2L;

    public QuanLyNhanVien(String username, String role) {
    	
        super();
        this.taiKhoanHienTai = username;
        this.quyenHienTai = role;
        
        napDuLieuPhongBan();
        cboHeSo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Tất cả hệ số", "Dưới 2.0", "2.0 - 3.0", "Trên 3.0"
        }));
        
        xuLyNutBam();
        phanQuyen();
        ui.hienThiGoiYCheat();
    }
    
    private void napDuLieuPhongBan() {
        cboPhongBan.removeAllItems();
        cboPhongBan.addItem("Tất cả Phòng Ban"); 
        List<String> pbList = dao.layDanhSachPhongBan();
        for (String pb : pbList) {
            cboPhongBan.addItem(pb);
        }
    }

    private void phanQuyen() {
        if (quyenHienTai.equalsIgnoreCase("Admin")) {
            btnQuanLyTK.addActionListener(e -> view.hienThiDanhSachTaiKhoanAdmin());
         // File: ui/QuanLyNhanVien.java - Trong hàm xuLyNutBam()

            btnThuongNong.addActionListener(e -> {
                // 1. Tạo 3 lựa chọn
                Object[] options = {"🏢 Toàn Công Ty", "🏢 Theo Phòng Ban", "👤 Cá Nhân (Đang chọn)", "Hủy bỏ"};
                
                int choice = JOptionPane.showOptionDialog(this,
                    "Chọn phạm vi áp dụng thưởng nóng:",
                    "Hệ Thống Thưởng & Phúc Lợi",
                    JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

                // 2. Xử lý từng trường hợp
                if (choice == 0) {
                    // --- TOÀN CÔNG TY ---
                    // (Cậu giữ nguyên code cũ của cậu ở đây, hoặc gọi hàm xử lý tương ứng)
                     solve.xuLyThuongToanCongTy(); 

                } else if (choice == 1) {
                    // --- THEO PHÒNG BAN ---
                    // (Cậu giữ nguyên code cũ của cậu ở đây)
                     solve.xuLyThuongPhongBan();

                } else if (choice == 2) {
                    // --- [LILITH ADD] CÁ NHÂN ---
                    int row = table.getSelectedRow();
                    if (row < 0) {
                        JOptionPane.showMessageDialog(this, 
                            "Vui lòng chọn nhân viên cần thưởng trong bảng trước!", 
                            "Chưa chọn người", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    String maNV = table.getValueAt(row, 0).toString();
                    String tenNV = table.getValueAt(row, 1).toString(); // Lấy tên để hiện lên popup cho chắc chắn

                    // Hiện hộp thoại nhập tiền
                    String sTien = JOptionPane.showInputDialog(this, 
                        "Nhập số tiền thưởng nóng cho: " + tenNV + "\n(Ví dụ: 500000)", 
                        "Thưởng Nóng Cá Nhân", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    if (sTien != null && !sTien.trim().isEmpty()) {
                        solve.xuLyThuongCaNhan(maNV, tenNV, sTien);
                    }
                }
            });
            btnChotThang.addActionListener(e -> solve.chotSoVaLuuTruThangNay());
            btnXuatExcel.addActionListener(e -> solve.xuLyXuatExcel()); 
            return;
        }

        if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
            getContentPane().removeAll();
            getContentPane().setLayout(new BorderLayout());

            NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
            String title = (myProfile != null) ? myProfile.getHoTen() : taiKhoanHienTai;
            setTitle("Hồ Sơ Cá Nhân - " + title);

            JTabbedPane tabPane = new JTabbedPane();
            
            tabPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
            
            tabPane.addTab("   Thông Tin Chung   ", null, nvUI.createTabProfile(myProfile));
            tabPane.addTab("   Thu Nhập & Phúc Lợi   ", null, nvUI.createTabIncome(myProfile));
            tabPane.addTab("   Lịch Sử & Vi Phạm   ", null, nvUI.createTabHistory(myProfile));
            tabPane.addTab("   Hộp Thư ", null, nvUI.createTabMailbox(myProfile));
            getContentPane().add(tabPane, BorderLayout.CENTER);
            
            JPanel pnlBot = new JPanel();
            
            JButton btnDoiMK = new JButton("Đổi Mật Khẩu");
            btnDoiMK.addActionListener(e -> view.hienThiFormDoiMatKhau());
            pnlBot.add(btnDoiMK);
            
            JButton btnLogOut = new JButton("Đăng Xuất");
            btnLogOut.addActionListener(e -> {
                dispose();
                new DangNhapUI().setVisible(true);
            });
            pnlBot.add(btnLogOut);
            
            getContentPane().add(pnlBot, BorderLayout.SOUTH);

            getContentPane().revalidate();
            getContentPane().repaint();
        }
    }
    
    private void xuLyNutBam() {
    	btnSortMa.addActionListener(e -> {
            if (isSortMaAsc) {
                reloadTable("NV.MaNV ASC");      // Tăng dần
                btnSortMa.setText("Mã NV ▼");    // Đổi icon mũi tên lên
                isSortMaAsc = false;             // Lần sau bấm sẽ là Giảm
            } else {
                reloadTable("NV.MaNV DESC");     // Giảm dần
                btnSortMa.setText("Mã NV ▲");    // Đổi icon mũi tên xuống
                isSortMaAsc = true;              // Lần sau bấm sẽ là Tăng
            }
        });
    	btnSortTen.addActionListener(e -> {
            if (isSortTenAsc) {
                reloadTable("NV.HoTen ASC");
                btnSortTen.setText("Họ Tên ▼A");
                isSortTenAsc = false;
            } else {
                reloadTable("NV.HoTen DESC");
                btnSortTen.setText("Họ Tên ▼Z");
                isSortTenAsc = true;
            }
        });
    	btnSortLuong.addActionListener(e -> {
            if (isSortLuongAsc) {
                reloadTable("NV.HeSoLuong ASC"); // Hệ số nhỏ xếp trước
                btnSortLuong.setText("Lương ▼L");
                isSortLuongAsc = false;
            } else {
                reloadTable("NV.HeSoLuong DESC"); // Hệ số to (Sếp) xếp trước
                btnSortLuong.setText("Lương ▼H");
                isSortLuongAsc = true;
            }
        }); 

        btnTimKiem.addActionListener(e -> solve.xuLyTimKiemDaNang()); 

		table.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int row = table.getSelectedRow();
		        if (row >= 0) { 
		            // 1. Lấy dữ liệu an toàn (Chống lỗi Null)
		            String ma = java.util.Objects.toString(table.getValueAt(row, 0), "");
		            String ten = java.util.Objects.toString(table.getValueAt(row, 1), "");
		            String phong = java.util.Objects.toString(table.getValueAt(row, 2), "");
		            
		            // Lấy lương và làm sạch dấu phẩy (1,000,000 -> 1000000)
		            String luong = java.util.Objects.toString(table.getValueAt(row, 3), "0")
		                           .replace(",", "").replace(".", "").replace(" VNĐ", "").trim();
		            
		            String heSo = java.util.Objects.toString(table.getValueAt(row, 4), "0");
		
		            // 2. Đổ dữ liệu lên giao diện (DÙNG ĐÚNG TÊN BIẾN TRONG ADMINUI)
		            txtMaNV.setText(ma);
		            txtHoTen.setText(ten);
		            
		            // Xử lý ComboBox Phòng ban
		            if (!phong.isEmpty()) {
		                cboPhongBan.setSelectedItem(phong);
		            }
		            
		            // [SỬA LẠI TÊN BIẾN Ở ĐÂY]
		            txtLuongCoBan.setText(luong); // Tên đúng là txtLuongCoBan
		            
		            // Xử lý ComboBox Hệ số (Vì là ComboBox nên dùng setSelectedItem)
		            cboHeSo.setSelectedItem(heSo); // Tên đúng là cboHeSo
		            
		            // 3. Lưu lại vết để dùng cho nút Sửa/Xóa sau này
		            lastMa = ma;
		            lastTen = ten;
		            lastPhong = phong;
		            lastLuong = luong;
		            lastHeSo = heSo;
		        }
		    }
		});

        btnThem.addActionListener(e -> { 
            if (txtMaNV.getText().equals("") || txtHoTen.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            try {
            	long luong = 2340000;
                float heSo = layGiaTriFloatTuCbo(cboHeSo);
                String phong = cboPhongBan.getSelectedItem() != null ? cboPhongBan.getSelectedItem().toString() : "";
                
                String maPB = dao.chuyenTenPhongThanhMa(phong);
                if (maPB == null || maPB.isEmpty()) maPB = phong; 

                NhanVien nv = new NhanVien(txtMaNV.getText(), txtHoTen.getText(), maPB, luong, heSo);

                if (dao.themNhanVien(nv)) {
                    JOptionPane.showMessageDialog(null, "✅ Thêm thành công!");
                    loadData("NV.MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi: Mã NV trùng hoặc sai định dạng!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Lỗi nhập liệu: " + ex.getMessage());
            }
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String maNV = txtMaNV.getText();
            NhanVien nvCu = dao.getNhanVienTheoMa(maNV);
            if (nvCu == null) return; 

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn sửa thông tin nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                String hoTenMoi = txtHoTen.getText();
                String phongMoi = cboPhongBan.getSelectedItem().toString(); 
                String maPBMoi = dao.chuyenTenPhongThanhMa(phongMoi);
                if (maPBMoi == null || maPBMoi.isEmpty()) maPBMoi = phongMoi;

                long luongMoi = 2340000;
                float heSoMoi = layGiaTriFloatTuCbo(cboHeSo);

                NhanVien nvMoi = new NhanVien(maNV, hoTenMoi, maPBMoi, luongMoi, heSoMoi);
                
                if (dao.suaNhanVien(nvMoi)) {
                    StringBuilder sb = new StringBuilder("<html>");
                    boolean coThayDoi = false;

                    if (nvCu.getLuongCoBan() != luongMoi) {
                        sb.append(String.format("- Lương: %,d -> <font color='red'><b>%,d</b></font><br>", nvCu.getLuongCoBan(), luongMoi));
                        coThayDoi = true;
                    }
                    if (Float.compare(nvCu.getHeSoLuong(), heSoMoi) != 0) {
                        sb.append(String.format("- Hệ số: %s -> <font color='blue'><b>%s</b></font><br>", nvCu.getHeSoLuong(), heSoMoi));
                        coThayDoi = true;
                    }
                    String tenPBCu = nvCu.getTenPB() != null ? nvCu.getTenPB() : nvCu.getMaPB();
                    if (!tenPBCu.equals(phongMoi)) {
                        sb.append(String.format("- Phòng: %s -> <b>%s</b><br>", tenPBCu, phongMoi));
                        coThayDoi = true;
                    }
                    if (!nvCu.getHoTen().equals(hoTenMoi)) {
                        sb.append(String.format("- Tên: %s -> %s<br>", nvCu.getHoTen(), hoTenMoi));
                        coThayDoi = true;
                    }

                    sb.append("</html>");

                    if (coThayDoi) {
                        dao.ghiLichSu(maNV, "Sửa thông tin", sb.toString(), taiKhoanHienTai);
                    } else {
                        dao.ghiLichSu(maNV, "Sửa thông tin", "Không có thay đổi nào", taiKhoanHienTai);
                    }
                    
                    JOptionPane.showMessageDialog(this, "✅ Sửa thành công!");
                    loadData("NV.MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Sửa thất bại! (Kiểm tra lại Mã Phòng Ban)");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        });

        btnXoa.addActionListener(e -> {
            if (txtMaNV.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            
            if (ui.isCheatMode()) {
                java.awt.Toolkit.getDefaultToolkit().beep(); 
                int hoi = JOptionPane.showConfirmDialog(null, 
                    "⚠️ ALERT! Enemy spotted!\nBạn có chắc muốn 'tiêu diệt' dữ liệu này không?", 
                    "Metal Gear Alert", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE); 
                
                if (hoi != JOptionPane.YES_OPTION) return;
                ui.unlockSecret("SNAKE");
            } else {
                int hoi = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa " + txtHoTen.getText() + "?\n(Cảnh báo: Tài khoản đăng nhập của người này cũng sẽ bị xóa)", "Cảnh báo", JOptionPane.YES_NO_OPTION);
                if (hoi != JOptionPane.YES_OPTION) return;
            }
            try {
                dao.xoaTaiKhoan(txtMaNV.getText()); 
            } catch (Exception ex) {
            }

            if (dao.xoaNhanVien(txtMaNV.getText())) {
                JOptionPane.showMessageDialog(null, "✅ Đã chuyển hồ sơ vào lưu trữ!");
                loadData("NV.MaNV ASC");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Lỗi hệ thống!");
            }
        });
        btnLamMoi.addActionListener(e -> { 
            resetForm();
            lastMa = ""; lastTen = ""; lastPhong = ""; lastLuong = "";
            isSortMaAsc = true;
            isSortTenAsc = true;
            isSortLuongAsc = true;
            
            btnSortMa.setText("Mã NV");
            btnSortTen.setText("Họ Tên");
            btnSortLuong.setText("Lương");
            
            reloadTable("NV.MaNV ASC");
        });

        btnTangLuong.addActionListener(e -> solve.xuLyTangLuong()); 
        btnGiamLuong.addActionListener(e -> solve.xuLyGiamLuong()); 
        btnBaoLoi.addActionListener(e -> view.hienThiFormBaoLoi()); 
        
        btnMoTinhLuong.addActionListener(e -> {
            String hoTen = "";
            long luongThucTe = 0;
            String gioiTinh = "Nam"; 
            
            if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
                NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
                if (myProfile != null) {
                    hoTen = myProfile.getHoTen();
                    luongThucTe = (long) (myProfile.getLuongCoBan() * myProfile.getHeSoLuong());
                    gioiTinh = myProfile.getGioiTinh();
                }
            } else {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tính lương!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String maNV = table.getValueAt(row, 0).toString();
                
                NhanVien nvFull = dao.getNhanVienTheoMa(maNV);
                if (nvFull != null) {
                    hoTen = nvFull.getHoTen();
                    luongThucTe = (long) (nvFull.getLuongCoBan() * nvFull.getHeSoLuong());
                    gioiTinh = nvFull.getGioiTinh();
                }
            }

            PhieuLuongUI cuaSoTinhLuong = new PhieuLuongUI(hoTen, luongThucTe, gioiTinh);
            cuaSoTinhLuong.setVisible(true);
        });

        btnThongKe.addActionListener(e -> { 
            ui.ThongKeAdmin thongKeForm = new ui.ThongKeAdmin(model);
            thongKeForm.setVisible(true);
        });
        
        btnLichSu.addActionListener(e -> view.hienThiBangLichSu());

        setHienThi(false);

        btnLoad.addActionListener(e -> { 
            setHienThi(true);
            loadData("NV.MaNV ASC");
            if (lblContraHint != null && ui.isCheatMode()) {
                lblContraHint.setVisible(true);
                lblSnake.setVisible(true);
                lblNeon.setVisible(true);
                }
        });
        
        btnKhoiPhuc.addActionListener(e -> view.hienThiCuaSoKhoiPhuc());
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });

        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(new java.awt.KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_V) {
                        if (isNeonUnlocked) {
                            if (!isNeonActive) {
                                ui.kichHoatGiaoDienAn();
                                isNeonActive = true;
                            } else {
                                ui.khoiPhucGiaoDienGoc();
                                isNeonActive = false;
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });
        
        btnChamCongLe.addActionListener(e -> solve.xuLyChamCongNgayLe());
        btnPhat.addActionListener(e -> solve.xuLyPhat());
        // File: ui/QuanLyNhanVien.java - Trong hàm xuLyNutBam()

     // File: ui/QuanLyNhanVien.java - Trong hàm xuLyNutBam()

        btnPhatLuong.addActionListener(e -> {
            Object[] options = {"🏢 Toàn Công Ty", "👤 Cá Nhân (Đang chọn)", "Hủy bỏ"};
            int choice = JOptionPane.showOptionDialog(this,
                "Bạn muốn thực hiện phát lương theo phạm vi nào?",
                "Tùy Chọn Phát Lương",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

            if (choice == 0) { // --- TOÀN CÔNG TY ---
                
                // [LILITH EDIT] 1. HỎI TRƯỚC KHI CHẠY
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn có chắc muốn chốt sổ và gửi phiếu lương cho TẤT CẢ nhân viên?\n(Hành động này sẽ gửi khoảng 100+ email nội bộ)", 
                    "Xác Nhận Phát Lương", JOptionPane.YES_NO_OPTION);
                
                // Nếu chọn NO hoặc tắt bảng đi -> Dừng lại, không làm gì cả
                if (confirm != JOptionPane.YES_OPTION) return;

                // [LILITH EDIT] 2. NẾU ĐỒNG Ý -> MỚI HIỆN LOADING
                JDialog loadingDialog = new JDialog(this, "Đang xử lý...", true);
                loadingDialog.setSize(300, 100);
                loadingDialog.setLocationRelativeTo(this);
                loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                
                JPanel pnl = new JPanel(new java.awt.BorderLayout());
                pnl.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                pnl.add(new JLabel("Đang gửi thư... Vui lòng không tắt App!", JLabel.CENTER), java.awt.BorderLayout.NORTH);
                pnl.add(progressBar, java.awt.BorderLayout.CENTER);
                loadingDialog.add(pnl);

                // 3. Chạy luồng gửi thư
                new Thread(() -> {
                    solve.xuLyPhatLuongHangLoat(); // Hàm này giờ chỉ chạy thôi, không hỏi nữa

                    SwingUtilities.invokeLater(() -> {
                        loadingDialog.dispose(); // Tắt Loading khi xong
                    });
                }).start();

                loadingDialog.setVisible(true); // Hiện Loading lên

            } else if (choice == 1) { // --- CÁ NHÂN ---
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String maNV = table.getValueAt(row, 0).toString();
                solve.xuLyGuiPhieuLuongRieng(maNV);
            }
        });    
    }
    
    private static float layGiaTriFloatTuCbo(JComboBox<String> cbo) throws NumberFormatException {
        String val = cbo.getSelectedItem() != null ? cbo.getSelectedItem().toString() : "0";
        String[] parts = val.split(" ");
        return Float.parseFloat(parts[0]);
    }

    private void resetForm() { 
        txtMaNV.setText("");
        txtHoTen.setText("");
        cboPhongBan.setSelectedIndex(-1);
        cboHeSo.setSelectedIndex(-1);
        txtMaNV.setEditable(true);
    }

    private void setHienThi(boolean hien) { 
        lblMa.setVisible(hien); lblTen.setVisible(hien);
        lblPhong.setVisible(hien); lblLuong.setVisible(hien);
        lblHS.setVisible(hien); lblSort.setVisible(hien);

        txtMaNV.setVisible(hien); txtHoTen.setVisible(hien);
        cboPhongBan.setVisible(hien); txtLuongCoBan.setVisible(hien);
        cboHeSo.setVisible(hien);
        
        btnLichSu.setVisible(hien); btnChamCongLe.setVisible(hien);
        btnThem.setVisible(hien); btnSua.setVisible(hien);
        btnXoa.setVisible(hien); btnLamMoi.setVisible(hien);
        btnTangLuong.setVisible(hien); btnMoTinhLuong.setVisible(hien);
        btnThongKe.setVisible(hien); btnTimKiem.setVisible(hien);
        btnQuanLyTK.setVisible(hien); btnGiamLuong.setVisible(hien);
        btnBaoLoi.setVisible(hien); btnChotThang.setVisible(hien);
        btnXuatExcel.setVisible(hien); btnThuongNong.setVisible(hien);
        btnPhat.setVisible(hien);
   
        btnKhoiPhuc.setVisible(hien); btnPhatLuong.setVisible(hien);

        btnSortMa.setVisible(hien); btnSortTen.setVisible(hien);
        btnSortLuong.setVisible(hien);
    }   

    private void fillTable(List<NhanVien> list) { 
        model.setRowCount(0);
        
        for (NhanVien nv : list) {
            // 1. Xử lý Thâm Niên
            String thamNien = "Mới vào";
            if (nv.getNgayVaoLam() != null) {
                java.time.LocalDate start = new java.util.Date(nv.getNgayVaoLam().getTime()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalDate now = java.time.LocalDate.now();
                int nam = java.time.Period.between(start, now).getYears();
                if (nam > 0) thamNien = nam + " năm";
            }

            // 2. [LOGIC MỚI] Xử lý Thực Lĩnh (Live Preview)
            long thucLinhHienThi = nv.getThucLinh(); // Lấy giá trị từ Database trước
            
            if (thucLinhHienThi == 0) {
                // Nếu DB bằng 0 (nghĩa là chưa Chốt Lương tháng này)
                // -> Gọi bộ xử lý Logic tính toán tạm thời để hiển thị cho đẹp
                
                double heSoTangCa = nv.getHeSoTangCa() == 0 ? 1.5 : nv.getHeSoTangCa(); // Mặc định 1.5 nếu chưa set
                
                thucLinhHienThi = logic.MayTinhLuong.tinhThucLinhFinal(
                    nv.getLuongCoBan(),
                    nv.getHeSoLuong(),
                    nv.getGioTangCa(),
                    heSoTangCa,
                    nv.getTienThuong(),
                    nv.getTienPhat(),
                    0 // Tạm tính 0 người phụ thuộc (sau này có thể nâng cấp thêm cột này)
                );
            }

            // 3. Đổ dữ liệu vào bảng
            model.addRow(new Object[] {
                nv.getMaNV(),                                // 1. Mã
                nv.getHoTen(),                               // 2. Tên
                nv.getTenPB(),                               // 3. Phòng
                String.format("%,d", nv.getLuongCoBan()),    // 4. Lương CB
                thamNien,                                    // 5. Thâm Niên
                nv.getHeSoLuong(),                           // 6. Hệ số
                String.format("%,d", nv.getTienThuong()),    // 7. Thưởng
                nv.getSoNgayDiTre() + " ngày",               // 8. Đi trễ
                String.format("%,d", nv.getTienPhat()),      // 9. Phạt
                String.format("%,d", thucLinhHienThi)        // 10. Thực Lĩnh (Đã có số liệu!)
            });
        }
    }

    void loadData(String orderBy) { 
        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cơ Bản", "Thâm Niên", "Hệ Số", "Thưởng", "Đi Trễ", "Tiền Phạt", "Thực Lĩnh"};
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

    void reloadTable(String orderBy) { 
        List < NhanVien > list = dao.timKiemDaNang(lastMa, lastTen, lastPhong, lastLuong,lastHeSo, orderBy);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu!");
            return;
        }
        
        // Logic sắp xếp tên tiếng Việt
        if (orderBy.contains("HoTen")) {
            Collections.sort(list, (nv1, nv2) -> {
                String ten1 = getTen(nv1.getHoTen());
                String ten2 = getTen(nv2.getHoTen());
                Collator collator = Collator.getInstance(Locale.of("vi", "VN"));
                int result = collator.compare(ten1, ten2);
                return result == 0 ? collator.compare(nv1.getHoTen(), nv2.getHoTen()) : result;
            });
            
            // 👇 THÊM ĐOẠN NÀY: Nếu lệnh là DESC thì đảo ngược danh sách lại
            if (orderBy.contains("DESC")) {
                Collections.reverse(list);
            }
        }
        fillTable(list);
    }

    private String getTen(String hoTen) { 
        if (hoTen == null || hoTen.trim().isEmpty()) return "";
        hoTen = hoTen.trim();
        String[] parts = hoTen.split("\\s+");
        return parts[parts.length - 1];
    }
}