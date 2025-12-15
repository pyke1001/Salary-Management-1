/* Trước khi đọc code thì khuyến khích bạn làm theo các bước sau để... đỡ đau mắt:
1. Trên thanh menu của Eclipse, chọn Window -> Preferences.
2. Ở ô tìm kiếm góc trên bên trái cửa sổ mới hiện ra, gõ chữ: Spelling.
3. Bấm vào mục Spelling (thường nằm trong General -> Editors -> Text Editors).
4. Bỏ dấu tích ở ô Enable spell checking (Bật kiểm tra chính tả). Bấm Apply and Close.
Làm xong thì xóa mấy dòng này đi cho đỡ vướng mắt (Hoặc click vào dấu trừ (-) ở đầu hàng số 1 để ẩn đi). */

// Chú thích: Code của Quốc nằm ở dòng 132 trở đi.

package quanlyluong;      

import database.ConnectDB;                                      //Dòng này để liên kết với file ConnectDB.java - Dùng để tải dữ liệu từ SQL Server
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;                                                           
import java.util.Vector;                                                       //Tốt nhất là nên bỏ qua mấy cái import này D:
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FormNhanVien extends JFrame {                                    //Tạo lớp FormNhanVien thừa hưởng các đặc điểm của JFrame. Nếu thắc mắc JFrame ở đâu thì nó ở import javax.swing.* đó.

    JTable table;
    DefaultTableModel model;                                                   
    JTextField txtNgayTre;                                                   //Khai báo biến
    JButton btnPhat;
    JButton btnLoad;
    JLabel lblTre;
    JButton btnMoTinhLuong;

    public FormNhanVien() {

        setTitle("Quản Lý Nhân Viên - VKU");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                      //Bắt buộc phải có dòng này! Nếu không lúc tắt cửa sổ nó vẫn chạy ngầm làm nặng máy..
        setLocationRelativeTo(null);               //Thay vì cửa sổ hiện góc trên bên trái, nó hiện ngay giữa!
        
        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cứng", "Hệ Số", "Tổng Nhận"};     //Tạo tên cột với biến columns (Dùng ở dưới nè)    
        model = new DefaultTableModel(columns, 0);                 //Tạo cái "não" để nó chứa dữ liệu, 2 tham số trong ngoặc tương ứng với (tiêu đề, dòng trắng)	
        
        getContentPane().setLayout(null);           //Thiết lập Absolute Layout! (Dùng để kéo thả). Có cả Layout Manager, mà newbie thì không dùng.
        
        table = new JTable(model);                             //Kẻ bảng         
        btnPhat = new JButton("Cập nhật Phạt");                //Tạo nút
        btnPhat.setBounds(0, 316, 150, 30);                  //Kéo thả :D
        getContentPane().add(btnPhat);	                   //Có thể sử dụng this.add(btnPhat), nhưng nếu lúc sau đổi màu thì phải getContentPane (Tham khảo Gemini để hiểu thêm).
        btnPhat.setVisible(false);
        
        JLabel lblTre = new JLabel("Số ngày trễ:");
        lblTre.setBounds(10, 276, 100, 30);
        getContentPane().add(lblTre);
        lblTre.setVisible(false);
        txtNgayTre = new JTextField();	
        txtNgayTre.setBounds(96, 277, 100, 30);
        getContentPane().add(txtNgayTre);
        txtNgayTre.setVisible(false);
        
        btnPhat.addActionListener(new ActionListener() {                   //Xử lí sự kiện khi bấm nút 'Cập nhật phạt'
            public void actionPerformed(ActionEvent e) {
                try {                 //Nếu try lỗi thì nhảy xuống catch
                	
                    int selectedRow = table.getSelectedRow();         //Check xem người dùng chọn dòng số mấy!
                    if (selectedRow == -1) {   //Trường hợp chưa chọn.
                        JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần phạt!");
                        return;   //Phải có return để thoát khỏi sự kiện mà nhập lại từ đầu
                    }

                    String maNV = table.getValueAt(selectedRow, 0).toString();          //Check xem Mã nhân viên của đứa bị phạt.
                    String strNgayTre = txtNgayTre.getText();                  //Lấy dữ liệu từ bàn phím, xem thanh niên này trễ mấy ngày.

                    if (strNgayTre.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Vui lòng nhập số ngày trễ!");
                        return;
                    }

                    int soNgayTre = Integer.parseInt(strNgayTre);       //Biến đổi từ chữ sang số

                    Connection conn = database.ConnectDB.getConnection();    //Gọi thằng ConnectDB ra để kết nối SQL Server
                    String sql = "UPDATE NhanVien SET SoNgayDiTre = ? WHERE MaNV = ?";
                    
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, soNgayTre);
                    pst.setString(2, maNV);
                    
                    pst.executeUpdate();              //Chạy lệnh Update trên xuống Database
                    conn.close();            //Đóng cổng kết nối!

                    JOptionPane.showMessageDialog(null, "Cập nhật thành công!");
                    loadDataFromSQL();            //Đây là một hàm dùng để load lại bảng (Nằm ở dưới)
                    txtNgayTre.setText("");         //Xóa dữ liệu lúc trước đi cho tiện

                } catch (Exception ex) {   //Trường hợp có lỗi, phần này cho mình xem mà sửa.
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
                }
            }
            
        });

		        //Hiển thị Bảng và nút Load
		JScrollPane sp = new JScrollPane(table); //Tạo thanh cuộn
		sp.setBounds(0, 0, 786, 277);
		 
		getContentPane().add(sp);
		 	//Nút tải danh sách
		JButton btnMoTinhLuong = new JButton("Mở Bảng Lương");
		btnMoTinhLuong.setFont(new Font("Arial", Font.BOLD, 14));
		btnMoTinhLuong.setBounds(0, 380, 160, 25); 
		btnMoTinhLuong.setVisible(false);

		getContentPane().add(btnMoTinhLuong);
		btnMoTinhLuong.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	GiaoDienChinh cuaSoTinhLuong = new GiaoDienChinh();

		        cuaSoTinhLuong.setVisible(true);
		        cuaSoTinhLuong.setLocationRelativeTo(null); 
		    }
		});
		JButton btnLoad = new JButton("Tải danh sách từ SQL");
		btnLoad.setBounds(0, 438, 786, 25);
		btnLoad.setFont(new Font("Arial", Font.BOLD, 14));
		getContentPane().add(btnLoad);
		btnLoad.addActionListener(e -> {
			lblTre.setVisible(true);
	        txtNgayTre.setVisible(true);
	        btnPhat.setVisible(true);
	        btnMoTinhLuong.setVisible(true);
		    loadDataFromSQL();
		    
		    
		     //Nút tăng lương - Quốc
		JButton btnTangLuong = new JButton("Tăng lương");
		btnTangLuong.setFont(new Font("Arial", Font.BOLD, 14));
		btnTangLuong.setBounds(636, 318, 150, 25);
		getContentPane().add(btnTangLuong);
		
		btnTangLuong.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		             tangLuong();
		             
		             
         }
 	});        
});
	}
    private void tangLuong() {     //Hàm tăng lương - Quốc
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên!");
            return;
        }

        String maNV = model.getValueAt(row, 0).toString();

        String input = JOptionPane.showInputDialog(
                null,
                "Nhập % tăng lương:",
                "Tăng lương",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) return;

        try {
            double percent = Double.parseDouble(input);
            if (percent <= 0) {
                JOptionPane.showMessageDialog(null, "Phần trăm phải > 0");
                return;
            }

            Connection conn = database.ConnectDB.getConnection();

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
    private void loadDataFromSQL() {   //Hàm load lại bảng!
        try {
            String[] columns = {            //Bỏ cái columns cũ đi, thay bằng cái mới!
                "Mã NV", "Họ Tên", "Phòng", 
                "Lương Cứng", "Hệ Số", "Thưởng", 
                "Đi Trễ", "Tiền Phạt", "Thực Lĩnh" 
            };
            model = new DefaultTableModel(columns, 0);    //Thay "não" mới
            table.setModel(model);   //Gắn "não" vào bảng mới

            Connection conn = database.ConnectDB.getConnection();
            
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
                row.add(rs.getLong("LuongCoBan"));
                row.add(rs.getFloat("HeSoLuong"));
                row.add(rs.getLong("TienThuong"));
                row.add(rs.getInt("SoNgayDiTre") + " ngày");
                row.add(rs.getLong("TienPhat"));
                row.add(rs.getLong("ThucLinh"));
                
                model.addRow(row);
            }
            
            conn.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {     //Hàm main đây rồi

        new FormNhanVien().setVisible(true);
    }
}