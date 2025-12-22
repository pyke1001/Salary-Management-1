package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

import dao.NhanVienDAO;
import entity.NhanVien;


public class QuanLyNhanVien extends NhanVienUI {
	
	Secret ui = new Secret(this);    
    HoSoNhanVien nvUI = new HoSoNhanVien(this);
    HienThiCuaSo view = new HienThiCuaSo(this);
    XuLyNutBam solve = new XuLyNutBam(this);
    NhanVienDAO dao = new NhanVienDAO();
    
    private static final long serialVersionUID = 2L;

    public QuanLyNhanVien(String username, String role) {
    	
        super();
        this.taiKhoanHienTai = username;
        this.quyenHienTai = role;
        
        napDuLieuPhongBan();

        cboLuongCoBan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Tất cả mức lương", "Dưới 5 triệu", "5 triệu - 10 triệu", 
            "10 triệu - 20 triệu", "Trên 20 triệu"
        }));

        cboHeSo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Tất cả hệ số", "Dưới 2.0", "2.0 - 3.0", "Trên 3.0"
        }));
        
        initEvents();
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
            btnThuongNong.addActionListener(e -> solve.xuLyThuongNong());
            btnChotThang.addActionListener(e -> solve.xuLyChotThang());
            btnXuatExcel.addActionListener(e -> solve.xuLyXuatExcel()); 
            
            btnPhat.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần phạt!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String maNV = table.getValueAt(row, 0).toString();
                String hoTen = table.getValueAt(row, 1).toString();
                
                String input = JOptionPane.showInputDialog(this, 
                    "Nhập số ngày đi trễ của " + hoTen + ":\n(Ví dụ: 1, 2, 3...)", 
                    "Xử Lý Vi Phạm", JOptionPane.QUESTION_MESSAGE);
                    
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int soNgay = Integer.parseInt(input.trim());
                        if (soNgay < 0) {
                            JOptionPane.showMessageDialog(this, "Số ngày không được âm!");
                            return;
                        }
                        dao.capNhatPhat(maNV, soNgay); 
                        dao.ghiLichSu(maNV, "Phạt đi trễ", "Số ngày trễ: " + soNgay, taiKhoanHienTai);
                        JOptionPane.showMessageDialog(this, "✅ Đã ghi nhận phạt cho: " + hoTen);
                        loadData("NV.MaNV ASC");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!");
                    }
                }
            });
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
    
    private void initEvents() {
        btnSortMa.addActionListener(e -> reloadTable("NV.MaNV ASC")); 
        btnSortTen.addActionListener(e -> reloadTable("NV.HoTen ASC")); 
        btnSortLuong.addActionListener(e -> reloadTable("NV.LuongCoBan DESC")); 

        btnTimKiem.addActionListener(e -> solve.xuLyTimKiemDaNang()); 

        table.addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String maNV = table.getValueAt(row, 0).toString().trim();
                    txtMaNV.setText(maNV);
                    txtHoTen.setText(table.getValueAt(row, 1).toString());
                    cboPhongBan.setSelectedItem(table.getValueAt(row, 2).toString());
                    String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "");
                    cboLuongCoBan.setSelectedItem(luongStr);
                    cboHeSo.setSelectedItem(table.getValueAt(row, 5).toString());
                    txtMaNV.setEditable(false);
                    
                    if (ui.isCheatMode()) { 
                        if (maNV.equalsIgnoreCase("NV30")) {
                        	ui.unlockSecret("CONTRA");
                        }
                    }
                }
            }
        });

        btnThem.addActionListener(e -> { 
            if (txtMaNV.getText().equals("") || txtHoTen.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            try {
                long luong = layGiaTriTuCbo(cboLuongCoBan);
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

                long luongMoi = layGiaTriTuCbo(cboLuongCoBan);
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
            reloadTable("NV.MaNV ASC");
        });

        btnTangLuong.addActionListener(e -> solve.xuLyTangLuong()); 
        btnGiamLuong.addActionListener(e -> solve.xuLyGiamLuong()); 
        btnBaoLoi.addActionListener(e -> view.hienThiFormBaoLoi()); 
        
        btnMoTinhLuong.addActionListener(e -> {
            String hoTen = "";
            long luongCoBan = 0;
            
            if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
                NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
                if (myProfile != null) {
                    hoTen = myProfile.getHoTen();
                    luongCoBan = myProfile.getLuongCoBan();
                }
            } else {
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
    }
    
    private long layGiaTriTuCbo(JComboBox<String> cbo) throws NumberFormatException {
        String val = cbo.getSelectedItem() != null ? cbo.getSelectedItem().toString() : "0";
        val = val.replaceAll("[^0-9]", "");
        if (val.isEmpty()) return 0;
        return Long.parseLong(val);
    }
    
    private float layGiaTriFloatTuCbo(JComboBox<String> cbo) throws NumberFormatException {
        String val = cbo.getSelectedItem() != null ? cbo.getSelectedItem().toString() : "0";
        String[] parts = val.split(" ");
        return Float.parseFloat(parts[0]);
    }

    private void resetForm() { 
        txtMaNV.setText("");
        txtHoTen.setText("");
        cboPhongBan.setSelectedIndex(-1);
        cboLuongCoBan.setSelectedIndex(-1);
        cboHeSo.setSelectedIndex(-1);
        txtMaNV.setEditable(true);
    }

    private void setHienThi(boolean hien) { 
        lblMa.setVisible(hien); lblTen.setVisible(hien);
        lblPhong.setVisible(hien); lblLuong.setVisible(hien);
        lblHS.setVisible(hien); lblSort.setVisible(hien);

        txtMaNV.setVisible(hien); txtHoTen.setVisible(hien);
        cboPhongBan.setVisible(hien); cboLuongCoBan.setVisible(hien);
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
   
        btnKhoiPhuc.setVisible(hien);

        btnSortMa.setVisible(hien); btnSortTen.setVisible(hien);
        btnSortLuong.setVisible(hien);
    }   

    private void fillTable(List < NhanVien > list) { 
        model.setRowCount(0);
        for (NhanVien nv: list) {
            java.util.Vector < Object > row = new java.util.Vector < > ();
            row.add(nv.getMaNV());
            row.add(nv.getHoTen());
            row.add(nv.getTenPB() != null ? nv.getTenPB() : nv.getMaPB());
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
        if (orderBy.contains("HoTen")) {
            Collections.sort(list, (nv1, nv2) -> {
                String ten1 = getTen(nv1.getHoTen());
                String ten2 = getTen(nv2.getHoTen());
                Collator collator = Collator.getInstance(Locale.of("vi", "VN"));
                int result = collator.compare(ten1, ten2);
                return result == 0 ? collator.compare(nv1.getHoTen(), nv2.getHoTen()) : result;
            });
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