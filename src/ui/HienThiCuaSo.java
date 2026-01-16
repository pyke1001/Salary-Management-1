package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.NhanVienDAO;
import entity.NhanVien;

public class HienThiCuaSo {
	
	private QuanLyNhanVien view;
	
	public HienThiCuaSo (QuanLyNhanVien view) {
		this.view = view;
	}
	
	public void hienThiCuaSoDanhBa() { 
        JDialog dialog = new JDialog(view, "Danh Bạ Nhân Viên", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(view); 
        dialog.getContentPane().setLayout(null);

        JLabel lblLoc = new JLabel("Lọc Phòng Ban:");
        lblLoc.setBounds(20, 20, 100, 30);
        dialog.getContentPane().add(lblLoc);

        JComboBox < String > cboPhong = new JComboBox < > ();
        cboPhong.setBounds(130, 20, 200, 30);
        cboPhong.addItem("Tất cả");
        
        for (String p: view.dao.layDanhSachPhongBan()) {
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

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban"};
        DefaultTableModel modelDanhBa = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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
            
            List < NhanVien > list = view.dao.timKiemDanhBa(phongDuocChon, tuKhoa);
            modelDanhBa.setRowCount(0);
            for (NhanVien nv: list) {
                modelDanhBa.addRow(new Object[] { nv.getMaNV(), nv.getHoTen(), nv.getTenPB() });
            }
        };

        cboPhong.addActionListener(e -> napDuLieu.run());
        txtTimDanhBa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { napDuLieu.run(); }
        });

        napDuLieu.run();
        dialog.setVisible(true);
    }

    public void hienThiFormDoiMatKhau() { 
        JDialog dialog = new JDialog(view, "Đổi Mật Khẩu", true); 
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(view); 
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

        btnLuu.addActionListener(e -> {
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
            
            if (view.dao.doiMatKhau(view.taiKhoanHienTai, cu, moi)) {
                JOptionPane.showMessageDialog(dialog, "✅ Đổi mật khẩu thành công!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    public void hienThiDanhSachTaiKhoanAdmin() { 
        JDialog dialog = new JDialog(view, "Danh Sách Tài Khoản & Mật Khẩu", true); 
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(view); 
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

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban", "Tài Khoản", "Mật Khẩu"};
        DefaultTableModel modelTK = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 4; }
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

        List < String[] > listGoc = view.dao.layDanhSachTaiKhoan();

        modelTK.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 4 && row >= 0) {
                    String passMoi = modelTK.getValueAt(row, column).toString();
                    String username = modelTK.getValueAt(row, 3).toString();
                    String maNV = modelTK.getValueAt(row, 0).toString();
                    
                    if (view.dao.capNhatMatKhau(username, passMoi)) {
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
                if (row[0].toLowerCase().contains(tuKhoa) || row[1].toLowerCase().contains(tuKhoa) || row[3].toLowerCase().contains(tuKhoa)) {
                    modelTK.addRow(row);
                }
            }
        };

        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { boLocDuLieu.run(); }
        });

        boLocDuLieu.run();
        dialog.setVisible(true);
    }
    
    public void hienThiFormBaoLoi() { 
        JDialog dialog = new JDialog(view, "Gửi Báo Cáo Lỗi", true); 
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(view); 
        dialog.getContentPane().setLayout(null);
        
        if (view.ui.isCheatMode()) {
            JLabel lblGradius = new JLabel("Nothing here but GRADIUS_1986.");
            lblGradius.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 10));
            lblGradius.setForeground(Color.GRAY);
            lblGradius.setBounds(120, 50, 200, 15); 
            dialog.getContentPane().add(lblGradius);
        }

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

        btnGui.addActionListener(e -> {
            String tieuDe = txtTieuDe.getText().trim();
            String noiDung = txtNoiDung.getText().trim();

            if (tieuDe.isEmpty() || noiDung.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (view.ui.isCheatMode() && tieuDe.equals("GRADIUS_1986")) {
                view.ui.unlockSecret("GRADIUS"); 
                dialog.dispose();
                return; 
            }

            NhanVienDAO dao = new NhanVienDAO();
            boolean ketQua = dao.guiBaoLoi(tieuDe, noiDung);
            
            if (ketQua) {
                JOptionPane.showMessageDialog(dialog, "✅ Cảm ơn! Báo cáo đã được gửi lên Discord.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Gửi thất bại!\nVui lòng kiểm tra mạng hoặc Webhook URL.", "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.getContentPane().add(btnGui); 
        dialog.setVisible(true);
    }
        
    public void hienThiBangLichSu() {
        JDialog dialog = new JDialog(view, "Nhật Ký Hoạt Động Hệ Thống", true); 
        dialog.setSize(1200, 600); 
        dialog.setLocationRelativeTo(view); 
        
        String[] cols = {"ID", "Nhân Viên Bị Tác Động", "Hành Động", "Chi Tiết Thay Đổi", "Người Thực Hiện", "Thời Gian"};
        
        DefaultTableModel modelLS = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JTable tblLS = new JTable(modelLS);
        tblLS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tblLS.getColumnModel().getColumn(0).setPreferredWidth(50);  
        tblLS.getColumnModel().getColumn(1).setPreferredWidth(200); 
        tblLS.getColumnModel().getColumn(2).setPreferredWidth(120); 
        tblLS.getColumnModel().getColumn(3).setPreferredWidth(450); 
        tblLS.getColumnModel().getColumn(4).setPreferredWidth(120); 
        tblLS.getColumnModel().getColumn(5).setPreferredWidth(150); 
        
        List<String[]> logs = view.dao.layDanhSachLichSu();
        for (String[] row : logs) {
            modelLS.addRow(row);
        }
        
        for (int i = 0; i < tblLS.getRowCount(); i++) {
            String noiDung = tblLS.getValueAt(i, 3).toString(); 
            int soDong = noiDung.split("<br>").length;
            int chieuCaoCanThiet = Math.max(40, soDong * 22 + 15);
            tblLS.setRowHeight(i, chieuCaoCanThiet);
        }
        
        dialog.add(new JScrollPane(tblLS));
        dialog.setVisible(true);
    }

    public void hienThiCuaSoKhoiPhuc() {
        JDialog dialog = new JDialog(view, "Hồ Sơ Lưu Trữ", true); 
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(view); 
        dialog.setLayout(new BorderLayout());

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        JTable t = new JTable(m);
        
        List<NhanVien> list = view.dao.layDanhSachNhanVienDaXoa();
        for (NhanVien nv : list) {
            m.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(), nv.getTenPB()});
        }

        JButton btnRestore = new JButton("Khôi Phục Nhân Viên");
        btnRestore.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn nhân viên!");
                return;
            }
            String ma = t.getValueAt(row, 0).toString();
            
            if (view.dao.khoiPhucNhanVien(ma)) {
                view.dao.taoLaiTaiKhoan(ma, "123");
                JOptionPane.showMessageDialog(dialog, "✅ Khôi phục thành công!");
                dialog.dispose();
                
                view.loadData("NV.MaNV ASC");
            }
        });

        dialog.add(new JScrollPane(t), BorderLayout.CENTER);
        dialog.add(btnRestore, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }    
}