package quanlyluong;
																	// View - Cả nhóm
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

public class FormNhanVien extends JFrame {							// Lớp 'FormNhanVien' - Việt

    JTable table;
    DefaultTableModel model;
    JTextField txtNgayTre;
    JLabel lblTre;
    JButton btnThem, btnSua, btnXoa, btnPhat, btnLoad, btnMoTinhLuong;
    
    private JLabel lblMa, lblTen, lblPhong, lblLuong, lblHS, lblSort;
    private JButton btnLamMoi, btnTangLuong, btnThongKe;
    private JButton btnSapXepMa, btnSapXepTen, btnSapXepLuong;
    
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JTextField txtLuongCoBan;
    private JTextField txtHeSo;

    private NhanVienDAO dao = new NhanVienDAO();
    private static final long serialVersionUID = 1L;				

    public FormNhanVien() {

        setTitle("Quản Lý Nhân Viên - VKU");						// Khung
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        
        lblMa = new JLabel("Mã NV:");								// Thùng chứa 'Mã NV'
        lblMa.setBounds(20, 20, 80, 25);
        getContentPane().add(lblMa);
        txtMaNV = new JTextField();
        txtMaNV.setBounds(80, 20, 100, 25);
        getContentPane().add(txtMaNV);

        lblTen = new JLabel("Họ Tên:");								// Thùng chứa 'Họ Tên'
        lblTen.setBounds(200, 20, 80, 25);
        getContentPane().add(lblTen);
        txtHoTen = new JTextField();
        txtHoTen.setBounds(260, 20, 150, 25);
        getContentPane().add(txtHoTen);

        lblPhong = new JLabel("Phòng:");							// Thùng chứa 'Phòng'
        lblPhong.setBounds(20, 60, 80, 25);
        getContentPane().add(lblPhong);
        txtPhongBan = new JTextField();
        txtPhongBan.setBounds(80, 60, 100, 25);
        getContentPane().add(txtPhongBan);

        lblLuong = new JLabel("Lương:");							// Thùng chứa 'Lương'
        lblLuong.setBounds(200, 60, 80, 25);
        getContentPane().add(lblLuong);
        txtLuongCoBan = new JTextField();
        txtLuongCoBan.setBounds(260, 60, 150, 25);
        getContentPane().add(txtLuongCoBan);

        lblHS = new JLabel("Hệ số:");								// Thùng chứa 'Hệ số'
        lblHS.setBounds(430, 60, 50, 25);
        getContentPane().add(lblHS);
        txtHeSo = new JTextField();
        txtHeSo.setBounds(480, 60, 50, 25);
        getContentPane().add(txtHeSo);

        btnThem = new JButton("➕ Thêm"); 							// Nút 'Thêm'
        btnThem.setBounds(430, 15, 100, 30);
        btnThem.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnThem);
        
