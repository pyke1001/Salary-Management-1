package ui;
																					//Controller - Cả nhóm
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

    public QuanLyNhanVien(String username, String role) {								// Hàm khởi tạo
        super();
        this.taiKhoanHienTai = username;
        this.quyenHienTai = role;
        initEvents();
        phanQuyen();
    }
    
    private void phanQuyen() {														// Hàm 'Phân quyền' - Việt
        if (quyenHienTai.equalsIgnoreCase("Admin")) {
            btnQuanLyTK.addActionListener(e -> hienThiDanhSachTaiKhoanAdmin());
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
            btnTangLuong.setVisible(false);
            btnTimKiem.setVisible(false);
            btnLoad.setVisible(false);
            lblSort.setVisible(false);
            btnSortMa.setVisible(false);
            btnSortTen.setVisible(false);
            btnSortLuong.setVisible(false);
            lblTre.setVisible(false);
            txtNgayTre.setVisible(false);
            btnThongKe.setVisible(false);

            lblMa.setVisible(true);
            txtMaNV.setVisible(true);
            lblTen.setVisible(true);
            txtHoTen.setVisible(true);
            lblPhong.setVisible(true);
            txtPhongBan.setVisible(true);
            lblLuong.setVisible(true);
            txtLuongCoBan.setVisible(true);
            lblHS.setVisible(true);
            txtHeSo.setVisible(true);

            Font fontTo = new Font("Segoe UI", Font.BOLD, 18);
            Font fontNhan = new Font("Segoe UI", Font.PLAIN, 16);

            int labelX = 180;
            int textX = 300;
            int widthText = 350;
            int startY = 60;
            int gap = 60;

            lblMa.setBounds(labelX, startY, 100, 30);
            lblMa.setFont(fontNhan);
            txtMaNV.setBounds(textX, startY, widthText, 40);
            txtMaNV.setFont(fontTo);

            lblTen.setBounds(labelX, startY + gap, 100, 30);
            lblTen.setFont(fontNhan);
            txtHoTen.setBounds(textX, startY + gap, widthText, 40);
            txtHoTen.setFont(fontTo);

            lblPhong.setBounds(labelX, startY + gap * 2, 100, 30);
            lblPhong.setFont(fontNhan);
            txtPhongBan.setBounds(textX, startY + gap * 2, widthText, 40);
            txtPhongBan.setFont(fontTo);

            lblLuong.setBounds(labelX, startY + gap * 3, 100, 30);
            lblLuong.setFont(fontNhan);
            txtLuongCoBan.setBounds(textX, startY + gap * 3, widthText, 40);
            txtLuongCoBan.setFont(fontTo);
            txtLuongCoBan.setForeground(Color.RED);

            lblHS.setBounds(labelX, startY + gap * 4, 100, 30);
            lblHS.setFont(fontNhan);
            txtHeSo.setBounds(textX, startY + gap * 4, widthText, 40);
            txtHeSo.setFont(fontTo);

            NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
            if (myProfile != null) {
                setTitle("Hồ Sơ Cá Nhân - " + myProfile.getHoTen());
                txtMaNV.setText(myProfile.getMaNV());
                txtHoTen.setText(myProfile.getHoTen());
                txtPhongBan.setText(myProfile.getMaPB());
                txtLuongCoBan.setText(String.format("%,d", myProfile.getLuongCoBan()) + " VNĐ");
                txtHeSo.setText(String.valueOf(myProfile.getHeSoLuong()));
            }

            JTextField[] cacO = {txtMaNV, txtHoTen, txtPhongBan, txtLuongCoBan, txtHeSo};
            for (JTextField txt : cacO) {
                txt.setEditable(false);
                txt.setBackground(Color.WHITE);
                txt.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
                txt.setFocusable(false);
            }

            JButton btnDanhBa = new JButton("📖 Danh bạ");
            btnDanhBa.setBounds(textX, 400, 160, 45);
            btnDanhBa.setFont(new Font("Dialog", Font.BOLD, 16));
            btnDanhBa.setBackground(new Color(173, 216, 230));
            add(btnDanhBa);
            btnDanhBa.setVisible(true);
            btnDanhBa.addActionListener(e -> hienThiCuaSoDanhBa());

            btnMoTinhLuong.setText("💰 Phiếu Lương");
            btnMoTinhLuong.setBounds(textX + 190, 400, 160, 45);
            btnMoTinhLuong.setVisible(true);

            JButton btnDoiMK = new JButton("🔒 Đổi Mật Khẩu");
            btnDoiMK.setBounds(textX + 80, 460, 180, 40);
            btnDoiMK.setFont(new Font("Dialog", Font.BOLD, 13));
            btnDoiMK.setBackground(new Color(255, 228, 181));
            add(btnDoiMK);
            btnDoiMK.setVisible(true);
            btnDoiMK.addActionListener(e -> hienThiFormDoiMatKhau());
        }
    }
    
    private void initEvents() {														// Hàm 'Xử lí sự kiện' - Cả nhóm

        btnSortMa.addActionListener(e -> reloadTable("NV.MaNV ASC"));           	// Xử lí sự kiện: 'Sắp xếp Mã NV'        
        btnSortTen.addActionListener(e -> reloadTable("NV.HoTen ASC"));				// Xử lí sự kiện: 'Sắp xếp Họ Tên'      //Tóm gọn là Xử lí sự kiện: 'Sắp xếp' - Việt
        btnSortLuong.addActionListener(e -> reloadTable("NV.LuongCoBan DESC"));     // Xử lí sự kiện: 'Sắp xếp Lương'
        																	
        btnTimKiem.addActionListener(e -> xuLyTimKiemDaNang());						// Xử lí sự kiện: 'Tìm kiếm' - Việt
        
        table.addMouseListener(new MouseAdapter() {                 				// Xử lí sự kiện: 'Click - Chỉnh sửa' - Việt
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtMaNV.setText(table.getValueAt(row, 0).toString());
                    txtHoTen.setText(table.getValueAt(row, 1).toString());
                    txtPhongBan.setText(table.getValueAt(row, 2).toString());
                    
                    String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "");
                    txtLuongCoBan.setText(luongStr);
                    
                    txtHeSo.setText(table.getValueAt(row, 4).toString());
                    txtMaNV.setEditable(false); 
                }
            }
        });

        btnThem.addActionListener(e -> {                            				// Xử lí sự kiện: 'Thêm' - Việt
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

        btnSua.addActionListener(e -> {                             				// Xử lí sự kiện: 'Sửa' - Việt
            if (txtMaNV.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên để sửa!");
                return;
            }
            try {
                NhanVien nv = new NhanVien();
                nv.setMaNV(txtMaNV.getText());
                nv.setHoTen(txtHoTen.getText());
                nv.setMaPB(txtPhongBan.getText());
                nv.setLuongCoBan(Long.parseLong(txtLuongCoBan.getText()));
                nv.setHeSoLuong(Float.parseFloat(txtHeSo.getText()));

                if (dao.suaNhanVien(nv)) {
                    JOptionPane.showMessageDialog(null, "✅ Cập nhật thành công!");
                    loadData("NV.MaNV ASC");
                    txtMaNV.setEditable(true);
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi khi sửa!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Lỗi nhập liệu!");
            }
        });

        btnXoa.addActionListener(e -> {                                 			// Xử lí sự kiện: 'Xóa' - Việt
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
        
        btnLamMoi.addActionListener(e -> {											// Xử lí sự kiện: 'Làm mới' - Tùng
            resetForm();
            lastMa = ""; lastTen = ""; lastPhong = ""; lastLuong = "";
            reloadTable("NV.MaNV ASC");
        });

        btnPhat.addActionListener(e -> {                                			// Xử lí sự kiện: 'Click - Cập nhật Phạt' - Việt
            try {                                                                               
                int selectedRow = table.getSelectedRow();                                           
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần phạt!");
                    return;   
                }
                String maNV = table.getValueAt(selectedRow, 0).toString();            
                String strNgayTre = txtNgayTre.getText();                   
                if (strNgayTre.isEmpty()) return;
                
                dao.capNhatPhat(maNV, Integer.parseInt(strNgayTre));
                
                JOptionPane.showMessageDialog(null, "Cập nhật phạt thành công!");
                loadData("NV.MaNV ASC");               
                txtNgayTre.setText("");         
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            }
        });

        btnTangLuong.addActionListener(e -> xuLyTangLuong());               			// Xử lí sự kiện: 'Tăng lương' - Quốc

        btnMoTinhLuong.addActionListener(e -> {                         			// Xử lí sự kiện: 'Mở Bảng Lương' - Đồng
            TinhLuongUI cuaSoTinhLuong = new TinhLuongUI();
            cuaSoTinhLuong.setVisible(true);
            cuaSoTinhLuong.setLocationRelativeTo(null);
        });

        btnThongKe.addActionListener(e -> {                             			// Xử lí sự kiện: 'Thống Kê' - Hướng
            ThongKeUI fr = new ThongKeUI();
            fr.setVisible(true);
        });

        setHienThi(false);                                              
        
        btnLoad.addActionListener(e -> {                                			// Xử lí sự kiện: 'Tải danh sách' - Việt
            
            setHienThi(true);                                           
            loadData("NV.MaNV ASC");
        });
    }

    private void resetForm() {                                          			// Hàm 'Lau bảng (Ô nhập liệu)' - Dùng trong Xử lí sự kiện: 'Thêm', 'Xóa', 'Làm mới' - Tùng
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhongBan.setText("");
        txtLuongCoBan.setText("");
        txtHeSo.setText("");
        txtMaNV.setEditable(true);
    }

    private void setHienThi(boolean hien) {                            				// Hàm 'Hiển thị' - Dùng trong Xử lí sự kiện: 'Tải danh sách' - Việt
        lblMa.setVisible(hien); lblTen.setVisible(hien); lblPhong.setVisible(hien);
        lblLuong.setVisible(hien); lblHS.setVisible(hien); lblTre.setVisible(hien);
        lblSort.setVisible(hien);
        
        txtMaNV.setVisible(hien); txtHoTen.setVisible(hien); txtPhongBan.setVisible(hien);
        txtLuongCoBan.setVisible(hien); txtHeSo.setVisible(hien); txtNgayTre.setVisible(hien);
                                                                        			// Vai trò: Công tắc ẩn/hiện tất cả các nút
        btnThem.setVisible(hien); btnSua.setVisible(hien); btnXoa.setVisible(hien);
        btnLamMoi.setVisible(hien); btnPhat.setVisible(hien); btnTangLuong.setVisible(hien);
        btnMoTinhLuong.setVisible(hien); btnThongKe.setVisible(hien);btnTimKiem.setVisible(hien);
        btnQuanLyTK.setVisible(hien);

        btnSortMa.setVisible(hien);
        btnSortTen.setVisible(hien);
        btnSortLuong.setVisible(hien);
    }
                                                                        
    private void xuLyTangLuong() {													// Hàm 'Click - Tăng lương' - Dùng trong Xử lí sự kiện: 'Tăng lương' - Quốc
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

        String[] options = {"KPI Loại A (Xuất sắc)", "KPI Loại B (Giỏi)", "KPI Loại C (Khá)", "Nhập tay %"};
        JComboBox<String> cboOption = new JComboBox<>(options);
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
    
    private void fillTable(List<NhanVien> list) {									// Hàm 'Vẽ bảng' - Dùng trong Hàm 'Lau bảng (Danh sách)' và Hàm 'Tiện ích' - Việt
        model.setRowCount(0); 
        
        for (NhanVien nv : list) {
            java.util.Vector<Object> row = new java.util.Vector<>();
            row.add(nv.getMaNV());
            row.add(nv.getHoTen());
            
            if (nv.getTenPB() != null) {
                row.add(nv.getTenPB());
            } else {
                row.add(nv.getMaPB());
            }

            row.add(String.format("%,d", nv.getLuongCoBan())); 
            row.add(nv.getHeSoLuong());
            row.add(String.format("%,d", nv.getTienThuong()));
            row.add(nv.getSoNgayDiTre() + " ngày");
            row.add(String.format("%,d", nv.getTienPhat()));
            row.add(String.format("%,d", nv.getThucLinh()));
            
            model.addRow(row);
        }
    }
    private void loadData(String orderBy) {                             			// Hàm 'Lau bảng (Danh sách)' - Việt
       String[] columns = {                            			// Dùng trong Xử lí sự kiện: 'Click- Cập nhật Phạt', 'Thêm', 'Xóa', 'Sửa'
            "Mã NV", "Họ Tên", "Phòng Ban", 
            "Lương Cứng", "Hệ Số", "Thưởng", 
            "Đi Trễ", "Tiền Phạt", "Thực Lĩnh" 
        };
        model = new DefaultTableModel(columns, 0);
        table.setModel(model);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(60); 
        table.getColumnModel().getColumn(1).setPreferredWidth(160); 
        table.getColumnModel().getColumn(2).setPreferredWidth(140); 
        table.getColumnModel().getColumn(3).setPreferredWidth(100); 
        table.getColumnModel().getColumn(4).setPreferredWidth(50); 
        table.getColumnModel().getColumn(8).setPreferredWidth(110);
        
        List<NhanVien> list = dao.layDanhSachNhanVien(orderBy);
        fillTable(list);
    }
    
    
    private void reloadTable(String orderBy) {										// Hàm 'Tiện ích'- Dùng trong Hàm 'Tìm thông tin' - Việt
        List<NhanVien> list = dao.timKiemDaNang(lastMa, lastTen, lastPhong, lastLuong, orderBy);
        
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu!");
            return;
        }
        
        fillTable(list);
    }

    private void xuLyTimKiemDaNang() {												// Hàm 'Tìm thông tin' - Dùng trong Xử lí sự kiện: 'Tìm Kiếm' - Việt
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

    private void hienThiCuaSoDanhBa() {												// Hàm 'Cửa sổ danh bạ'- Dùng trong Hàm 'Phân quyền' - Việt
        JDialog dialog = new JDialog(this, "Danh Bạ Nhân Viên", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel lblLoc = new JLabel("Lọc Phòng Ban:");
        lblLoc.setBounds(20, 20, 100, 30);
        dialog.add(lblLoc);

        JComboBox<String> cboPhong = new JComboBox<>();
        cboPhong.setBounds(130, 20, 200, 30);
        cboPhong.addItem("Tất cả");
        for (String p : dao.layDanhSachPhongBan()) {
            cboPhong.addItem(p);
        }
        dialog.add(cboPhong);

        JLabel lblTim = new JLabel("🔍 Tìm nhanh:");
        lblTim.setBounds(20, 60, 100, 30);
        dialog.add(lblTim);

        JTextField txtTimDanhBa = new JTextField();
        txtTimDanhBa.setBounds(130, 60, 430, 30);
        txtTimDanhBa.setToolTipText("Nhập Tên hoặc Mã NV để tìm...");
        dialog.add(txtTimDanhBa);

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban"};
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
        dialog.add(sp);

        Runnable napDuLieu = () -> {
            String phongDuocChon = cboPhong.getSelectedItem().toString();
            String tuKhoa = txtTimDanhBa.getText().trim();

            List<NhanVien> list = dao.timKiemDanhBa(phongDuocChon, tuKhoa);

            modelDanhBa.setRowCount(0);
            for (NhanVien nv : list) {
                modelDanhBa.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getHoTen(),
                    nv.getTenPB()
                });
            }
        };

        cboPhong.addActionListener(e -> napDuLieu.run());

        txtTimDanhBa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                napDuLieu.run();
            }
        });

        napDuLieu.run();
        dialog.setVisible(true);
    }
    private void hienThiFormDoiMatKhau() {											// Hàm 'Đổi mật khẩu' - Dùng trong Hàm 'Phân quyền' - Việt
        JDialog dialog = new JDialog(this, "Đổi Mật Khẩu", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        
        JLabel lblCu = new JLabel("Mật khẩu cũ:");
        lblCu.setBounds(30, 30, 100, 30);
        dialog.add(lblCu);
        JPasswordField txtPassCu = new JPasswordField();
        txtPassCu.setBounds(140, 30, 200, 30);
        dialog.add(txtPassCu);

        JLabel lblMoi = new JLabel("Mật khẩu mới:");
        lblMoi.setBounds(30, 80, 100, 30);
        dialog.add(lblMoi);
        JPasswordField txtPassMoi = new JPasswordField();
        txtPassMoi.setBounds(140, 80, 200, 30);
        dialog.add(txtPassMoi);

        JLabel lblXacNhan = new JLabel("Nhập lại MK:");
        lblXacNhan.setBounds(30, 130, 100, 30);
        dialog.add(lblXacNhan);
        JPasswordField txtPassXacNhan = new JPasswordField();
        txtPassXacNhan.setBounds(140, 130, 200, 30);
        dialog.add(txtPassXacNhan);

        JButton btnLuu = new JButton("💾 Lưu Thay Đổi");
        btnLuu.setBounds(100, 190, 180, 40);
        btnLuu.setBackground(Color.GREEN);
        dialog.add(btnLuu);

        btnLuu.addActionListener(e -> {												// Xử lí sự kiện: 'Lưu' 						
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
    
    private void hienThiDanhSachTaiKhoanAdmin() {									// Hàm 'Danh sách tài khoản - Admin' - Dùng trong Hàm 'Phân quyền' - Việt	
        JDialog dialog = new JDialog(this, "Danh Sách Tài Khoản & Mật Khẩu", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        JLabel lblTitle = new JLabel("BẢNG THEO DÕI TÀI KHOẢN NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.RED);
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setBounds(0, 10, 780, 30);
        dialog.add(lblTitle);

        JLabel lblTim = new JLabel("🔍 Tìm nhanh:");
        lblTim.setBounds(30, 50, 100, 30);
        lblTim.setFont(new Font("Dialog", Font.BOLD, 12));
        dialog.add(lblTim);

        JTextField txtTimKiem = new JTextField();
        txtTimKiem.setBounds(120, 50, 630, 30);
        txtTimKiem.setToolTipText("Nhập Mã NV, Tên hoặc Tài khoản để tìm...");
        dialog.add(txtTimKiem);

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban", "Tài Khoản", "Mật Khẩu"};
        DefaultTableModel modelTK = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tableTK = new JTable(modelTK);
        tableTK.setRowHeight(25);
        tableTK.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTK.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTK.getColumnModel().getColumn(1).setPreferredWidth(150);
        
        JScrollPane sp = new JScrollPane(tableTK);
        sp.setBounds(30, 90, 720, 350); 
        dialog.add(sp);

        List<String[]> listGoc = dao.layDanhSachTaiKhoan();

        Runnable boLocDuLieu = () -> {
            String tuKhoa = txtTimKiem.getText().toLowerCase().trim();
            modelTK.setRowCount(0);

            for (String[] row : listGoc) {
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
}