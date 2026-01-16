package ui; 

import java.awt.GridLayout;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import entity.NhanVien;
import logic.ThuatToanTangLuong;
import logic.MayTinhLuong;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class XuLySuKien {
  
    private QuanLyNhanVien controller;

    public XuLySuKien(QuanLyNhanVien controller) {
        this.controller = controller;
    }

    // Xử lý logic giảm lương nhân viên
    public void xuLyGiamLuong() {
        int row = controller.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(controller, "Vui lòng chọn nhân viên cần giảm lương!", "Chưa chọn đối tượng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = controller.table.getValueAt(row, 0).toString();
        String hoTen = controller.table.getValueAt(row, 1).toString();
        float heSoCu = Float.parseFloat(controller.table.getValueAt(row, 5).toString());

        String input = JOptionPane.showInputDialog(controller, 
            "Nhập hệ số lương MỚI cho " + hoTen + ":\n(Hệ số hiện tại: " + heSoCu + ")", 
            heSoCu);
            
        if (input == null || input.trim().isEmpty()) return;

        try {
            float heSoMoi = Float.parseFloat(input);
            // Validate dữ liệu đầu vào
            if (heSoMoi <= 0) {
                JOptionPane.showMessageDialog(controller, "Hệ số lương phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (heSoMoi >= heSoCu) {
                JOptionPane.showMessageDialog(controller, "Đang giảm lương thì hệ số mới phải NHỎ HƠN hệ số cũ!", "Sai logic", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Xác nhận hành động nhạy cảm
            String msg = String.format("XÁC NHẬN KỶ LUẬT HẠ BẬC LƯƠNG\n\nNhân viên: %s\nHệ số cũ: %.2f\nHệ số mới: <font color='red'><b>%.2f</b></font>", hoTen, heSoCu, heSoMoi);
            int confirm = JOptionPane.showConfirmDialog(controller, "<html>" + msg + "</html>", "Phê Duyệt Kỷ Luật", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.dao.capNhatHeSo(maNV, heSoMoi)) {
                    controller.dao.ghiLichSu(maNV, "Hạ Bậc Lương", String.format("Hạ hệ số từ %.2f -> %.2f", heSoCu, heSoMoi), controller.taiKhoanHienTai);
                    JOptionPane.showMessageDialog(controller, "✅ Đã cập nhật hệ số lương mới!");
                    controller.loadData("NV.MaNV ASC");
                } else {
                    JOptionPane.showMessageDialog(controller, "❌ Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(controller, "Vui lòng nhập con số hợp lệ!");
        }
    }

    // Xử lý logic tăng lương dựa trên thuật toán thâm niên
    public void xuLyTangLuong() { 
        int row = controller.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(controller, "Vui lòng chọn nhân viên cần xét nâng lương!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = controller.table.getValueAt(row, 0).toString();
        String hoTen = controller.table.getValueAt(row, 1).toString();
        float heSoCu = Float.parseFloat(controller.table.getValueAt(row, 5).toString());
        
        NhanVien nvFull = controller.dao.getNhanVienTheoMa(maNV);
        // Gọi thuật toán tính toán hệ số đề xuất
        float heSoDeXuat = ThuatToanTangLuong.deXuatHeSoMoi(nvFull.getNgayVaoLam(), heSoCu);
        
        String goiY = (heSoDeXuat > heSoCu) ? " (Đã đến hạn nâng bậc!)" : " (Chưa đến hạn)";

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Xét nâng bậc lương cho: " + hoTen));
        panel.add(new JLabel("Hệ số hiện tại: " + heSoCu));
        panel.add(new JLabel("Hệ số đề xuất: " + heSoDeXuat + goiY));
        
        String[] options = { 
            "Nâng lên bậc tiếp theo (" + String.format("%.2f", heSoCu + 0.33) + ")", 
            "Nâng vượt cấp (Đặc cách +0.66)", 
            "Cập nhật theo đề xuất (" + heSoDeXuat + ")",
            "Nhập tay hệ số mới" 
        };
        JComboBox<String> cboOption = new JComboBox<>(options);
        panel.add(cboOption);

        int result = JOptionPane.showConfirmDialog(controller, panel, "Hội Đồng Xét Duyệt Lương", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            float heSoMoi = heSoCu;
            int selectedIndex = cboOption.getSelectedIndex();
            String lyDo = "";

            try {
                // Xử lý các lựa chọn tăng lương
                if (selectedIndex == 0) { heSoMoi = heSoCu + 0.33f; lyDo = "Nâng lương thường niên"; }
                else if (selectedIndex == 1) { heSoMoi = heSoCu + 0.66f; lyDo = "Nâng lương đặc cách"; }
                else if (selectedIndex == 2) { heSoMoi = heSoDeXuat; lyDo = "Nâng lương theo niên hạn"; }
                else { 
                    String input = JOptionPane.showInputDialog(controller, "Nhập hệ số mới:", String.format("%.2f", heSoCu + 0.33));
                    if (input == null || input.trim().isEmpty()) return;
                    heSoMoi = Float.parseFloat(input);
                    lyDo = "Điều chỉnh thủ công"; 
                }
                
                // Làm tròn 2 chữ số thập phân
                heSoMoi = (float) (Math.round(heSoMoi * 100.0) / 100.0);
                if (heSoMoi <= heSoCu) { JOptionPane.showMessageDialog(controller, "Hệ số mới phải cao hơn hệ số cũ!"); return; }

                if (controller.dao.capNhatHeSo(maNV, heSoMoi)) {
                    String chiTiet = String.format("<html>- %s<br>- Hệ số: %.2f -> <font color='blue'><b>%.2f</b></font></html>", lyDo, heSoCu, heSoMoi);
                    controller.dao.ghiLichSu(maNV, "Nâng Lương", chiTiet, controller.taiKhoanHienTai);
                    JOptionPane.showMessageDialog(controller, "✅ Cập nhật thành công!");
                    controller.loadData("NV.MaNV ASC");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(controller, "Lỗi nhập liệu: " + ex.getMessage());
            }
        }
    }   

    // Cập nhật phạt đi trễ
    public void xuLyPhat() {
        int row = controller.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(controller, "Vui lòng chọn nhân viên cần phạt!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maNV = controller.table.getValueAt(row, 0).toString();
        String hoTen = controller.table.getValueAt(row, 1).toString();
        
        String input = JOptionPane.showInputDialog(controller, "Nhập số ngày đi trễ của " + hoTen + ":", "Xử Lý Vi Phạm", JOptionPane.QUESTION_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                int soNgay = Integer.parseInt(input.trim());
                if (soNgay < 0) { JOptionPane.showMessageDialog(controller, "Số ngày không được âm!"); return; }
                
                controller.dao.capNhatPhat(maNV, soNgay); 
                controller.dao.ghiLichSu(maNV, "Phạt đi trễ", "Số ngày trễ: " + soNgay, controller.taiKhoanHienTai);
                JOptionPane.showMessageDialog(controller, "✅ Đã ghi nhận phạt cho: " + hoTen);
                controller.loadData("NV.MaNV ASC");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(controller, "Vui lòng nhập số nguyên hợp lệ!");
            }
        }
    }

    // Hàm hỗ trợ nhập liệu thưởng
    private Object[] nhapThongTinThuong() {
        String[] lyDoList = { "Thưởng Nóng (Đột xuất)", "Thưởng Quý 1", "Thưởng Quý 2", "Thưởng Quý 3", "Thưởng Quý 4", "Thưởng Hoàn Thành Dự Án", "Thưởng Sinh Nhật Công Ty" };
        String lyDoChon = (String) JOptionPane.showInputDialog(controller, "Chọn loại hình khen thưởng:", "Danh Mục Thưởng", JOptionPane.PLAIN_MESSAGE, null, lyDoList, lyDoList[0]);
        if (lyDoChon == null) return null;

        String moneyStr = JOptionPane.showInputDialog(controller, "Nhập số tiền thưởng (VNĐ):", "1000000");
        if (moneyStr == null || moneyStr.trim().isEmpty()) return null;

        try {
            long tienThuong = Long.parseLong(moneyStr.replace(",", "").replace(".", ""));
            if (tienThuong <= 0) { JOptionPane.showMessageDialog(controller, "Tiền thưởng phải lớn hơn 0!"); return null; }
            return new Object[]{lyDoChon, tienThuong};
        } catch (Exception e) {
            JOptionPane.showMessageDialog(controller, "Nhập tiền sai định dạng!");
            return null;
        }
    }

    public void xuLyThuongToanCongTy() {
        Object[] info = nhapThongTinThuong();
        if (info == null) return;
        
        String lyDo = (String) info[0];
        long tien = (long) info[1];

        if (controller.dao.congTienThuong(tien, lyDo)) {
            controller.dao.ghiLichSu("ALL", lyDo, "Mức thưởng: " + String.format("%,d", tien), controller.taiKhoanHienTai);
            JOptionPane.showMessageDialog(controller, "✅ Đã chi thưởng cho TOÀN CÔNG TY!");
            controller.loadData("NV.MaNV ASC");
        } else {
            JOptionPane.showMessageDialog(controller, "❌ Lỗi kết nối!");
        }
    }

    public void xuLyThuongPhongBan() {
        // Lấy danh sách các phòng ban hiện có từ Table
        java.util.Set<String> danhSachPhong = new java.util.HashSet<>();
        for (int i = 0; i < controller.table.getRowCount(); i++) {
            danhSachPhong.add(controller.table.getValueAt(i, 2).toString());
        }
        
        if (danhSachPhong.isEmpty()) { 
            JOptionPane.showMessageDialog(controller, "Danh sách trống!"); 
            return; 
        }
        
        String[] cacPhong = danhSachPhong.toArray(new String[0]);
        String phongDuocChon = (String) JOptionPane.showInputDialog(
            controller, 
            "Chọn phòng ban cần thưởng:", 
            "Danh Sách Phòng", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            cacPhong, 
            cacPhong[0]
        );

        if (phongDuocChon == null) return;

        Object[] info = nhapThongTinThuong();
        if (info == null) return;

        String lyDo = (String) info[0];
        long tien = (long) info[1];

        if (controller.dao.congTienThuongTheoPhong(phongDuocChon, tien, lyDo)) {
            controller.dao.ghiLichSu("DEPT", lyDo, "Phòng: " + phongDuocChon + " - Tiền: " + String.format("%,d", tien), controller.taiKhoanHienTai);     
            JOptionPane.showMessageDialog(controller, "✅ Đã chi thưởng cho phòng " + phongDuocChon.toUpperCase() + "!");
            controller.loadData("NV.MaNV ASC");
        } else {
            JOptionPane.showMessageDialog(controller, "❌ Lỗi hệ thống khi cộng tiền!");
        }
    }

    public void xuLyThuongCaNhan(String maNV, String tenNV, String sTien) {
        try {
            long tien = Long.parseLong(sTien.replaceAll("[^0-9]", ""));
            if (tien <= 0) { JOptionPane.showMessageDialog(controller, "Số tiền thưởng phải lớn hơn 0!"); return; }

            String lyDo = JOptionPane.showInputDialog(controller, "Nhập lý do thưởng cho " + tenNV + ":", "Thưởng Nóng", JOptionPane.QUESTION_MESSAGE);
            if (lyDo == null || lyDo.trim().isEmpty()) lyDo = "Thưởng đột xuất";

            controller.dao.congTienThuongCaNhan(maNV, tien, lyDo);
            
            controller.dao.ghiLichSu(maNV, "Thưởng Cá Nhân", lyDo + " - Tiền: " + String.format("%,d", tien), controller.taiKhoanHienTai);

            JOptionPane.showMessageDialog(controller, "✅ Đã thưởng nóng cho " + tenNV);
            controller.loadData("NV.MaNV ASC"); 

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(controller, "Vui lòng nhập số tiền hợp lệ!");
        }
    }

    public void xuLyChotThang() {
        int confirm = JOptionPane.showConfirmDialog(controller, "BẠN CÓ CHẮC MUỐN CHỐT SỔ THÁNG NÀY?\n\nHành động này sẽ xóa hết ngày trễ, phạt, thưởng để tính tháng mới.", "Cảnh báo Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.dao.resetThangMoi()) {
                JOptionPane.showMessageDialog(controller, "✅ Đã reset dữ liệu cho tháng mới!");
                controller.loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(controller, "❌ Lỗi hệ thống!");
            }
        }
    }
    
    // Xuất báo cáo ra file CSV (Excel)
    public void xuLyXuatExcel() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel File (*.csv)", "csv"));
        int userSelection = fileChooser.showSaveDialog(controller);
        
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
                bw.write("\uFEFF"); // BOM marker cho Excel đọc UTF-8
                for (int i = 0; i < controller.table.getColumnCount(); i++) {
                    bw.write(controller.table.getColumnName(i));
                    if (i < controller.table.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
                for (int i = 0; i < controller.table.getRowCount(); i++) {
                    for (int j = 0; j < controller.table.getColumnCount(); j++) {
                        String val = controller.table.getValueAt(i, j).toString();
                        val = val.replace(",", ""); 
                        bw.write(val);
                        if (j < controller.table.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(controller, "✅ Xuất file Excel thành công!\n" + filePath);
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(controller, "❌ Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }
    
    // Logic xác định ngày lễ tự động
    private String layTenNgayLe(LocalDate date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        if (day == 1 && month == 1) return "Tết Dương Lịch";
        if (day == 30 && month == 4) return "Ngày Giải Phóng (30/4)";
        if (day == 1 && month == 5) return "Quốc Tế Lao Động (1/5)";
        if (day == 2 && month == 9) return "Quốc Khánh (2/9)";
        if (day == 25 && month == 12) return "Giáng Sinh (Noel)";
        return null;
    }

    public void xuLyChamCongNgayLe() {
        LocalDate today = LocalDate.now();
        String tenLeDuongLich = layTenNgayLe(today); 

        int row = controller.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(controller, "Vui lòng chọn nhân viên để chấm công!");
            return;
        }

        String maNV = controller.table.getValueAt(row, 0).toString();
        String hoTen = controller.table.getValueAt(row, 1).toString();
        
        Object[] options = { "Lễ Âm Lịch / Thủ Công", "Theo Dương Lịch (Auto)", "❌ Hủy Bỏ" };

        int choice = JOptionPane.showOptionDialog(controller,
            "Hôm nay là: " + today + "\n" +
            "Hệ thống phát hiện lễ dương lịch: " + (tenLeDuongLich != null ? tenLeDuongLich : "Không có") + "\n\n" +
            "Bạn muốn chấm công cho [" + hoTen + "] theo chế độ nào?",
            "Chế Độ Chấm Công", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]); 

        long heSoLuongNgay = 1; 
        String lyDo = "";
        
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;

        if (choice == 0) {
            String tenLeInput = JOptionPane.showInputDialog(controller, "Nhập tên ngày lễ (Ví dụ: Mùng 1 Tết...):", "Xác nhận Lễ Đặc Biệt", JOptionPane.INFORMATION_MESSAGE);
            if (tenLeInput == null || tenLeInput.trim().isEmpty()) return;
            heSoLuongNgay = 3;
            lyDo = "Trực lễ: " + tenLeInput + " (x" + heSoLuongNgay + ")";
        }
        else if (choice == 1) {
            if (tenLeDuongLich != null) {
                heSoLuongNgay = 3;
                lyDo = "Trực lễ: " + tenLeDuongLich + " (x3 Auto)";
            } else {
                heSoLuongNgay = 1;
                lyDo = "Làm thêm ngày thường (" + today + ")";
            }
        }

        NhanVien nv = controller.dao.getNhanVienTheoMa(maNV); 
        long luong1Ngay = (long) ((nv.getLuongCoBan() * nv.getHeSoLuong()) / 26);
        long tienCongThem = luong1Ngay * heSoLuongNgay;

        int confirm = JOptionPane.showConfirmDialog(controller, 
            "XÁC NHẬN CHẤM CÔNG:\n- Nhân viên: " + hoTen + "\n- Chế độ: " + lyDo + "\n- Tiền cộng thêm: " + String.format("%,d VNĐ", tienCongThem),
            "Thực Thi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.dao.congTienThuongChoNhanVien(maNV, tienCongThem);
            controller.dao.ghiLichSu(maNV, "Chấm công", lyDo + " - Tiền: " + String.format("%,d", tienCongThem), controller.taiKhoanHienTai);
            controller.loadData("NV.MaNV ASC"); 
            JOptionPane.showMessageDialog(controller, "✅ Đã chấm công thành công!");
        }
    }
    
    public void xuLyTimKiemDaNang() { 
        controller.lastMa = controller.txtMaNV.getText().trim();
        controller.lastTen = controller.txtHoTen.getText().trim();
        String selectedPhong = controller.cboPhongBan.getSelectedItem() != null ? controller.cboPhongBan.getSelectedItem().toString() : "";
        controller.lastPhong = selectedPhong.equals("Tất cả Phòng Ban") ? "" : selectedPhong;
        controller.lastLuong = "";
        controller.lastHeSo = controller.cboHeSo.getSelectedItem() != null ? controller.cboHeSo.getSelectedItem().toString() : "";
        controller.reloadTable("NV.MaNV ASC"); 
    }

    // Gửi email phiếu lương hàng loạt (Chạy ngầm)
    public void xuLyPhatLuongHangLoat() {
         java.util.List<NhanVien> listNV = controller.dao.layDanhSachNhanVien("NV.MaNV ASC");
         int count = 0;
         String thangHienTai = java.time.LocalDate.now().getMonthValue() + "/" + java.time.LocalDate.now().getYear();

         for (NhanVien nv : listNV) {
             long thucLinh = nv.getGross(); 
             
             String noiDung = String.format(
                 "Chào %s,\n\nĐây là phiếu lương tháng %s của bạn:\n--------------------------------\n- Lương Cứng:   %,d VNĐ\n- Hệ Số Lương:  %s\n- Thưởng Thêm:  %,d VNĐ\n- Bị Trừ Phạt:  %,d VNĐ\n--------------------------------\nTHỰC LĨNH:      %,d VNĐ\n\nCảm ơn bạn đã cống hiến cho Konami!",
                 nv.getHoTen(), thangHienTai, nv.getLuongCoBan(), nv.getHeSoLuong(), nv.getTienThuong(), nv.getTienPhat(), thucLinh
             );

             // Gửi vào bảng HopThu trong CSDL
             if (controller.dao.guiThuMoi(nv.getMaNV(), "Phiếu Lương Tháng " + thangHienTai, noiDung)) {
                 count++;
             }
         }
         javax.swing.JOptionPane.showMessageDialog(controller, "✅ Đã gửi thành công " + count + " phiếu lương vào hộp thư!");
    }

    public void xuLyGuiPhieuLuongRieng(String maNV) {
        NhanVien nv = controller.dao.getNhanVienTheoMa(maNV);
        if (nv == null) return;

        int confirm = JOptionPane.showConfirmDialog(controller, "Gửi lại phiếu lương riêng cho nhân viên: " + nv.getHoTen() + "?", "Xác Nhận Gửi Lẻ", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        long thucLinh = nv.getGross(); 
        String thangHienTai = LocalDate.now().getMonthValue() + "/" + LocalDate.now().getYear();
        
        String noiDung = String.format(
            "Chào %s,\n\n[CẬP NHẬT] Đây là phiếu lương ĐIỀU CHỈNH tháng %s của bạn:\n--------------------------------\n- Lương Cứng:   %,d VNĐ\n- Hệ Số Lương:  %s\n- Thưởng Thêm:  %,d VNĐ\n- Bị Trừ Phạt:  %,d VNĐ\n--------------------------------\nTHỰC LĨNH:      %,d VNĐ\n\nMọi thắc mắc vui lòng phản hồi lại mail này.",
            nv.getHoTen(), thangHienTai, nv.getLuongCoBan(), nv.getHeSoLuong(), nv.getTienThuong(), nv.getTienPhat(), thucLinh
        );

        if (controller.dao.guiThuMoi(nv.getMaNV(), "Phiếu Lương Điều Chỉnh T" + thangHienTai, noiDung)) {
            JOptionPane.showMessageDialog(controller, "✅ Đã gửi phiếu lương riêng cho " + nv.getHoTen());
        } else {
            JOptionPane.showMessageDialog(controller, "❌ Gửi thất bại!");
        }
    }
    
    // Tính toán lại toàn bộ lương (Gross/Net/Thuế) trước khi phát
    public void xuLyPhatLuongToanCongTy() {
        int confirm = JOptionPane.showConfirmDialog(controller, 
            "Hệ thống sẽ tính toán lại lương cho TOÀN BỘ nhân viên dựa trên:\n" +
            "- Giờ tăng ca\n- Thưởng/Phạt hiện tại\n- Thuế TNCN & Bảo hiểm\n\nTiếp tục?", 
            "Xác nhận phát lương", JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            java.sql.Connection conn = database.ConnectDB.getConnection();
            
            // Lấy dữ liệu thô để tính toán
            String sqlGet = "SELECT MaNV, LuongCoBan, HeSoLuong, GioTangCa, HeSoTangCa, TienThuong, TienPhat, NgayVaoLam FROM NhanVien";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            ResultSet rs = psGet.executeQuery();
            
            String sqlUpdate = "UPDATE NhanVien SET ThucLinh = ? WHERE MaNV = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            
            // Vòng lặp tính toán: Java xử lý Logic -> SQL lưu trữ
            while (rs.next()) {
                String maNV = rs.getString("MaNV");
                long luongCB = rs.getLong("LuongCoBan");
                float heSo = rs.getFloat("HeSoLuong");
                double gioOT = rs.getDouble("GioTangCa"); 
                double heSoOT = rs.getDouble("HeSoTangCa"); 
                java.sql.Date ngayVaoLam = rs.getDate("NgayVaoLam");
                if (heSoOT == 0) heSoOT = 1.5; 
                
                long thuong = rs.getLong("TienThuong");
                long phat = rs.getLong("TienPhat");
                
                // Sử dụng Class tiện ích MayTinhLuong để tính con số cuối cùng
                long thucLinh = MayTinhLuong.tinhThucLinhFinal(luongCB, heSo, gioOT, heSoOT, thuong, phat, 0,ngayVaoLam);
                
                // Đẩy vào Batch Update để tối ưu hiệu năng
                psUpdate.setLong(1, thucLinh);
                psUpdate.setString(2, maNV);
                psUpdate.addBatch();
            }
            
            // Thực thi cập nhật hàng loạt
            int[] results = psUpdate.executeBatch();
            
            int soNguoiDuocCapNhat = 0;
            for (int r : results) {
                if (r >= 0 || r == java.sql.Statement.SUCCESS_NO_INFO) {
                    soNguoiDuocCapNhat++;
                }
            }
            
            psGet.close();
            psUpdate.close();
            
            JOptionPane.showMessageDialog(controller, "✅ Đã tính toán và cập nhật lương thành công cho " + soNguoiDuocCapNhat + " nhân sự!");
            
            // Refresh lại bảng hiển thị
            controller.loadData("NV.MaNV ASC");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(controller, "Lỗi phát lương: " + e.getMessage());
        }
    }
    
    // Lưu trữ lương tháng vào bảng Lịch Sử và Reset bảng chính
	public void chotSoVaLuuTruThangNay() {
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        
        String[] months = {
            "Tháng " + currentMonth + "/" + currentYear,
            "Tháng " + (currentMonth == 1 ? 12 : currentMonth - 1) + "/" + (currentMonth == 1 ? currentYear - 1 : currentYear),
            "Tháng " + (currentMonth <= 2 ? 10 + currentMonth : currentMonth - 2) + "/" + (currentMonth <= 2 ? currentYear - 1 : currentYear)
        };

        String selectedMonthStr = (String) JOptionPane.showInputDialog(controller, 
            "Chọn kỳ lương muốn chốt sổ và lưu trữ:", 
            "Xác Nhận Chốt Sổ", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            months, 
            months[0]);

        if (selectedMonthStr == null) return;

        String[] parts = selectedMonthStr.replace("Tháng ", "").split("/");
        int thangChot = Integer.parseInt(parts[0]);
        int namChot = Integer.parseInt(parts[1]);

        int confirm = JOptionPane.showConfirmDialog(controller, 
            "XÁC NHẬN CHỐT LƯƠNG " + selectedMonthStr + "?\n\n" +
            "⚠️ Dữ liệu sẽ được lưu vào lịch sử và reset bảng lương về 0.",
            "Cảnh Báo", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            java.sql.Connection conn = database.ConnectDB.getConnection();
            
            // Kiểm tra tránh trùng lặp dữ liệu tháng cũ
            String sqlCheck = "SELECT COUNT(*) FROM BangLuongLuuTru WHERE Thang = ? AND Nam = ?";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setInt(1, thangChot);
            psCheck.setInt(2, namChot);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                JOptionPane.showMessageDialog(controller, "❌ Lương tháng " + thangChot + "/" + namChot + " ĐÃ ĐƯỢC CHỐT trước đó rồi!", "Trùng Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sao chép dữ liệu từ bảng NhanVien sang bảng BangLuongLuuTru
            String sqlArchive = "INSERT INTO BangLuongLuuTru (MaNV, HoTen, Thang, Nam, LuongCung, TienThuong, TienPhat, ThucLinh, LyDoGhiChu) " +
                                "SELECT MaNV, HoTen, ?, ?, LuongCoBan, TienThuong, TienPhat, ThucLinh, LyDoThuongPhat FROM NhanVien";
            
            PreparedStatement psArchive = conn.prepareStatement(sqlArchive);
            psArchive.setInt(1, thangChot);
            psArchive.setInt(2, namChot);
            int rowsSaved = psArchive.executeUpdate();
            psArchive.close();

            // Reset các chỉ số về 0 để bắt đầu tháng mới
            if (rowsSaved > 0) {
                String sqlReset = "UPDATE NhanVien SET TienThuong = 0, TienPhat = 0, GioTangCa = 0, HeSoTangCa = 1.5, ThucLinh = 0, LyDoThuongPhat = N''";
                PreparedStatement psReset = conn.prepareStatement(sqlReset);
                psReset.executeUpdate();
                psReset.close();
                
                JOptionPane.showMessageDialog(controller, "✅ THÀNH CÔNG!\nĐã lưu trữ hồ sơ lương " + selectedMonthStr + ".");
                controller.loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(controller, "⚠️ Không có dữ liệu nhân viên để lưu!");
            }
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(controller, "❌ Lỗi Chốt Sổ: " + e.getMessage());
        }
    }
}