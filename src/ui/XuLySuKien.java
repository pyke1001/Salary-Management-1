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
  
    private QuanLyNhanVien solve;

    public XuLySuKien(QuanLyNhanVien solve) {
        this.solve = solve;
    }

    public void xuLyGiamLuong() {
        int row = solve.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(solve, "Vui lòng chọn nhân viên cần giảm lương!", "Chưa chọn đối tượng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = solve.table.getValueAt(row, 0).toString();
        String hoTen = solve.table.getValueAt(row, 1).toString();
        float heSoCu = Float.parseFloat(solve.table.getValueAt(row, 5).toString());

        String input = JOptionPane.showInputDialog(solve, 
            "Nhập hệ số lương MỚI cho " + hoTen + ":\n(Hệ số hiện tại: " + heSoCu + ")", 
            heSoCu);
            
        if (input == null || input.trim().isEmpty()) return;

        try {
            float heSoMoi = Float.parseFloat(input);
            if (heSoMoi <= 0) {
                JOptionPane.showMessageDialog(solve, "Hệ số lương phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (heSoMoi >= heSoCu) {
                JOptionPane.showMessageDialog(solve, "Đang giảm lương thì hệ số mới phải NHỎ HƠN hệ số cũ!", "Sai logic", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String msg = String.format("XÁC NHẬN KỶ LUẬT HẠ BẬC LƯƠNG\n\nNhân viên: %s\nHệ số cũ: %.2f\nHệ số mới: <font color='red'><b>%.2f</b></font>", hoTen, heSoCu, heSoMoi);
            int confirm = JOptionPane.showConfirmDialog(solve, "<html>" + msg + "</html>", "Phê Duyệt Kỷ Luật", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (solve.dao.capNhatHeSo(maNV, heSoMoi)) {
                    solve.dao.ghiLichSu(maNV, "Hạ Bậc Lương", String.format("Hạ hệ số từ %.2f -> %.2f", heSoCu, heSoMoi), solve.taiKhoanHienTai);
                    JOptionPane.showMessageDialog(solve, "✅ Đã cập nhật hệ số lương mới!");
                    solve.loadData("NV.MaNV ASC");
                } else {
                    JOptionPane.showMessageDialog(solve, "❌ Lỗi kết nối CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(solve, "Vui lòng nhập con số hợp lệ!");
        }
    }

    public void xuLyTangLuong() { 
        int row = solve.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(solve, "Vui lòng chọn nhân viên cần xét nâng lương!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = solve.table.getValueAt(row, 0).toString();
        String hoTen = solve.table.getValueAt(row, 1).toString();
        float heSoCu = Float.parseFloat(solve.table.getValueAt(row, 5).toString());
        
        NhanVien nvFull = solve.dao.getNhanVienTheoMa(maNV);
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

        int result = JOptionPane.showConfirmDialog(solve, panel, "Hội Đồng Xét Duyệt Lương", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            float heSoMoi = heSoCu;
            int selectedIndex = cboOption.getSelectedIndex();
            String lyDo = "";

            try {
                if (selectedIndex == 0) { heSoMoi = heSoCu + 0.33f; lyDo = "Nâng lương thường niên"; }
                else if (selectedIndex == 1) { heSoMoi = heSoCu + 0.66f; lyDo = "Nâng lương đặc cách"; }
                else if (selectedIndex == 2) { heSoMoi = heSoDeXuat; lyDo = "Nâng lương theo niên hạn"; }
                else { 
                    String input = JOptionPane.showInputDialog(solve, "Nhập hệ số mới:", String.format("%.2f", heSoCu + 0.33));
                    if (input == null || input.trim().isEmpty()) return;
                    heSoMoi = Float.parseFloat(input);
                    lyDo = "Điều chỉnh thủ công"; 
                }
                
                heSoMoi = (float) (Math.round(heSoMoi * 100.0) / 100.0);
                if (heSoMoi <= heSoCu) { JOptionPane.showMessageDialog(solve, "Hệ số mới phải cao hơn hệ số cũ!"); return; }

                if (solve.dao.capNhatHeSo(maNV, heSoMoi)) {
                    String chiTiet = String.format("<html>- %s<br>- Hệ số: %.2f -> <font color='blue'><b>%.2f</b></font></html>", lyDo, heSoCu, heSoMoi);
                    solve.dao.ghiLichSu(maNV, "Nâng Lương", chiTiet, solve.taiKhoanHienTai);
                    JOptionPane.showMessageDialog(solve, "✅ Cập nhật thành công!");
                    solve.loadData("NV.MaNV ASC");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(solve, "Lỗi nhập liệu: " + ex.getMessage());
            }
        }
    }   

    public void xuLyPhat() {
        int row = solve.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(solve, "Vui lòng chọn nhân viên cần phạt!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maNV = solve.table.getValueAt(row, 0).toString();
        String hoTen = solve.table.getValueAt(row, 1).toString();
        
        String input = JOptionPane.showInputDialog(solve, "Nhập số ngày đi trễ của " + hoTen + ":", "Xử Lý Vi Phạm", JOptionPane.QUESTION_MESSAGE);
            
        if (input != null && !input.trim().isEmpty()) {
            try {
                int soNgay = Integer.parseInt(input.trim());
                if (soNgay < 0) { JOptionPane.showMessageDialog(solve, "Số ngày không được âm!"); return; }
                
                solve.dao.capNhatPhat(maNV, soNgay); 
                solve.dao.ghiLichSu(maNV, "Phạt đi trễ", "Số ngày trễ: " + soNgay, solve.taiKhoanHienTai);
                JOptionPane.showMessageDialog(solve, "✅ Đã ghi nhận phạt cho: " + hoTen);
                solve.loadData("NV.MaNV ASC");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(solve, "Vui lòng nhập số nguyên hợp lệ!");
            }
        }
    }

    private Object[] nhapThongTinThuong() {
        String[] lyDoList = { "Thưởng Nóng (Đột xuất)", "Thưởng Quý 1", "Thưởng Quý 2", "Thưởng Quý 3", "Thưởng Quý 4", "Thưởng Hoàn Thành Dự Án", "Thưởng Sinh Nhật Công Ty" };
        String lyDoChon = (String) JOptionPane.showInputDialog(solve, "Chọn loại hình khen thưởng:", "Danh Mục Thưởng", JOptionPane.PLAIN_MESSAGE, null, lyDoList, lyDoList[0]);
        if (lyDoChon == null) return null;

        String moneyStr = JOptionPane.showInputDialog(solve, "Nhập số tiền thưởng (VNĐ):", "1000000");
        if (moneyStr == null || moneyStr.trim().isEmpty()) return null;

        try {
            long tienThuong = Long.parseLong(moneyStr.replace(",", "").replace(".", ""));
            if (tienThuong <= 0) { JOptionPane.showMessageDialog(solve, "Tiền thưởng phải lớn hơn 0!"); return null; }
            return new Object[]{lyDoChon, tienThuong};
        } catch (Exception e) {
            JOptionPane.showMessageDialog(solve, "Nhập tiền sai định dạng!");
            return null;
        }
    }

    // Trong file XuLySuKien.java

    public void xuLyThuongToanCongTy() {
        Object[] info = nhapThongTinThuong();
        if (info == null) return;
        
        String lyDo = (String) info[0];
        long tien = (long) info[1];

        // [FIX] Truyền thêm lyDo vào
        if (solve.dao.congTienThuong(tien, lyDo)) {
            solve.dao.ghiLichSu("ALL", lyDo, "Mức thưởng: " + String.format("%,d", tien), solve.taiKhoanHienTai);
            JOptionPane.showMessageDialog(solve, "✅ Đã chi thưởng cho TOÀN CÔNG TY!");
            solve.loadData("NV.MaNV ASC");
        } else {
            JOptionPane.showMessageDialog(solve, "❌ Lỗi kết nối!");
        }
    }

    public void xuLyThuongPhongBan() {
        // 1. Lấy danh sách các phòng ban hiện có trên bảng để cho người dùng chọn
        java.util.Set<String> danhSachPhong = new java.util.HashSet<>();
        for (int i = 0; i < solve.table.getRowCount(); i++) {
            danhSachPhong.add(solve.table.getValueAt(i, 2).toString());
        }
        
        if (danhSachPhong.isEmpty()) { 
            JOptionPane.showMessageDialog(solve, "Danh sách trống!"); 
            return; 
        }
        
        String[] cacPhong = danhSachPhong.toArray(new String[0]);
        
        // --- ĐÂY LÀ CHỖ KHAI BÁO BIẾN CẬU ĐANG TÌM ---
        String phongDuocChon = (String) JOptionPane.showInputDialog(
            solve, 
            "Chọn phòng ban cần thưởng:", 
            "Danh Sách Phòng", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            cacPhong, 
            cacPhong[0]
        );
        // ----------------------------------------------

        if (phongDuocChon == null) return; // Nếu bấm Cancel thì thoát

        // 2. Nhập thông tin tiền và lý do
        Object[] info = nhapThongTinThuong();
        if (info == null) return;

        String lyDo = (String) info[0];
        long tien = (long) info[1];

        // 3. Gọi DAO để xử lý
        if (solve.dao.congTienThuongTheoPhong(phongDuocChon, tien, lyDo)) {
            solve.dao.ghiLichSu("DEPT", lyDo, "Phòng: " + phongDuocChon + " - Tiền: " + String.format("%,d", tien), solve.taiKhoanHienTai);     
            JOptionPane.showMessageDialog(solve, "✅ Đã chi thưởng cho phòng " + phongDuocChon.toUpperCase() + "!");
            solve.loadData("NV.MaNV ASC");
        } else {
            JOptionPane.showMessageDialog(solve, "❌ Lỗi hệ thống khi cộng tiền!");
        }
    }

    public void xuLyThuongCaNhan(String maNV, String tenNV, String sTien) {
        try {
            long tien = Long.parseLong(sTien.replaceAll("[^0-9]", ""));
            if (tien <= 0) { JOptionPane.showMessageDialog(solve, "Số tiền thưởng phải lớn hơn 0!"); return; }

            // [MỚI] Hỏi lý do thưởng cá nhân
            String lyDo = JOptionPane.showInputDialog(solve, "Nhập lý do thưởng cho " + tenNV + ":", "Thưởng Nóng", JOptionPane.QUESTION_MESSAGE);
            if (lyDo == null || lyDo.trim().isEmpty()) lyDo = "Thưởng đột xuất";

            // [FIX] Truyền thêm lyDo vào
            solve.dao.congTienThuongCaNhan(maNV, tien, lyDo);
            
            solve.dao.ghiLichSu(maNV, "Thưởng Cá Nhân", lyDo + " - Tiền: " + String.format("%,d", tien), solve.taiKhoanHienTai);

            JOptionPane.showMessageDialog(solve, "✅ Đã thưởng nóng cho " + tenNV);
            solve.loadData("NV.MaNV ASC"); 

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(solve, "Vui lòng nhập số tiền hợp lệ!");
        }
    }

    public void xuLyChotThang() {
        int confirm = JOptionPane.showConfirmDialog(solve, "BẠN CÓ CHẮC MUỐN CHỐT SỔ THÁNG NÀY?\n\nHành động này sẽ xóa hết ngày trễ, phạt, thưởng để tính tháng mới.", "Cảnh báo Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (solve.dao.resetThangMoi()) {
                JOptionPane.showMessageDialog(solve, "✅ Đã reset dữ liệu cho tháng mới!");
                solve.loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(solve, "❌ Lỗi hệ thống!");
            }
        }
    }
    
    public void xuLyXuatExcel() {
        // (Copy lại code cũ của cậu vào đây, đoạn này không đổi)
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel File (*.csv)", "csv"));
        int userSelection = fileChooser.showSaveDialog(solve);
        
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
                bw.write("\uFEFF"); 
                for (int i = 0; i < solve.table.getColumnCount(); i++) {
                    bw.write(solve.table.getColumnName(i));
                    if (i < solve.table.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
                for (int i = 0; i < solve.table.getRowCount(); i++) {
                    for (int j = 0; j < solve.table.getColumnCount(); j++) {
                        String val = solve.table.getValueAt(i, j).toString();
                        val = val.replace(",", ""); 
                        bw.write(val);
                        if (j < solve.table.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(solve, "✅ Xuất file Excel thành công!\n" + filePath);
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(solve, "❌ Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }
    
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

        int row = solve.table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(solve, "Vui lòng chọn nhân viên để chấm công!");
            return;
        }

        String maNV = solve.table.getValueAt(row, 0).toString();
        String hoTen = solve.table.getValueAt(row, 1).toString();
        
        Object[] options = { "Lễ Âm Lịch / Thủ Công", "Theo Dương Lịch (Auto)", "❌ Hủy Bỏ" };

        int choice = JOptionPane.showOptionDialog(solve,
            "Hôm nay là: " + today + "\n" +
            "Hệ thống phát hiện lễ dương lịch: " + (tenLeDuongLich != null ? tenLeDuongLich : "Không có") + "\n\n" +
            "Bạn muốn chấm công cho [" + hoTen + "] theo chế độ nào?",
            "Chế Độ Chấm Công", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]); 

        long heSoLuongNgay = 1; 
        String lyDo = "";
        
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;

        if (choice == 0) {
            String tenLeInput = JOptionPane.showInputDialog(solve, "Nhập tên ngày lễ (Ví dụ: Mùng 1 Tết...):", "Xác nhận Lễ Đặc Biệt", JOptionPane.INFORMATION_MESSAGE);
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

        NhanVien nv = solve.dao.getNhanVienTheoMa(maNV); 
        long luong1Ngay = (long) ((nv.getLuongCoBan() * nv.getHeSoLuong()) / 26);
        long tienCongThem = luong1Ngay * heSoLuongNgay;

        int confirm = JOptionPane.showConfirmDialog(solve, 
            "XÁC NHẬN CHẤM CÔNG:\n- Nhân viên: " + hoTen + "\n- Chế độ: " + lyDo + "\n- Tiền cộng thêm: " + String.format("%,d VNĐ", tienCongThem),
            "Thực Thi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            solve.dao.congTienThuongChoNhanVien(maNV, tienCongThem);
            solve.dao.ghiLichSu(maNV, "Chấm công", lyDo + " - Tiền: " + String.format("%,d", tienCongThem), solve.taiKhoanHienTai);
            solve.loadData("NV.MaNV ASC"); 
            JOptionPane.showMessageDialog(solve, "✅ Đã chấm công thành công!");
        }
    }
    
    public void xuLyTimKiemDaNang() { 
        solve.lastMa = solve.txtMaNV.getText().trim();
        solve.lastTen = solve.txtHoTen.getText().trim();
        String selectedPhong = solve.cboPhongBan.getSelectedItem() != null ? solve.cboPhongBan.getSelectedItem().toString() : "";
        solve.lastPhong = selectedPhong.equals("Tất cả Phòng Ban") ? "" : selectedPhong;
        solve.lastLuong = "";
        solve.lastHeSo = solve.cboHeSo.getSelectedItem() != null ? solve.cboHeSo.getSelectedItem().toString() : "";
        solve.reloadTable("NV.MaNV ASC"); 
    }

    public void xuLyPhatLuongHangLoat() {
         java.util.List<NhanVien> listNV = solve.dao.layDanhSachNhanVien("NV.MaNV ASC");
         int count = 0;
         String thangHienTai = java.time.LocalDate.now().getMonthValue() + "/" + java.time.LocalDate.now().getYear();

         for (NhanVien nv : listNV) {
             long thucLinh = nv.getGross(); // Sửa getGross() thành getThucLinh() nếu class NV dùng tên này
             
             String noiDung = String.format(
                 "Chào %s,\n\nĐây là phiếu lương tháng %s của bạn:\n--------------------------------\n- Lương Cứng:   %,d VNĐ\n- Hệ Số Lương:  %s\n- Thưởng Thêm:  %,d VNĐ\n- Bị Trừ Phạt:  %,d VNĐ\n--------------------------------\nTHỰC LĨNH:      %,d VNĐ\n\nCảm ơn bạn đã cống hiến cho Konami!",
                 nv.getHoTen(), thangHienTai, nv.getLuongCoBan(), nv.getHeSoLuong(), nv.getTienThuong(), nv.getTienPhat(), thucLinh
             );

             if (solve.dao.guiThuMoi(nv.getMaNV(), "Phiếu Lương Tháng " + thangHienTai, noiDung)) {
                 count++;
             }
         }
         javax.swing.JOptionPane.showMessageDialog(solve, "✅ Đã gửi thành công " + count + " phiếu lương vào hộp thư!");
    }

    public void xuLyGuiPhieuLuongRieng(String maNV) {
        NhanVien nv = solve.dao.getNhanVienTheoMa(maNV);
        if (nv == null) return;

        int confirm = JOptionPane.showConfirmDialog(solve, "Gửi lại phiếu lương riêng cho nhân viên: " + nv.getHoTen() + "?", "Xác Nhận Gửi Lẻ", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        long thucLinh = nv.getGross(); 
        String thangHienTai = LocalDate.now().getMonthValue() + "/" + LocalDate.now().getYear();
        
        String noiDung = String.format(
            "Chào %s,\n\n[CẬP NHẬT] Đây là phiếu lương ĐIỀU CHỈNH tháng %s của bạn:\n--------------------------------\n- Lương Cứng:   %,d VNĐ\n- Hệ Số Lương:  %s\n- Thưởng Thêm:  %,d VNĐ\n- Bị Trừ Phạt:  %,d VNĐ\n--------------------------------\nTHỰC LĨNH:      %,d VNĐ\n\nMọi thắc mắc vui lòng phản hồi lại mail này.",
            nv.getHoTen(), thangHienTai, nv.getLuongCoBan(), nv.getHeSoLuong(), nv.getTienThuong(), nv.getTienPhat(), thucLinh
        );

        if (solve.dao.guiThuMoi(nv.getMaNV(), "Phiếu Lương Điều Chỉnh T" + thangHienTai, noiDung)) {
            JOptionPane.showMessageDialog(solve, "✅ Đã gửi phiếu lương riêng cho " + nv.getHoTen());
        } else {
            JOptionPane.showMessageDialog(solve, "❌ Gửi thất bại!");
        }
    }
    
    public void xuLyPhatLuongToanCongTy() {
        int confirm = JOptionPane.showConfirmDialog(solve, 
            "Hệ thống sẽ tính toán lại lương cho TOÀN BỘ nhân viên dựa trên:\n" +
            "- Giờ tăng ca\n- Thưởng/Phạt hiện tại\n- Thuế TNCN & Bảo hiểm\n\nTiếp tục?", 
            "Xác nhận phát lương", JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            java.sql.Connection conn = database.ConnectDB.getConnection();
            
            // 1. Lấy dữ liệu thô để tính toán
            String sqlGet = "SELECT MaNV, LuongCoBan, HeSoLuong, GioTangCa, HeSoTangCa, TienThuong, TienPhat FROM NhanVien";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            ResultSet rs = psGet.executeQuery();
            
            String sqlUpdate = "UPDATE NhanVien SET ThucLinh = ? WHERE MaNV = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            
            // 2. Vòng lặp tính toán (Java xử lý Logic -> SQL lưu trữ)
            while (rs.next()) {
                String maNV = rs.getString("MaNV");
                long luongCB = rs.getLong("LuongCoBan");
                float heSo = rs.getFloat("HeSoLuong");
                double gioOT = rs.getDouble("GioTangCa"); // Cột mới
                double heSoOT = rs.getDouble("HeSoTangCa"); // Cột mới
                if (heSoOT == 0) heSoOT = 1.5; // Giá trị mặc định nếu null
                
                long thuong = rs.getLong("TienThuong");
                long phat = rs.getLong("TienPhat");
                
                // Giả định số người phụ thuộc là 0 (hoặc cậu có thể thêm cột này vào DB sau)
                long thucLinh = MayTinhLuong.tinhThucLinhFinal(luongCB, heSo, gioOT, heSoOT, thuong, phat, 0);
                
                // Đẩy vào Batch Update
                psUpdate.setLong(1, thucLinh);
                psUpdate.setString(2, maNV);
                psUpdate.addBatch();
            }
            
            // 3. Thực thi cập nhật hàng loạt
            int[] results = psUpdate.executeBatch();
            conn.commit(); // Nếu connection của cậu setAutoCommit(false)
            
            int soNguoiDuocCapNhat = 0;
            for (int r : results) {
                // Trong JDBC, số >= 0 nghĩa là số dòng bị ảnh hưởng, SUCCESS_NO_INFO cũng là thành công
                if (r >= 0 || r == java.sql.Statement.SUCCESS_NO_INFO) {
                    soNguoiDuocCapNhat++;
                }
            }
            
            psGet.close();
            psUpdate.close();
            
            JOptionPane.showMessageDialog(solve, "✅ Đã tính toán và cập nhật lương thành công cho " + soNguoiDuocCapNhat + " nhân sự!");
            
            // Refresh lại bảng hiển thị
            solve.loadData("NV.MaNV ASC");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(solve, "Lỗi phát lương: " + e.getMessage());
        }
    }
    
	public void chotSoVaLuuTruThangNay() {
        // 1. Tạo danh sách tháng để chọn (Mặc định chọn tháng hiện tại)
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();
        
        // Tạo option cho 3 tháng gần nhất (để lỡ có chốt bù)
        String[] months = {
            "Tháng " + currentMonth + "/" + currentYear,
            "Tháng " + (currentMonth == 1 ? 12 : currentMonth - 1) + "/" + (currentMonth == 1 ? currentYear - 1 : currentYear),
            "Tháng " + (currentMonth <= 2 ? 10 + currentMonth : currentMonth - 2) + "/" + (currentMonth <= 2 ? currentYear - 1 : currentYear)
        };

        String selectedMonthStr = (String) JOptionPane.showInputDialog(solve, 
            "Chọn kỳ lương muốn chốt sổ và lưu trữ:", 
            "Xác Nhận Chốt Sổ", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            months, 
            months[0]); // Mặc định chọn tháng hiện tại

        if (selectedMonthStr == null) return; // Nếu bấm Cancel thì thôi

        // Parse lại tháng năm từ chuỗi vừa chọn
        String[] parts = selectedMonthStr.replace("Tháng ", "").split("/");
        int thangChot = Integer.parseInt(parts[0]);
        int namChot = Integer.parseInt(parts[1]);

        // 2. Hỏi xác nhận lần cuối
        int confirm = JOptionPane.showConfirmDialog(solve, 
            "XÁC NHẬN CHỐT LƯƠNG " + selectedMonthStr + "?\n\n" +
            "⚠️ Dữ liệu sẽ được lưu vào lịch sử và reset bảng lương về 0.",
            "Cảnh Báo", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            java.sql.Connection conn = database.ConnectDB.getConnection();
            
            // 3. Kiểm tra xem tháng này đã chốt chưa (Tránh lưu trùng)
            String sqlCheck = "SELECT COUNT(*) FROM BangLuongLuuTru WHERE Thang = ? AND Nam = ?";
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setInt(1, thangChot);
            psCheck.setInt(2, namChot);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                JOptionPane.showMessageDialog(solve, "❌ Lương tháng " + thangChot + "/" + namChot + " ĐÃ ĐƯỢC CHỐT trước đó rồi!", "Trùng Dữ Liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Thực hiện Lưu Trữ (INSERT)
            String sqlArchive = "INSERT INTO BangLuongLuuTru (MaNV, HoTen, Thang, Nam, LuongCung, TienThuong, TienPhat, ThucLinh, LyDoGhiChu) " +
                                "SELECT MaNV, HoTen, ?, ?, LuongCoBan, TienThuong, TienPhat, ThucLinh, LyDoThuongPhat FROM NhanVien";
            
            PreparedStatement psArchive = conn.prepareStatement(sqlArchive);
            psArchive.setInt(1, thangChot);
            psArchive.setInt(2, namChot);
            int rowsSaved = psArchive.executeUpdate();
            psArchive.close();

            // 5. Reset dữ liệu
            if (rowsSaved > 0) {
                String sqlReset = "UPDATE NhanVien SET TienThuong = 0, TienPhat = 0, GioTangCa = 0, HeSoTangCa = 1.5, ThucLinh = 0, LyDoThuongPhat = N''";
                PreparedStatement psReset = conn.prepareStatement(sqlReset);
                psReset.executeUpdate();
                psReset.close();
                
                JOptionPane.showMessageDialog(solve, "✅ THÀNH CÔNG!\nĐã lưu trữ hồ sơ lương " + selectedMonthStr + ".");
                solve.loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(solve, "⚠️ Không có dữ liệu nhân viên để lưu!");
            }
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(solve, "❌ Lỗi Chốt Sổ: " + e.getMessage());
        }
    }
}