        btnSua = new JButton("✏️ Sửa"); 								// Nút 'Sửa'
        btnSua.setBounds(540, 15, 100, 30);
        btnSua.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnSua);
        
        btnXoa = new JButton("🗑️ Xóa"); 								// Nút 'Xóa'
        btnXoa.setBounds(650, 15, 100, 30);
        btnXoa.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnXoa);
        
        btnLamMoi = new JButton("🔄 Làm mới");						// Nút 'Làm mới'
        btnLamMoi.setBounds(650, 56, 100, 30);
        btnLamMoi.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnLamMoi);

        lblSort = new JLabel("Sắp xếp theo:");						// Nhãn 'Sắp xếp theo'
        lblSort.setBounds(10, 85, 100, 20);
        lblSort.setFont(new Font("Dialog", Font.ITALIC, 12));
        getContentPane().add(lblSort);

        btnSapXepMa = new JButton("Mã NV");							// Nút 'Sắp xếp Mã NV'
        btnSapXepMa.setBounds(100, 85, 80, 20);
        btnSapXepMa.setFont(new Font("Arial", Font.PLAIN, 10));
        getContentPane().add(btnSapXepMa);

        btnSapXepTen = new JButton("Họ Tên");						// Nút 'Sắp xếp Họ Tên'
        btnSapXepTen.setBounds(190, 85, 80, 20);
        btnSapXepTen.setFont(new Font("Arial", Font.PLAIN, 10));
        getContentPane().add(btnSapXepTen);

        btnSapXepLuong = new JButton("Lương");						// Nút 'Sắp xếp Lương'
        btnSapXepLuong.setBounds(280, 85, 80, 20);
        btnSapXepLuong.setFont(new Font("Arial", Font.PLAIN, 10));
        getContentPane().add(btnSapXepLuong);

        btnSapXepMa.addActionListener(e -> loadData("MaNV ASC"));   			// Xử lí sự kiện: 'Sắp xếp Mã NV'  - Việt
        
        btnSapXepTen.addActionListener(e -> {									// Xử lí sự kiện: 'Sắp xếp Họ Tên' - Việt
            String sqlSortVietnamese = "SUBSTRING(HoTen, LEN(HoTen) - CHARINDEX(' ', REVERSE(HoTen)) + 2, LEN(HoTen)) ASC, HoTen ASC";
            loadData(sqlSortVietnamese);     
        });
        
        btnSapXepLuong.addActionListener(e -> loadData("LuongCoBan DESC"));		// Xử lí sự kiện: 'Sắp xếp Lương' - Việt

        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cứng", "Hệ Số", "Tổng Nhận"};		// Cột
        model = new DefaultTableModel(columns, 0);					// Model
        table = new JTable(model);									// Table
        
        JScrollPane sp = new JScrollPane(table); 					// Thanh cuộn
        sp.setBounds(10, 115, 815, 295); 
        getContentPane().add(sp);

        table.addMouseListener(new MouseAdapter() {					// Xử lí sự kiện: 'Chuột chọn hàng - Chỉnh sửa dữ liệu' - Việt
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

        btnThem.addActionListener(e -> {							// Xử lí sự kiện: 'Thêm' - Việt
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
                    loadData("MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi: Mã nhân viên trùng hoặc sai định dạng số!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Lỗi nhập liệu!");
            }
        });

        btnSua.addActionListener(e -> {								// Xử lí sự kiện: 'Sửa' - Việt
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
                    loadData("MaNV ASC");
                    txtMaNV.setEditable(true);
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi khi sửa!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Lỗi nhập liệu!");
            }
        });

        btnXoa.addActionListener(e -> {									// Xử lí sự kiện: 'Xóa' - Việt
            if (txtMaNV.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            int hoi = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (hoi != JOptionPane.YES_OPTION) return;

            if (dao.xoaNhanVien(txtMaNV.getText())) {
                JOptionPane.showMessageDialog(null, "✅ Đã xóa thành công!");
                loadData("MaNV ASC");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Lỗi: Không thể xóa!");
            }
        });
        
        btnLamMoi.addActionListener(e -> {								// Xử lí sự kiện: 'Làm mới' - Tùng
            resetForm();
            table.clearSelection();
        });

        lblTre = new JLabel("Số ngày trễ:");							// Nhãn 'Số ngày trễ'
        lblTre.setBounds(20, 420, 100, 30);
        getContentPane().add(lblTre);
        
        txtNgayTre = new JTextField();  								// Ô nhập liệu 'Số ngày trễ'
        txtNgayTre.setBounds(100, 420, 100, 30);
        getContentPane().add(txtNgayTre);

        btnPhat = new JButton("⚠️ Cập nhật Phạt");             			// Nút 'Cập nhật Phạt'											
        btnPhat.setBounds(210, 420, 175, 30);                    													
        btnPhat.setFont(new Font("Dialog", Font.BOLD, 14));
        getContentPane().add(btnPhat);	                   
        
        btnPhat.addActionListener(e -> {								// Xử lí sự kiện: 'Chuột chọn hàng - Cập nhật Phạt' - Việt
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
                loadData("MaNV ASC");            
                txtNgayTre.setText("");         
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
            }
        });

        btnTangLuong = new JButton("💰 Tăng lương");						// Nút 'Tăng lương'
        btnTangLuong.setFont(new Font("Dialog", Font.BOLD, 14));
        btnTangLuong.setBounds(400, 420, 150, 30);
        getContentPane().add(btnTangLuong);
        
        btnTangLuong.addActionListener(e -> tangLuong());				// Xử lí sự kiện: 'Tăng lương' - Quốc

        btnMoTinhLuong = new JButton("📋 Mở Bảng Lương");				// Nút 'Mở Bảng Lương'
        btnMoTinhLuong.setFont(new Font("Dialog", Font.BOLD, 14));
        btnMoTinhLuong.setBounds(570, 420, 175, 30); 
        getContentPane().add(btnMoTinhLuong);
        
        btnMoTinhLuong.addActionListener(e -> {							// Xử lí sự kiện: 'Mở Bảng Lương' - Đồng
            GiaoDienChinh cuaSoTinhLuong = new GiaoDienChinh();
            cuaSoTinhLuong.setVisible(true);
            cuaSoTinhLuong.setLocationRelativeTo(null);
        });

        btnThongKe = new JButton("📊 Thống Kê");							// Nút 'Thống Kê' - 
        btnThongKe.setBounds(570, 460, 175, 30); 
        btnThongKe.setFont(new Font("Dialog", Font.BOLD, 14));
        getContentPane().add(btnThongKe);

        btnThongKe.addActionListener(e -> {								// Xử lí sự kiện: 'Thống Kê' - Hướng
            FormThongKe fr = new FormThongKe();
            fr.setVisible(true);
        });

        btnLoad = new JButton("📂 Tải danh sách từ SQL");				// Nút 'Tải danh sách từ SQL'
        btnLoad.setBounds(10, 500, 815, 40);
        btnLoad.setFont(new Font("Dialog", Font.BOLD, 16));
        getContentPane().add(btnLoad);
        
        setHienThi(false); 												
        
        btnLoad.addActionListener(e -> {								// Xử lí sự kiện: 'Tải danh sách từ SQL' - Việt
        	
            setHienThi(true);											
            loadData("MaNV ASC");
        });
    }

    private void resetForm() {											// Hàm 'Lau bảng (Ô nhập liệu)' - Dùng trong Xử lí sự kiện: 'Thêm', 'Xóa', 'Làm mới' - Tùng
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhongBan.setText("");
        txtLuongCoBan.setText("");
        txtHeSo.setText("");
        txtMaNV.setEditable(true);
    }

    private void setHienThi(boolean hien) {								// Hàm 'Hiển thị' - Dùng trong Xử lí sự kiện: 'Tải danh sách từ SQL' - Việt
        lblMa.setVisible(hien); lblTen.setVisible(hien); lblPhong.setVisible(hien);
        lblLuong.setVisible(hien); lblHS.setVisible(hien); lblTre.setVisible(hien);
        
        txtMaNV.setVisible(hien); txtHoTen.setVisible(hien); txtPhongBan.setVisible(hien);
        txtLuongCoBan.setVisible(hien); txtHeSo.setVisible(hien); txtNgayTre.setVisible(hien);
        																// Vai trò: Công tắc ẩn/hiện tất cả các nút
        btnThem.setVisible(hien); btnSua.setVisible(hien); btnXoa.setVisible(hien);
        btnLamMoi.setVisible(hien); btnPhat.setVisible(hien); btnTangLuong.setVisible(hien);
        btnMoTinhLuong.setVisible(hien); btnThongKe.setVisible(hien);

        lblSort.setVisible(hien);
        btnSapXepMa.setVisible(hien);
        btnSapXepTen.setVisible(hien);
        btnSapXepLuong.setVisible(hien);
    }
    																	
    private void tangLuong() {											// Hàm 'Tăng lương' - Dùng trong Xử lí sự kiện: 'Tăng lương' - Quốc
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên trong bảng!");
            return;
        }
        String maNV = model.getValueAt(row, 0).toString();
        String input = JOptionPane.showInputDialog(null, "Nhập % tăng lương:", "Tăng lương", JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            double percent = Double.parseDouble(input);
            int kq = dao.tangLuong(maNV, percent);

            if (kq > 0) {
                JOptionPane.showMessageDialog(null, "Tăng lương thành công!");
                loadData("MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy nhân viên!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }

    private void loadData(String orderBy) {								// Hàm 'Lau bảng' (Danh sách) - Việt
    						// Dùng trong Xử lí sự kiện: 'Chuột chọn hàng - Cập nhật Phạt', 'Thêm', 'Xóa', 'Sửa', 'Sắp xếp Mã NV', 'Sắp xếp Họ Tên', 'Sắp xếp Lương'
    	String[] columns = {							
            "Mã NV", "Họ Tên", "Phòng", 
            "Lương Cứng", "Hệ Số", "Thưởng", 
            "Đi Trễ", "Tiền Phạt", "Thực Lĩnh" 
        };
        model = new DefaultTableModel(columns, 0);
        table.setModel(model);

        List<NhanVien> list = dao.layDanhSachNhanVien(orderBy);

        for (NhanVien nv : list) {
            Vector<Object> row = new Vector<>();
            row.add(nv.getMaNV());
            row.add(nv.getHoTen());
            row.add(nv.getMaPB());
            row.add(String.format("%,d", nv.getLuongCoBan())); 
            row.add(nv.getHeSoLuong());
            row.add(String.format("%,d", nv.getTienThuong()));
            row.add(nv.getSoNgayDiTre() + " ngày");
            row.add(String.format("%,d", nv.getTienPhat()));
            row.add(String.format("%,d", nv.getThucLinh()));
            model.addRow(row);
        }
    }

    public static void main(String[] args) {							// Hàm main - Việt
        FormDangNhap loginScreen = new FormDangNhap();
        loginScreen.setVisible(true);            
        loginScreen.setLocationRelativeTo(null); 
    }
}