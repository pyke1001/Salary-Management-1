/*1 điều lưu ý cho File này:
 Ở dòng code JOptionPane.showMessageDialog(this, chiTiet); JOptionPane là công cụ của javax.swing.*, showMessageDialog(...) sẽ hiển thị thông báo cùng nút OK.
 Tham số this nghĩa là chỉ vào cái cửa sổ hiện tại (Tính Lương Nhân Viên), nghĩa là nó sẽ hiện thông báo ngay chính giữa CỬA SỔ.
 Nếu ta thay this bằng null thì nó sẽ hiện thông báo ngay chính giữa MÀN HÌNH. (Nghe có vẻ vô dụng :\ )
 
 Nhiệm vụ của Đồng là tìm và sửa chỗ null này thành this nhé, sau đó Commit lên Github để t kiểm tra xem hoạt động tốt không.
 */
//Cửa sổ tính lương - Đồng/Tùng
package quanlyluong;

import javax.swing.*;
import java.awt.*;               // Để dùng FlowLayout
public class GiaoDienChinh extends JFrame {
    
    private JTextField txtLuongMotGio;
    private JTextField txtGioLamChuan;
    private JTextField txtGioTangCa;                       // Khai báo biến
    private JTextField txtHeSoTangCa;
    
    private JCheckBox chkNghiThaiSan;
    private JTextArea txtKetQua;
    private JButton btnTinhLuong;

    public GiaoDienChinh() {
        // 1. Cài đặt cửa sổ
        setTitle("Tính Lương Nhân Viên");
        setSize(1000, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  	//Cái này khác EXIT_ON_CLOSE nhé.
        setLayout(new FlowLayout());
        setLocationRelativeTo(null); 

        // 2. Khởi tạo các thành phần (Phải có đoạn này mới chạy được)
        add(new JLabel("Lương 1 giờ:"));
        txtLuongMotGio = new JTextField(10);
        add(txtLuongMotGio);

        add(new JLabel("Giờ làm chuẩn:"));
        txtGioLamChuan = new JTextField(10);
        add(txtGioLamChuan);

        add(new JLabel("Giờ tăng ca:"));
        txtGioTangCa = new JTextField(10);
        add(txtGioTangCa);

        add(new JLabel("Hệ số tăng ca:"));
        txtHeSoTangCa = new JTextField(10);
        add(txtHeSoTangCa);

        chkNghiThaiSan = new JCheckBox("Nghỉ thai sản?");
        add(chkNghiThaiSan);

        btnTinhLuong = new JButton("🧮 Tính Lương");
        btnTinhLuong.setFont(new Font("Dialog",Font.BOLD, 14));
        add(btnTinhLuong);

        txtKetQua = new JTextArea(5, 30);
        add(new JScrollPane(txtKetQua));

        // 3. Gắn sự kiện bấm nút
        btnTinhLuong.addActionListener(e -> xuLyTinhLuong());
        
        // Hiển thị lên
        setVisible(true);
    }
    
    private void xuLyTinhLuong() {
        try {
            			//Lấy dữ liệu từ người dùng: getText()
            double luong1Gio = Double.parseDouble(txtLuongMotGio.getText());
            double gioChuan  = Double.parseDouble(txtGioLamChuan.getText());
            double gioTangCa = Double.parseDouble(txtGioTangCa.getText());
            double heSo      = Double.parseDouble(txtHeSoTangCa.getText());
            
            			//Check xem có bầu không ?
            boolean dangNghiThaiSan = chkNghiThaiSan.isSelected();

            			//Gọi thằng Máy Tính Tiền Lương ở file bên kia ra.
            double tongLuongGross = MayTinhTienLuong.tinhTongLuong(luong1Gio, gioChuan, gioTangCa, heSo, dangNghiThaiSan);

            			//Tính tiền thuế - Tùng
            int soNguoiPhuThuoc = 0; 
            double tienBaoHiem = CongCuThue.tinhBaoHiem(tongLuongGross);
            double tienThue = CongCuThue.tinhThueTNCN(tongLuongGross, soNguoiPhuThuoc);

            			//Thực Lĩnh (Tức tổng tiền đó)
            double thucLinh = tongLuongGross - tienBaoHiem - tienThue;
            
            //In ra màn hình
            String chiTiet = String.format(           //Tìm hiểu format, hiểu đơn giản là lưu cái đống ở dưới vào biến chiTiet.
                "Tổng lương (Gross): %,.0f VNĐ\n" +
                "Bảo hiểm (10.5%%): -%,.0f VNĐ\n" +
                "Thuế TNCN: -%,.0f VNĐ\n" +
                "------------------------\n" +
                "THỰC LĨNH: %,.0f VNĐ",
                tongLuongGross, tienBaoHiem, tienThue, thucLinh
            );
            JOptionPane.showMessageDialog(null, chiTiet); 		//Lôi đầu thằng chiTiet ra để thông báo số tiền!

            // Ghi số tiền thực lĩnh vào ô kết quả (cho người dùng copy nếu cần)
            txtKetQua.setText(String.format("%,.0f VNĐ", thucLinh));

        } catch (NumberFormatException ex) {
            // Bắt lỗi nếu người dùng nhập chữ thay vì số, hoặc để trống
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các ô và đúng định dạng số!");
        }
    }

    // Hàm main để chạy thử giao diện này luôn
    public static void main(String[] args) {
        new GiaoDienChinh();
    }
}