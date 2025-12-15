package quanlyluong;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import database.ConnectDB; 													  // Import class kết nối database

public class FormNhanVien extends JFrame {                                    //Tạo lớp FormNhanVien thừa hưởng các đặc điểm của JFrame.

    JTable table;
    DefaultTableModel model;                                                   
    JTextField txtNgayTre;                                                    
    JLabel lblTre;
    JButton btnThem, btnSua, btnXoa, btnPhat, btnLoad, btnMoTinhLuong;
    
    // Khai báo thêm các biến này ra ngoài để xử lý ẩn/hiện
    private JLabel lblMa, lblTen, lblPhong, lblLuong, lblHS;
    private JButton btnLamMoi, btnTangLuong, btnThongKe;
    																		  //Khai báo biến
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JTextField txtLuongCoBan;
    private JTextField txtHeSo;

    public FormNhanVien() {

        setTitle("Quản Lý Nhân Viên - VKU");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                     	 //Bắt buộc phải có dòng này.
        setLocationRelativeTo(null);              								
        getContentPane().setLayout(null);          								 //Absolute Layout!
        
        //Tạo ô nhập liệu - Việt
        
        // Hàng 1: Mã NV + Họ Tên
        lblMa = new JLabel("Mã NV:");
        lblMa.setBounds(20, 20, 80, 25);
        getContentPane().add(lblMa);
        txtMaNV = new JTextField();
        txtMaNV.setBounds(80, 20, 100, 25);
        getContentPane().add(txtMaNV);

        lblTen = new JLabel("Họ Tên:");
        lblTen.setBounds(200, 20, 80, 25);
        getContentPane().add(lblTen);
        txtHoTen = new JTextField();
        txtHoTen.setBounds(260, 20, 150, 25);
        getContentPane().add(txtHoTen);

        // Hàng 2: Phòng ban + Lương Cứng
        lblPhong = new JLabel("Phòng:");
        lblPhong.setBounds(20, 60, 80, 25);
        getContentPane().add(lblPhong);
        txtPhongBan = new JTextField();
        txtPhongBan.setBounds(80, 60, 100, 25);
        getContentPane().add(txtPhongBan);

        lblLuong = new JLabel("Lương:");
        lblLuong.setBounds(200, 60, 80, 25);
        getContentPane().add(lblLuong);
        txtLuongCoBan = new JTextField();
        txtLuongCoBan.setBounds(260, 60, 150, 25);
        getContentPane().add(txtLuongCoBan);

        // Hàng 3: Hệ số
        lblHS = new JLabel("Hệ số:");
        lblHS.setBounds(430, 60, 50, 25);
        getContentPane().add(lblHS);
        txtHeSo = new JTextField();
        txtHeSo.setBounds(480, 60, 50, 25);
        getContentPane().add(txtHeSo);

        //Thao tác cơ bản - Việt
        
        // Nút THÊM
        btnThem = new JButton("➕ Thêm"); 
        btnThem.setBounds(430, 15, 100, 30);
        btnThem.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnThem);
        
