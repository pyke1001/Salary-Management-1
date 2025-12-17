package quanlyluong;
																		// View - Việt
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class NhanVienUI extends JFrame {

    protected JTable table;
    protected DefaultTableModel model;
    protected JTextField txtNgayTre;
    protected JLabel lblTre;
    protected JButton btnThem, btnSua, btnXoa, btnPhat, btnLoad, btnMoTinhLuong;
    
    protected JLabel lblMa, lblTen, lblPhong, lblLuong, lblHS, lblSort;
    protected JButton btnLamMoi, btnTangLuong, btnThongKe;
    protected JButton btnTimKiem, btnQuanLyTK;
    protected JButton btnSortMa, btnSortTen, btnSortLuong;
    
    protected JTextField txtMaNV;
    protected JTextField txtHoTen;
    protected JTextField txtPhongBan;
    protected JTextField txtLuongCoBan;
    protected JTextField txtHeSo;

    private static final long serialVersionUID = 2L;

    public NhanVienUI() {											// Hàm khởi tạo
        initUI();
    }

    protected void initUI() {										// Hàm 'Hiển thị'

        setTitle("Phần mềm Quản lý Nhân sự & Tiền lương Konami Enterprise");                // Khung
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        
        lblMa = new JLabel("Mã NV:");                               // Thùng chứa 'Mã NV'
        lblMa.setBounds(20, 16, 80, 25);
        getContentPane().add(lblMa);
        txtMaNV = new JTextField();
        txtMaNV.setBounds(80, 16, 100, 25);
        getContentPane().add(txtMaNV);

        lblTen = new JLabel("Họ Tên:");                             // Thùng chứa 'Họ Tên'
        lblTen.setBounds(200, 16, 80, 25);
        getContentPane().add(lblTen);
        txtHoTen = new JTextField();
        txtHoTen.setBounds(260, 16, 150, 25);
        getContentPane().add(txtHoTen);

        lblPhong = new JLabel("Phòng:");                            // Thùng chứa 'Phòng'
        lblPhong.setBounds(20, 51, 80, 25);
        getContentPane().add(lblPhong);
        txtPhongBan = new JTextField();
        txtPhongBan.setBounds(80, 51, 100, 25);
        getContentPane().add(txtPhongBan);

        lblLuong = new JLabel("Lương:");                            // Thùng chứa 'Lương'
        lblLuong.setBounds(200, 55, 80, 25);
        getContentPane().add(lblLuong);
        txtLuongCoBan = new JTextField();
        txtLuongCoBan.setBounds(260, 55, 150, 25);
        getContentPane().add(txtLuongCoBan);

        lblHS = new JLabel("Hệ số:");                               // Thùng chứa 'Hệ số'
        lblHS.setBounds(419, 55, 50, 25);
        getContentPane().add(lblHS);
        txtHeSo = new JTextField();
        txtHeSo.setBounds(463, 55, 50, 25);
        getContentPane().add(txtHeSo);

        btnThem = new JButton("➕ Thêm");                          	// Nút 'Thêm'
        btnThem.setBounds(490, 16, 100, 30);
        btnThem.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnThem);
        
        btnSua = new JButton("✏️ Sửa");                            	// Nút 'Sửa'
        btnSua.setBounds(600, 16, 100, 30);
        btnSua.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnSua);
        
        btnXoa = new JButton("🗑️ Xóa");                            	// Nút 'Xóa'
        btnXoa.setBounds(710, 16, 100, 30);
        btnXoa.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnXoa);
        
        btnLamMoi = new JButton("🔄 Làm Mới");                      	// Nút 'Làm Mới'
        btnLamMoi.setBounds(669, 58, 100, 30);
        btnLamMoi.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnLamMoi);
       
        btnTimKiem = new JButton("🔍 Tìm Kiếm");                    	// Nút 'Tìm Kiếm'
        btnTimKiem.setBounds(545, 58, 114, 30);
        btnTimKiem.setBackground(Color.YELLOW);   
        getContentPane().add(btnTimKiem);

        btnSortMa = new JButton("Mã NV");							// Nút 'Sắp xếp Mã NV'
        btnSortMa.setBounds(100, 85, 80, 20);
        btnSortMa.setFont(new Font("Arial", Font.PLAIN, 10));
        getContentPane().add(btnSortMa);

        btnSortTen = new JButton("Họ Tên");                         // Nút 'Sắp xếp Họ Tên'
        btnSortTen.setBounds(190, 85, 80, 20);
        btnSortTen.setFont(new Font("Arial", Font.PLAIN, 10));
        getContentPane().add(btnSortTen);

        btnSortLuong = new JButton("Lương");                        // Nút 'Sắp xếp Lương'
        btnSortLuong.setBounds(280, 85, 80, 20);
        btnSortLuong.setFont(new Font("Arial", Font.PLAIN, 10));
        getContentPane().add(btnSortLuong);
        
        btnPhat = new JButton("⚠️ Cập nhật Phạt");                 	// Nút 'Cập nhật Phạt'                                          
        btnPhat.setBounds(210, 420, 175, 30);                                                                   
        btnPhat.setFont(new Font("Dialog", Font.BOLD, 14));
        getContentPane().add(btnPhat);                      
        
        btnTangLuong = new JButton("💰 Tăng lương");                	// Nút 'Tăng lương'
        btnTangLuong.setFont(new Font("Dialog", Font.BOLD, 14));
        btnTangLuong.setBounds(400, 420, 150, 30);
        getContentPane().add(btnTangLuong);
        
        btnMoTinhLuong = new JButton("📋 Mở Bảng Lương");           	// Nút 'Mở Bảng Lương'
        btnMoTinhLuong.setFont(new Font("Dialog", Font.BOLD, 14));
        btnMoTinhLuong.setBounds(570, 420, 175, 30); 
        getContentPane().add(btnMoTinhLuong);
        
        btnThongKe = new JButton("📊 Thống Kê");                    	// Nút 'Thống Kê'
        btnThongKe.setBounds(570, 460, 175, 30); 
        btnThongKe.setFont(new Font("Dialog", Font.BOLD, 14));
        getContentPane().add(btnThongKe);
        
        btnQuanLyTK = new JButton("🔐 Quản lý TK");					// Nút 'Quản lý TK'
        btnQuanLyTK.setBounds(15, 461, 142, 30);
        btnQuanLyTK.setFont(new Font("Dialog", Font.BOLD, 14));
        btnQuanLyTK.setBackground(Color.PINK);
        btnQuanLyTK.setVisible(false);
        getContentPane().add(btnQuanLyTK);

        btnLoad = new JButton("📂 Tải danh sách");              		// Nút 'Tải danh sách'
        btnLoad.setBounds(10, 500, 815, 40);
        btnLoad.setFont(new Font("Dialog", Font.BOLD, 16));
        getContentPane().add(btnLoad);
        
        lblSort = new JLabel("Sắp xếp theo:");                     	// Nhãn 'Sắp xếp theo'
        lblSort.setBounds(10, 85, 100, 20);
        lblSort.setFont(new Font("Dialog", Font.ITALIC, 12));
        getContentPane().add(lblSort);
        
        lblTre = new JLabel("Số ngày trễ:");                       	// Nhãn 'Số ngày trễ'
        lblTre.setBounds(20, 420, 100, 30);
        getContentPane().add(lblTre);
        
        txtNgayTre = new JTextField();                            	// Ô nhập liệu 'Số ngày trễ'
        txtNgayTre.setBounds(100, 420, 100, 30);
        getContentPane().add(txtNgayTre);
        
        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cứng", "Hệ Số", "Tổng Nhận"};        // Cột
        model = new DefaultTableModel(columns, 0);                  // Model
        table = new JTable(model);                                  // Table
        
        JScrollPane sp = new JScrollPane(table);                    // Thanh cuộn
        sp.setBounds(10, 115, 815, 295); 
        getContentPane().add(sp);
    }
}