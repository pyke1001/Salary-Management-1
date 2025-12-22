package ui;
                                                                    // Giao diện Admin - Việt
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox; // <--- Thay JTextField bằng JComboBox
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class NhanVienUI extends JFrame {
	
	protected String lastMa = "";
	protected String lastTen = "";
	protected String lastPhong = "";
	protected String lastLuong = "";
	protected String taiKhoanHienTai;
	protected String quyenHienTai;
	protected String lastHeSo = "";
	protected java.util.Set<String> secretsFound = new java.util.HashSet<>();
	protected boolean isNeonUnlocked = false;
	protected boolean isNeonActive = false;

	protected JLabel lblContraHint;
	protected JLabel lblSnake;
	protected JLabel lblNeon;

    protected JTable table;
    protected DefaultTableModel model;
    protected JButton btnThem, btnSua, btnXoa, btnPhat, btnLoad, btnMoTinhLuong;
    
    protected JLabel lblMa, lblTen, lblPhong, lblLuong, lblHS, lblSort;
    protected JButton btnLamMoi, btnTangLuong, btnThongKe, btnBaoLoi, btnChamCongLe;
    protected JButton btnTimKiem, btnQuanLyTK, btnGiamLuong, btnChotThang, btnXuatExcel, btnThuongNong;
    protected JButton btnSortMa, btnSortTen, btnSortLuong, btnLichSu, btnKhoiPhuc;
    
    protected JTextField txtMaNV;
    protected JTextField txtHoTen;
    
    protected JComboBox<String> cboPhongBan;
    protected JComboBox<String> cboLuongCoBan;
    protected JComboBox<String> cboHeSo;
    // ----------------------------------------------------

    private static final long serialVersionUID = 2L;

    public NhanVienUI() {                                           // Hàm khởi tạo
        initUI();
    }

    protected void initUI() {                                       // Hàm 'Hiển thị'

        setTitle("Phần mềm Quản lý Nhân sự & Tiền lương Konami Enterprise");
        setSize(1000, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(new Color(240, 242, 245));

        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(null);
        pnlHeader.setBackground(new Color(0, 102, 204));
        pnlHeader.setBounds(0, 0, 1000, 50);
        getContentPane().add(pnlHeader);
        
        JLabel lblTieuDe = new JLabel("HỆ THỐNG QUẢN TRỊ NHÂN SỰ");
        lblTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTieuDe.setForeground(Color.WHITE);
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        lblTieuDe.setBounds(0, 0, 1000, 50);
        pnlHeader.add(lblTieuDe);

        JPanel pnlInput = new JPanel();
        pnlInput.setLayout(null);
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBounds(10, 60, 965, 100);
        TitledBorder border = javax.swing.BorderFactory.createTitledBorder(null, " Thông Tin Nhân Viên ", 
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 13), new Color(0, 102, 204));
        pnlInput.setBorder(border);
        getContentPane().add(pnlInput);
        
        // --- HÀNG 1 ---
        lblMa = new JLabel("Mã NV:");
        lblMa.setBounds(20, 25, 60, 25);
        lblMa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(lblMa);
        txtMaNV = new JTextField();
        txtMaNV.setBounds(80, 25, 120, 25);
        txtMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(txtMaNV);

        lblTen = new JLabel("Họ Tên:");
        lblTen.setBounds(210, 25, 46, 25);
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(lblTen);
        txtHoTen = new JTextField();
        txtHoTen.setBounds(259, 25, 120, 25);
        txtHoTen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(txtHoTen);

        lblPhong = new JLabel("Phòng:");
        lblPhong.setBounds(389, 25, 46, 25);
        lblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(lblPhong);
        
        // CBO PHÒNG BAN (Editable = True để nhập "Khác")
        cboPhongBan = new JComboBox<>();
        cboPhongBan.setEditable(true); // Cho phép nhập tay nếu không có trong danh sách
        cboPhongBan.setBounds(445, 25, 132, 25);
        cboPhongBan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(cboPhongBan);

        // --- HÀNG 2 ---
        lblLuong = new JLabel("Lương:");
        lblLuong.setBounds(20, 60, 60, 25);
        lblLuong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(lblLuong);
        
        // CBO LƯƠNG (Các mốc lương phổ biến)
        String[] mocLuong = {"Thỏa thuận...", "5,000,000", "7,000,000", "10,000,000", "15,000,000", "20,000,000", "30,000,000"};
        cboLuongCoBan = new JComboBox<>(mocLuong);
        cboLuongCoBan.setEditable(true); // Nhập số lẻ thoải mái
        cboLuongCoBan.setBounds(80, 60, 120, 25);
        cboLuongCoBan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(cboLuongCoBan);

        lblHS = new JLabel("Hệ số:");
        lblHS.setBounds(217, 60, 39, 25);
        lblHS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(lblHS);
        
        // CBO HỆ SỐ (Các mốc bằng cấp)
        String[] mocHeSo = {"1.0 (Thử việc)", "1.86 (Trung cấp)", "2.10 (Cao đẳng)", "2.34 (Đại học)", "2.67 (Thạc sĩ)"};
        cboHeSo = new JComboBox<>(mocHeSo);
        cboHeSo.setEditable(true);
        cboHeSo.setBounds(259, 60, 120, 25);
        cboHeSo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlInput.add(cboHeSo);

        // --- CÁC NÚT CHỨC NĂNG (GIỮ NGUYÊN) ---
        btnThem = new JButton("➕ Thêm");
        btnThem.setBounds(650, 60, 95, 30);
        btnThem.setFont(new Font("Dialog", Font.BOLD, 12));
        btnThem.setBackground(new Color(76, 175, 80));
        btnThem.setForeground(Color.WHITE);
        pnlInput.add(btnThem);
        
        btnSua = new JButton("✏️ Sửa");
        btnSua.setBounds(750, 60, 95, 30);
        btnSua.setFont(new Font("Dialog", Font.BOLD, 12));
        btnSua.setBackground(new Color(33, 150, 243));
        btnSua.setForeground(Color.WHITE);
        pnlInput.add(btnSua);
        
        btnXoa = new JButton("🗑️ Xóa");
        btnXoa.setBounds(850, 60, 95, 30);
        btnXoa.setFont(new Font("Dialog", Font.BOLD, 12));
        btnXoa.setBackground(new Color(244, 67, 54));
        btnXoa.setForeground(Color.WHITE);
        pnlInput.add(btnXoa);
        
        btnLamMoi = new JButton("🔄 Làm Mới");
        btnLamMoi.setBounds(780, 22, 120, 30);
        btnLamMoi.setFont(new Font("Dialog", Font.BOLD, 12));
        btnLamMoi.setBackground(new Color(224, 224, 224));
        pnlInput.add(btnLamMoi);
        
        btnTimKiem = new JButton("🔍 Tìm Kiếm");
        btnTimKiem.setBounds(650, 22, 120, 30);
        btnTimKiem.setBackground(new Color(255, 193, 7));   
        btnTimKiem.setFont(new Font("Dialog", Font.BOLD, 12));
        pnlInput.add(btnTimKiem);

        // --- CÁC NÚT SẮP XẾP & CHỨC NĂNG DƯỚI ---
        btnSortMa = new JButton("Mã NV");
        btnSortMa.setBounds(160, 165, 80, 25);
        btnSortMa.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        getContentPane().add(btnSortMa);

        btnSortTen = new JButton("Họ Tên");
        btnSortTen.setBounds(250, 165, 80, 25);
        btnSortTen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        getContentPane().add(btnSortTen);

        btnSortLuong = new JButton("Lương");
        btnSortLuong.setBounds(340, 165, 80, 25);
        btnSortLuong.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        getContentPane().add(btnSortLuong);
        
        btnPhat = new JButton("⚠️ Cập nhật Phạt");                                           
        btnPhat.setBounds(165, 570, 145, 35);                                                                                                   
        btnPhat.setFont(new Font("Dialog", Font.BOLD, 12));
        btnPhat.setBackground(new Color(233, 30, 99));
        btnPhat.setForeground(Color.WHITE);
        getContentPane().add(btnPhat);                      
        
        btnTangLuong = new JButton("💰 Tăng Lương");
        btnTangLuong.setFont(new Font("Dialog", Font.BOLD, 12));
        btnTangLuong.setBounds(320, 570, 145, 35);
        getContentPane().add(btnTangLuong);
        
        btnGiamLuong = new JButton("💸 Giảm Lương");
        btnGiamLuong.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });
        btnGiamLuong.setFont(new Font("Dialog", Font.BOLD, 12));
        btnGiamLuong.setBounds(475, 570, 145, 35); 
        getContentPane().add(btnGiamLuong);
        
        btnMoTinhLuong = new JButton("💰 Phiếu Lương");
        btnMoTinhLuong.setFont(new Font("Dialog", Font.BOLD, 12));
        btnMoTinhLuong.setBounds(630, 570, 145, 35); 
        btnMoTinhLuong.setBackground(new Color(156, 39, 176));
        btnMoTinhLuong.setForeground(Color.WHITE);
        getContentPane().add(btnMoTinhLuong);
        
        btnThongKe = new JButton("📊 Thống Kê");
        btnThongKe.setBounds(320, 620, 145, 35); 
        btnThongKe.setFont(new Font("Dialog", Font.BOLD, 12));  
        getContentPane().add(btnThongKe);
        
        btnQuanLyTK = new JButton("🔐 Quản lý TK");
        btnQuanLyTK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });
        btnQuanLyTK.setBounds(10, 620, 145, 35);
        btnQuanLyTK.setFont(new Font("Dialog", Font.BOLD, 12));
        btnQuanLyTK.setBackground(Color.PINK);
        btnQuanLyTK.setVisible(false);
        getContentPane().add(btnQuanLyTK);

        btnLoad = new JButton("📂 Tải danh sách");
        btnLoad.setBounds(800, 165, 175, 25);
        btnLoad.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnLoad);
        
        btnBaoLoi = new JButton("⚠️ Báo Lỗi");
        btnBaoLoi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });
        btnBaoLoi.setFont(new Font("Dialog", Font.BOLD, 12));
        btnBaoLoi.setForeground(Color.RED);
        btnBaoLoi.setBounds(830, 620, 145, 35); 
        getContentPane().add(btnBaoLoi);
        
        btnChotThang = new JButton("🔄 Chốt Tháng");
        btnChotThang.setFont(new Font("Dialog", Font.BOLD, 12));
        btnChotThang.setBackground(new Color(255, 99, 71));
        btnChotThang.setBounds(165, 620, 145, 35);
        btnChotThang.setForeground(Color.WHITE);
        getContentPane().add(btnChotThang);

        btnXuatExcel = new JButton("📊 Xuất Excel");
        btnXuatExcel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });
        btnXuatExcel.setFont(new Font("Dialog", Font.BOLD, 12));
        btnXuatExcel.setBackground(new Color(60, 179, 113));
        btnXuatExcel.setBounds(475, 620, 145, 35);
        btnXuatExcel.setForeground(Color.WHITE);
        getContentPane().add(btnXuatExcel);
            
        btnThuongNong = new JButton("💰 Thưởng Nóng");
        btnThuongNong.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {}
        });
        btnThuongNong.setFont(new Font("Dialog", Font.BOLD, 12));
        btnThuongNong.setBounds(10, 570, 145, 35);
        btnThuongNong.setBackground(new Color(255, 152, 0));
        getContentPane().add(btnThuongNong);
        
        btnLichSu = new JButton("🕒 Lịch Sử");
        btnLichSu.setBounds(630, 620, 145, 35); // Đặt cạnh nút Xuất Excel hoặc chỗ nào trống
        btnLichSu.setFont(new Font("Dialog", Font.BOLD, 12));
        btnLichSu.setBackground(new Color(100, 181, 246)); // Màu xanh dương nhạt
        btnLichSu.setForeground(Color.WHITE);
        getContentPane().add(btnLichSu);
        
        btnKhoiPhuc = new JButton("⟲ Khôi Phục");
        btnKhoiPhuc.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        btnKhoiPhuc.setBounds(830, 572, 145, 35);
        btnKhoiPhuc.setBackground(new Color(46, 204, 113));
        btnKhoiPhuc.setForeground(Color.WHITE);
        btnKhoiPhuc.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnKhoiPhuc);
        // ------------------------
        
        lblSort = new JLabel("Sắp xếp theo:");
        lblSort.setBounds(10, 165, 100, 25);
        lblSort.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        getContentPane().add(lblSort);
        
        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cứng", "Hệ Số", "Tổng Nhận"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model) {
            private static final long serialVersionUID = 2L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };                                  
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(0, 102, 204));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(10, 195, 965, 360); 
        getContentPane().add(sp);
        
        btnChamCongLe = new JButton("🎁 Chấm Công Lễ");
        btnChamCongLe.setBounds(615, 165, 175, 25);
        getContentPane().add(btnChamCongLe);
        btnChamCongLe.setBackground(new Color(255, 215, 0));
        btnChamCongLe.setFont(new Font("Dialog", Font.BOLD, 12));
    }
}