        // Nút SỬA
        btnSua = new JButton("✏️ Sửa"); 
        btnSua.setBounds(540, 15, 100, 30);
        btnSua.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnSua);
        
        // Nút XÓA
        btnXoa = new JButton("🗑️ Xóa"); 
        btnXoa.setBounds(650, 15, 100, 30);
        btnXoa.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnXoa);
        
        // Nút LÀM MỚI - Tùng
        btnLamMoi = new JButton("🔄 Làm mới");
        btnLamMoi.setBounds(650, 56, 100, 30); // Chỉnh tọa độ cho nằm cạnh nút Xóa
        btnLamMoi.setFont(new Font("Dialog", Font.BOLD, 12));
        getContentPane().add(btnLamMoi);

        // Bảng dữ liệu - Việt

        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cứng", "Hệ Số", "Tổng Nhận"};     //Tạo tên cột 
        model = new DefaultTableModel(columns, 0);                 									 //Tạo model - cái não của bảng 
        table = new JTable(model);                             										 //Kẻ bảng
        
        JScrollPane sp = new JScrollPane(table); 													 //Tạo thanh cuộn
        sp.setBounds(10, 110, 815, 300);
        getContentPane().add(sp);

        // Xử lí sự kiện: Click vào bảng - Việt
        
        // Ý tưởng: Khi bấm vào một dòng trong bảng, dữ liệu sẽ nhảy lên các ô nhập
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow(); 													 // Lấy dòng đang chọn
                if (row >= 0) {
                    txtMaNV.setText(table.getValueAt(row, 0).toString());
                    txtHoTen.setText(table.getValueAt(row, 1).toString());
                    txtPhongBan.setText(table.getValueAt(row, 2).toString());
                    
                    // Xử lý chuỗi tiền tệ (bỏ dấu phẩy) để tránh lỗi khi sửa
                    String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "");
                    txtLuongCoBan.setText(luongStr);
                    
                    txtHeSo.setText(table.getValueAt(row, 4).toString());
                    
                    txtMaNV.setEditable(false);  													  //Khóa ô Mã NV lại
                }
            }
        });

        // Xử lí sự kiện: Thêm, Sửa, Xóa - Việt

        // Code nút THÊM
        btnThem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 1. Kiểm tra rỗng
                if (txtMaNV.getText().equals("") || txtHoTen.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                    return;
                }
                // 2. Kết nối và Thêm vào SQL
                try {
                    Connection conn = ConnectDB.getConnection();
                    String sql = "INSERT INTO NhanVien (MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstm = conn.prepareStatement(sql);
                    
                    pstm.setString(1, txtMaNV.getText());
                    pstm.setString(2, txtHoTen.getText());
                    pstm.setString(3, txtPhongBan.getText());
                    pstm.setDouble(4, Double.parseDouble(txtLuongCoBan.getText()));
                    pstm.setDouble(5, Double.parseDouble(txtHeSo.getText()));

                    pstm.executeUpdate(); 																		// Thực thi lệnh thêm
                    
                    JOptionPane.showMessageDialog(null, "✅ Thêm thành công!");
                    loadDataFromSQL(); 																			// Tải lại bảng ngay lập tức
                    
                    // Reset ô nhập về trắng
                    txtMaNV.setText("");
                    txtHoTen.setText("");
                    txtLuongCoBan.setText("");
                    txtMaNV.setEditable(true); 																	// Mở lại khóa mã NV
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi: Mã nhân viên trùng hoặc sai định dạng số!");
                    ex.printStackTrace();
                }
            }
        });

        // Code nút SỬA
        btnSua.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtMaNV.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên để sửa!");
                    return;
                }
                try {
                    Connection conn = ConnectDB.getConnection();
                    String sql = "UPDATE NhanVien SET HoTen=?, MaPB=?, LuongCoBan=?, HeSoLuong=? WHERE MaNV=?";
                    PreparedStatement pstm = conn.prepareStatement(sql);
                    
                    pstm.setString(1, txtHoTen.getText());
                    pstm.setString(2, txtPhongBan.getText());
                    pstm.setDouble(3, Double.parseDouble(txtLuongCoBan.getText()));
                    pstm.setDouble(4, Double.parseDouble(txtHeSo.getText()));
                    pstm.setString(5, txtMaNV.getText());

                    pstm.executeUpdate();
                    JOptionPane.showMessageDialog(null, "✅ Cập nhật thành công!");
                    loadDataFromSQL();
                    txtMaNV.setText(""); 																		 // Reset
                    txtMaNV.setEditable(true);
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi khi sửa: " + ex.getMessage());
                }
            }
        });

        // Code nút XÓA
        btnXoa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtMaNV.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
                    return;
                }
                int hoi = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa nhân viên này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
                if (hoi != JOptionPane.YES_OPTION) return;

                try {
                    Connection conn = ConnectDB.getConnection();
                    String sql = "DELETE FROM NhanVien WHERE MaNV=?";
                    PreparedStatement pstm = conn.prepareStatement(sql);
                    pstm.setString(1, txtMaNV.getText());
                    pstm.executeUpdate();
                    
                    JOptionPane.showMessageDialog(null, "✅ Đã xóa thành công!");
                    loadDataFromSQL();
                    txtMaNV.setText("");
                    txtHoTen.setText("");
                    txtMaNV.setEditable(true);
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi: Không thể xóa (Có thể do dính dữ liệu lương/thưởng)");
                }
            }
        });
        
        // Xử lí sự kiện: Làm mới - Tùng 
        
        // Code nút LÀM MỚI
        btnLamMoi.addActionListener(e -> {
            
            txtMaNV.setText("");
            txtHoTen.setText("");
            txtPhongBan.setText("");
            txtLuongCoBan.setText("");
            txtHeSo.setText("");
            txtMaNV.setEditable(true);
            table.clearSelection();
        });

        // Chức năng Phạt - Việt
        lblTre = new JLabel("Số ngày trễ:");
        lblTre.setBounds(20, 420, 100, 30);
        getContentPane().add(lblTre);
        
        txtNgayTre = new JTextField();  
        txtNgayTre.setBounds(100, 420, 100, 30);
        getContentPane().add(txtNgayTre);

        btnPhat = new JButton("⚠️ Cập nhật Phạt");             														
        btnPhat.setBounds(210, 420, 175, 30);                    													
        btnPhat.setFont(new Font("Dialog", Font.BOLD, 14));
        getContentPane().add(btnPhat);	                   
        
        // Xử lí sự kiện: Cập nhật phạt - Việt
        btnPhat.addActionListener(new ActionListener() {                   
            public void actionPerformed(ActionEvent e) {
                try {                 																	//Nếu try lỗi thì nhảy xuống catch
                    int selectedRow = table.getSelectedRow();        									//Check xem người dùng chọn dòng số mấy
                    if (selectedRow == -1) {   //Trường hợp chưa chọn.
                        JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần phạt!");
                        return;   
                    }
                    String maNV = table.getValueAt(selectedRow, 0).toString();          
                    String strNgayTre = txtNgayTre.getText();                  
                    if (strNgayTre.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Vui lòng nhập số ngày trễ!");
                        return;
                    }
                    int soNgayTre = Integer.parseInt(strNgayTre);       								//Biến đổi từ chữ sang số

                    Connection conn = ConnectDB.getConnection();    
                    String sql = "UPDATE NhanVien SET SoNgayDiTre = ? WHERE MaNV = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, soNgayTre);
                    pst.setString(2, maNV);
                    pst.executeUpdate();              												    //Chạy lệnh Update
                    conn.close();           															//Đóng cổng kết nối!

                    JOptionPane.showMessageDialog(null, "Cập nhật phạt thành công!");
                    loadDataFromSQL();            
                    txtNgayTre.setText("");         

                } catch (Exception ex) {   //Trường hợp có lỗi
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
                }
            }
        });

        // Chức năng Tăng Lương - Quốc
        btnTangLuong = new JButton("💰 Tăng lương");
        btnTangLuong.setFont(new Font("Dialog", Font.BOLD, 14));
        btnTangLuong.setBounds(400, 420, 150, 30);
        getContentPane().add(btnTangLuong);
        
        btnTangLuong.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 tangLuong();
            }
        });

        // Nút tính lương - Đồng
        btnMoTinhLuong = new JButton("📋 Mở Bảng Lương");
        btnMoTinhLuong.setFont(new Font("Dialog", Font.BOLD, 14));
        btnMoTinhLuong.setBounds(570, 420, 175, 30); 
        getContentPane().add(btnMoTinhLuong);
        
        btnMoTinhLuong.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GiaoDienChinh cuaSoTinhLuong = new GiaoDienChinh();
                cuaSoTinhLuong.setVisible(true);
                cuaSoTinhLuong.setLocationRelativeTo(null);
            }
        });

        // Nút thống kê - Hướng
        btnThongKe = new JButton("📊 Thống Kê");
        btnThongKe.setBounds(570, 460, 175, 30); 
        btnThongKe.setFont(new Font("Dialog", Font.BOLD, 14));
        getContentPane().add(btnThongKe);

        btnThongKe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                FormThongKe fr = new FormThongKe();
                fr.setVisible(true);
            }
        });

        // Nút tải danh sách - Việt (Nút to ở dưới cùng)
        btnLoad = new JButton("📂 Tải danh sách từ SQL");
        btnLoad.setBounds(10, 500, 815, 40);
        btnLoad.setFont(new Font("Dialog", Font.BOLD, 16));
        getContentPane().add(btnLoad);
        
        // Ẩn giao diện ban đầu
        setHienThi(false);
        
        btnLoad.addActionListener(e -> {
            // Hiện lại giao diện khi bấm Load
            setHienThi(true);
            loadDataFromSQL();
        });
    }

    private void setHienThi(boolean hien) {
        // Ẩn/Hiện Label
        lblMa.setVisible(hien); lblTen.setVisible(hien); lblPhong.setVisible(hien);
        lblLuong.setVisible(hien); lblHS.setVisible(hien); lblTre.setVisible(hien);
        
        // Ẩn/Hiện Text
        txtMaNV.setVisible(hien); txtHoTen.setVisible(hien); txtPhongBan.setVisible(hien);
        txtLuongCoBan.setVisible(hien); txtHeSo.setVisible(hien); txtNgayTre.setVisible(hien);
        
        // Ẩn/Hiện Button
        btnThem.setVisible(hien); btnSua.setVisible(hien); btnXoa.setVisible(hien);
        btnLamMoi.setVisible(hien); btnPhat.setVisible(hien); btnTangLuong.setVisible(hien);
        btnMoTinhLuong.setVisible(hien); btnThongKe.setVisible(hien);
    }

        // Hàm tăng lương - Quốc
    private void tangLuong() {
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
            if (percent <= 0) {
                JOptionPane.showMessageDialog(null, "Phần trăm phải > 0");
                return;
            }
            Connection conn = ConnectDB.getConnection();
            String sql = "UPDATE NhanVien SET LuongCoBan = LuongCoBan * (1 + ? / 100) WHERE MaNV = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, percent);
            ps.setString(2, maNV);
            int kq = ps.executeUpdate();
            conn.close();

            if (kq > 0) {
                JOptionPane.showMessageDialog(null, "Tăng lương thành công!");
                loadDataFromSQL();
            } else {
                JOptionPane.showMessageDialog(null, "Không tìm thấy nhân viên!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
        }
    }
    		// Hàm tải lại bảng - Việt
    private void loadDataFromSQL() { 
        try {	
            String[] columns = {            																//Bỏ cái columns cũ đi, thay bằng cái mới!
                "Mã NV", "Họ Tên", "Phòng", 
                "Lương Cứng", "Hệ Số", "Thưởng", 
                "Đi Trễ", "Tiền Phạt", "Thực Lĩnh" 
            };
            model = new DefaultTableModel(columns, 0);    													//Thay model mới
            table.setModel(model);   																		//Gắn model vào bảng mới

            Connection conn = ConnectDB.getConnection();
        
            String sql = "SELECT MaNV, HoTen, MaPB, LuongCoBan, HeSoLuong, TienThuong, SoNgayDiTre, " +
                         "(SoNgayDiTre * 100000) AS TienPhat, " +
                         "((LuongCoBan * HeSoLuong) + PhuCap + TienThuong - (SoNgayDiTre * 100000)) AS ThucLinh " +
                         "FROM NhanVien";
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("MaNV"));
                row.add(rs.getString("HoTen"));
                row.add(rs.getString("MaPB"));
                // Định dạng tiền tệ đơn giản
                row.add(String.format("%,d", rs.getLong("LuongCoBan"))); 
                row.add(rs.getFloat("HeSoLuong"));
                row.add(String.format("%,d", rs.getLong("TienThuong")));
                row.add(rs.getInt("SoNgayDiTre") + " ngày");
                row.add(String.format("%,d", rs.getLong("TienPhat")));
                row.add(String.format("%,d", rs.getLong("ThucLinh")));
                
                model.addRow(row);
            }
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {     															//Hàm main đây rồi
        new FormNhanVien().setVisible(true);
    }
}