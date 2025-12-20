package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Graphics2D;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ThongKeUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private DefaultTableModel mainModel;

    // --- BỘ MÀU SẮC CHUẨN PROFESSIONAL ---
    private final Color COL_PRIMARY = new Color(0, 102, 204);     // Xanh dương (Chủ đạo)
    private final Color COL_SUCCESS = new Color(40, 167, 69);     // Xanh lá (Tiền/Thưởng)
    private final Color COL_DANGER = new Color(220, 53, 69);      // Đỏ (Phạt/Lỗi)
    private final Color COL_WARNING = new Color(255, 140, 0);     // Cam/Vàng (Cảnh báo/Trung bình)
    private final Color COL_INFO = new Color(102, 51, 153);       // Tím (Thông tin đặc biệt)
    
    private final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);

    public ThongKeUI(DefaultTableModel model) {
        this.mainModel = model;
        initUI();
    }

    private void initUI() {
        setTitle("Báo cáo Quản trị Nhân Sự & Lương thưởng - Konami Enterprise");
        setSize(1100, 700); // Kích thước rộng rãi
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 247, 250)); // Màu nền xám nhẹ hiện đại

        // 1. HEADER
        JLabel lblTitle = new JLabel("TRUNG TÂM PHÂN TÍCH DỮ LIỆU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. TABBED PANE (CHIA TAB)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Dialog", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);

        // Thêm 6 Tab chức năng
        tabbedPane.addTab("  📊 Tổng Quan  ", null, taoPanelTongQuan());
        tabbedPane.addTab("  🏢 Tài Chính Phòng Ban  ", null, taoPanelTaiChinhPhongBan());
        tabbedPane.addTab("  🏆 Danh Sách Khen Thưởng  ", null, taoPanelKhenThuong());
        tabbedPane.addTab("  ⚠️ Theo Dõi Vi Phạm  ", null, taoPanelViPham());
        tabbedPane.addTab("🏆 Top Thu Nhập", null, createTabTopThuNhap());
        tabbedPane.addTab("⏳ Phân Tích Thâm Niên", null, createTabThamNien());

        add(tabbedPane, BorderLayout.CENTER);

        // 3. FOOTER
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(new Color(240, 240, 240));
        
        JButton btnClose = new JButton("Đóng Báo Cáo");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(120, 35));
        btnClose.addActionListener(e -> dispose());
        
        pnlBot.add(btnClose);
        add(pnlBot, BorderLayout.SOUTH);
    }

    // ========================================================================
    // TAB 1: TỔNG QUAN (DASHBOARD)
    // ========================================================================
    // ========================================================================
    // TAB 1: TỔNG QUAN (DASHBOARD) - ĐÃ NÂNG CẤP BIỂU ĐỒ
    // ========================================================================
    private JPanel taoPanelTongQuan() {
        // 1. Panel chính dùng BorderLayout để xếp Trên - Dưới
        JPanel pnlMain = new JPanel(new BorderLayout(0, 20));
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 2. Panel chứa 4 ô thống kê (KPIs) - Giữ nguyên logic cũ
        JPanel pnlKPI = new JPanel(new GridLayout(1, 4, 20, 0)); // Đổi thành 1 hàng 4 cột cho gọn (hoặc giữ 2x2 tùy ý cậu)
        // Nếu cậu thích 2 hàng 2 cột như cũ thì sửa dòng trên thành: new GridLayout(2, 2, 30, 30)
        pnlKPI.setBackground(Color.WHITE);
        // pnlKPI.setPreferredSize(new Dimension(1000, 250)); // Nếu dùng 2x2 thì bỏ comment dòng này để set chiều cao

        int tongNV = mainModel.getRowCount();
        long tongLuong = 0;
        long luongCaoNhat = 0;
        
        for (int i = 0; i < tongNV; i++) {
            String luongStr = mainModel.getValueAt(i, 9).toString().replace(",", "").replace(".", "");
            long luong = 0;
            try { luong = Long.parseLong(luongStr); } catch (Exception e) {}
            
            tongLuong += luong;
            if (luong > luongCaoNhat) luongCaoNhat = luong;
        }
        long luongTB = tongNV > 0 ? tongLuong / tongNV : 0;

        // Thêm 4 thẻ vào Panel KPI
        pnlKPI.add(taoOThongKe("TỔNG NHÂN SỰ", tongNV + " nhân viên", COL_PRIMARY));
        pnlKPI.add(taoOThongKe("TỔNG QUỸ LƯƠNG", String.format("%,d", tongLuong) + " VNĐ", COL_SUCCESS));
        pnlKPI.add(taoOThongKe("THU NHẬP CAO NHẤT", String.format("%,d", luongCaoNhat) + " VNĐ", COL_INFO));
        pnlKPI.add(taoOThongKe("THU NHẬP TRUNG BÌNH", String.format("%,d", luongTB) + " VNĐ", COL_WARNING));

        // 3. Ráp vào Panel chính
        pnlMain.add(pnlKPI, BorderLayout.NORTH); // KPI nằm trên
        pnlMain.add(createChartPanel(), BorderLayout.CENTER); // Biểu đồ nằm dưới (Chiếm phần còn lại)

        return pnlMain;
    }

    // ========================================================================
    // TAB 2: TÀI CHÍNH PHÒNG BAN (Phân tích chi phí)
    // ========================================================================
    // ========================================================================
    // TAB 2: TÀI CHÍNH PHÒNG BAN (Đã nâng cấp biểu đồ)
    // ========================================================================
    private JPanel taoPanelTaiChinhPhongBan() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnl.setBackground(Color.WHITE);

        // Header ghi chú
        JLabel lblNote = new JLabel("<html><i>* Bảng phân tích chi phí lương và so sánh mức thu nhập trung bình giữa các khối phòng ban.</i></html>");
        lblNote.setBorder(new EmptyBorder(0, 5, 5, 0));
        pnl.add(lblNote, BorderLayout.NORTH);

        String[] cols = {"Tên Phòng Ban", "Nhân Sự", "Tổng Chi Phí Lương (VNĐ)", "Lương TB (VNĐ)", "Tỷ Trọng (%)"};
        DefaultTableModel modelPB = new DefaultTableModel(cols, 0);
        JTable tblPB = new JTable(modelPB);
        styleTable(tblPB);

        // --- TÍNH TOÁN DỮ LIỆU ---
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Long> sumMap = new HashMap<>();
        long totalCompanySalary = 0;

        for (int i = 0; i < mainModel.getRowCount(); i++) {
            String phong = mainModel.getValueAt(i, 2).toString(); 
            String luongStr = mainModel.getValueAt(i, 9).toString().replace(",", ""); 
            long luong = 0;
            try { luong = Long.parseLong(luongStr); } catch (Exception e) {}

            countMap.put(phong, countMap.getOrDefault(phong, 0) + 1);
            sumMap.put(phong, sumMap.getOrDefault(phong, 0L) + luong);
            totalCompanySalary += luong;
        }

        if (totalCompanySalary == 0) totalCompanySalary = 1; 

        // Đổ dữ liệu vào Bảng
        for (String phong : countMap.keySet()) {
            int soNV = countMap.get(phong);
            long tongL = sumMap.get(phong);
            long tbL = tongL / soNV;
            double tyTrong = (double) tongL / totalCompanySalary * 100;

            modelPB.addRow(new Object[]{
                phong.toUpperCase(), 
                soNV + " người", 
                String.format("%,d", tongL), 
                String.format("%,d", tbL),
                String.format("%.1f", tyTrong) + "%"
            });
        }

        // --- PHẦN MỚI: THÊM BIỂU ĐỒ VÀO DƯỚI ĐÁY ---
        // Gọi hàm vẽ biểu đồ (viết ở Bước 2)
        JPanel pnlCharts = createDeptFinanceCharts(sumMap, countMap); 
        pnlCharts.setPreferredSize(new Dimension(1000, 320)); // Chiều cao vừa đủ đẹp
        
        pnl.add(new JScrollPane(tblPB), BorderLayout.CENTER); // Bảng nằm giữa
        pnl.add(pnlCharts, BorderLayout.SOUTH); // Biểu đồ nằm đáy
        
        return pnl;
    }

    // ========================================================================
    // TAB 3: DANH SÁCH KHEN THƯỞNG
    // ========================================================================
    private JPanel taoPanelKhenThuong() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnl.setBackground(Color.WHITE);

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban", "Nội Dung", "Số Tiền (VNĐ)"};
        DefaultTableModel modelThuong = new DefaultTableModel(cols, 0);
        JTable tblThuong = new JTable(modelThuong);
        styleTable(tblThuong);
        
        // Render màu xanh cho tiền thưởng
        tblThuong.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                super.setValue(value);
                setForeground(COL_SUCCESS);
                setFont(getFont().deriveFont(Font.BOLD));
            }
        });

        long tongTienThuong = 0;
        int countThuong = 0;

        for (int i = 0; i < mainModel.getRowCount(); i++) {
            String thuongStr = mainModel.getValueAt(i, 6).toString().replace(",", ""); // Cột 6: Thưởng
            long thuong = 0;
            try { thuong = Long.parseLong(thuongStr); } catch (Exception e) {}

            if (thuong > 0) {
                modelThuong.addRow(new Object[]{
                    mainModel.getValueAt(i, 0),
                    mainModel.getValueAt(i, 1),
                    mainModel.getValueAt(i, 2),
                    "Thưởng KPI / Doanh số", 
                    String.format("%,d", thuong)
                });
                tongTienThuong += thuong;
                countThuong++;
            }
        }

        // Panel Info Header
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlInfo.setBackground(new Color(235, 250, 235)); // Xanh nhạt
        pnlInfo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_SUCCESS));
        
        JLabel lblCount = new JLabel("Nhân sự được thưởng: " + countThuong);
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCount.setForeground(new Color(20, 100, 20));
        
        JLabel lblSum = new JLabel("|   Tổng ngân sách thưởng: " + String.format("%,d", tongTienThuong) + " VNĐ");
        lblSum.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSum.setForeground(COL_SUCCESS);

        pnlInfo.add(lblCount);
        pnlInfo.add(lblSum);

        pnl.add(pnlInfo, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblThuong), BorderLayout.CENTER);
        return pnl;
    }

    // ========================================================================
    // TAB 4: THEO DÕI VI PHẠM
    // ========================================================================
    private JPanel taoPanelViPham() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnl.setBackground(Color.WHITE);

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban", "Số Ngày Trễ", "Tiền Phạt (VNĐ)"};
        DefaultTableModel modelPhat = new DefaultTableModel(cols, 0);
        JTable tblPhat = new JTable(modelPhat);
        styleTable(tblPhat);
        
        // Render màu đỏ cho tiền phạt
        tblPhat.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public void setValue(Object value) {
                super.setValue(value);
                setForeground(COL_DANGER);
                setFont(getFont().deriveFont(Font.BOLD));
            }
        });

        long tongTienPhat = 0;
        int countViPham = 0;

        for (int i = 0; i < mainModel.getRowCount(); i++) {
            String ngayTreStr = mainModel.getValueAt(i, 7).toString().replace(" ngày", "").trim(); // Cột 7
            int ngayTre = 0;
            try { ngayTre = Integer.parseInt(ngayTreStr); } catch (Exception e) {}
            
            String tienPhatStr = mainModel.getValueAt(i, 8).toString().replace(",", ""); // Cột 8
            long tienPhat = 0;
            try { tienPhat = Long.parseLong(tienPhatStr); } catch (Exception e) {}

            if (ngayTre > 0) {
                modelPhat.addRow(new Object[]{
                    mainModel.getValueAt(i, 0),
                    mainModel.getValueAt(i, 1),
                    mainModel.getValueAt(i, 2),
                    ngayTre + " ngày",
                    String.format("%,d", tienPhat)
                });
                tongTienPhat += tienPhat;
                countViPham++;
            }
        }

        // Panel Info Header
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlInfo.setBackground(new Color(255, 240, 240)); // Đỏ nhạt
        pnlInfo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COL_DANGER));
        
        JLabel lblCount = new JLabel("Nhân sự vi phạm: " + countViPham);
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCount.setForeground(new Color(150, 20, 20));
        
        JLabel lblSum = new JLabel("|   Tổng tiền phạt thu về: " + String.format("%,d", tongTienPhat) + " VNĐ");
        lblSum.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSum.setForeground(COL_DANGER);

        pnlInfo.add(lblCount);
        pnlInfo.add(lblSum);

        pnl.add(pnlInfo, BorderLayout.NORTH);
        pnl.add(new JScrollPane(tblPhat), BorderLayout.CENTER);
        return pnl;
    }

    // ========================================================================
    // CÁC HÀM HỖ TRỢ (HELPER METHODS)
    // ========================================================================
    private JPanel taoOThongKe(String title, String value, Color color) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(color);
        // Tạo viền đậm hơn màu nền một chút
        pnl.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
        
        JLabel lblVal = new JLabel(value, SwingConstants.CENTER);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblVal.setForeground(Color.WHITE);
        
        JLabel lblTit = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(new Color(255, 255, 255, 220));
        lblTit.setBorder(new EmptyBorder(10, 0, 10, 0));

        pnl.add(lblVal, BorderLayout.CENTER);
        pnl.add(lblTit, BorderLayout.SOUTH);
        return pnl;
    }
    
    private JPanel createTabTopThuNhap() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        // Tiêu đề
        JLabel lblTitle = new JLabel("TOP 5 NHÂN VIÊN CÓ THỰC LĨNH CAO NHẤT THÁNG", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(231, 76, 60)); // Màu đỏ cam nổi bật
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 15, 0));
        p.add(lblTitle, BorderLayout.NORTH);

        // Xử lý dữ liệu: Lấy danh sách từ DAO và sắp xếp giảm dần theo Thực Lĩnh
        dao.NhanVienDAO dao = new dao.NhanVienDAO();
        List<entity.NhanVien> list = dao.layDanhSachNhanVien("NV.MaNV ASC"); // Lấy hết về rồi tự sort
        
        Collections.sort(list, (o1, o2) -> Long.compare(o2.getThucLinh(), o1.getThucLinh()));

        // Tạo bảng
        String[] cols = {"Hạng", "Mã NV", "Họ Tên", "Phòng Ban", "Thực Lĩnh (VNĐ)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        
        // Chỉ lấy Top 5 (hoặc ít hơn nếu danh sách ko đủ 5)
        int top = Math.min(5, list.size());
        for (int i = 0; i < top; i++) {
            entity.NhanVien nv = list.get(i);
            model.addRow(new Object[]{
                (i + 1), // Hạng
                nv.getMaNV(),
                nv.getHoTen(),
                nv.getTenPB() != null ? nv.getTenPB() : nv.getMaPB(),
                String.format("%,d", nv.getThucLinh())
            });
        }

        JTable tbl = new JTable(model);
        tbl.setRowHeight(35);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Căn giữa cột Hạng và Số tiền
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tbl.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        javax.swing.table.DefaultTableCellRenderer rightRenderer = new javax.swing.table.DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tbl.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }
    
    private JPanel createTabThamNien() {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        dao.NhanVienDAO dao = new dao.NhanVienDAO();
        List<entity.NhanVien> list = dao.layDanhSachNhanVien("NV.MaNV ASC");

        int duoi1Nam = 0;
        int tu1den3 = 0;
        int tren3Nam = 0;
        LocalDate now = LocalDate.now();

        for (entity.NhanVien nv : list) {
            if (nv.getNgayVaoLam() != null) {
                LocalDate start = new java.util.Date(nv.getNgayVaoLam().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                int years = Period.between(start, now).getYears();
                if (years < 1) duoi1Nam++;
                else if (years <= 3) tu1den3++;
                else tren3Nam++;
            }
        }

        int total = Math.max(list.size(), 1); // Tránh chia cho 0

        // Vẽ các thanh thống kê
        addBar(p, "Nhân sự mới (< 1 năm)", duoi1Nam, total, new Color(46, 204, 113), 50);
        addBar(p, "Nhân sự ổn định (1 - 3 năm)", tu1den3, total, new Color(52, 152, 219), 150);
        addBar(p, "Nhân sự cốt cán (> 3 năm)", tren3Nam, total, new Color(155, 89, 182), 250);
        
        // Thêm ghi chú
        JLabel lblNote = new JLabel("<html><i>* Thống kê này giúp đánh giá độ ổn định nhân sự của công ty.<br>Tỷ lệ nhân viên cốt cán cao chứng tỏ chế độ đãi ngộ tốt.</i></html>");
        lblNote.setBounds(50, 350, 600, 40);
        lblNote.setForeground(Color.GRAY);
        p.add(lblNote);

        return p;
    }

    private void addBar(JPanel p, String title, int count, int total, Color c, int y) {
        JLabel lbl = new JLabel(title + ": " + count + " người");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(50, y, 300, 30);
        p.add(lbl);

        javax.swing.JProgressBar bar = new javax.swing.JProgressBar(0, total);
        bar.setValue(count);
        bar.setStringPainted(true);
        bar.setString(String.format("%.1f%%", (double)count/total * 100));
        bar.setForeground(c);
        bar.setBackground(new Color(230, 230, 230));
        bar.setBounds(50, y + 35, 600, 25);
        p.add(bar);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
    }
    
 // Hàm tạo Panel chứa 2 biểu đồ Dashboard (Admin)
    private JPanel createChartPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0)); // Chia đôi màn hình
        p.setBackground(Color.WHITE);
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- BƯỚC 1: CHUẨN BỊ DỮ LIỆU ---
        dao.NhanVienDAO dao = new dao.NhanVienDAO();
        List<entity.NhanVien> list = dao.layDanhSachNhanVien("NV.MaNV ASC");

        // Map lưu: Tên Phòng -> Số lượng nhân viên
        java.util.Map<String, Integer> mapCount = new java.util.HashMap<>();
        // Map lưu: Tên Phòng -> Tổng lương
        java.util.Map<String, Long> mapSalary = new java.util.HashMap<>();

        for (entity.NhanVien nv : list) {
            String pb = nv.getTenPB() != null ? nv.getTenPB() : "Khác";
            mapCount.put(pb, mapCount.getOrDefault(pb, 0) + 1);
            mapSalary.put(pb, mapSalary.getOrDefault(pb, 0L) + nv.getThucLinh());
        }

        // --- BƯỚC 2: VẼ BIỂU ĐỒ TRÒN (CƠ CẤU NHÂN SỰ) ---
        JPanel pnlPie = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int size = Math.min(w, h) - 80;
                int x = 20;
                int y = (h - size) / 2;

                int totalNV = list.size();
                int startAngle = 90;
                
                // Bảng màu cho các phòng ban
                Color[] colors = {
                    new Color(52, 152, 219), new Color(46, 204, 113), 
                    new Color(155, 89, 182), new Color(241, 196, 15), 
                    new Color(230, 126, 34), new Color(231, 76, 60)
                };

                int colorIdx = 0;
                int legendY = y + 20;

                for (String key : mapCount.keySet()) {
                    int count = mapCount.get(key);
                    int angle = (int) Math.round((count * 360.0) / totalNV);

                    g2.setColor(colors[colorIdx % colors.length]);
                    g2.fillArc(x, y, size, size, startAngle, angle);
                    
                    // Vẽ chú thích (Legend) bên phải
                    g2.fillRect(x + size + 20, legendY, 15, 15);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    String percent = String.format("%.1f%%", (count * 100.0) / totalNV);
                    g2.drawString(key + " (" + count + " - " + percent + ")", x + size + 45, legendY + 12);
                    
                    legendY += 30;
                    startAngle += angle;
                    colorIdx++;
                }
                
                // Tiêu đề biểu đồ
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("CƠ CẤU NHÂN SỰ", x + size/3, y - 10);
            }
        };
        pnlPie.setBackground(Color.WHITE);
        pnlPie.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(230, 230, 230)));
        p.add(pnlPie);

        // --- BƯỚC 3: VẼ BIỂU ĐỒ CỘT (TỶ TRỌNG QUỸ LƯƠNG) ---
        JPanel pnlBar = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int pad = 40;
                
                // Tìm lương cao nhất để chia tỷ lệ
                long maxSalary = 0;
                for (long val : mapSalary.values()) if (val > maxSalary) maxSalary = val;
                if (maxSalary == 0) maxSalary = 1;

                int barW = (w - pad * 2) / Math.max(1, mapSalary.size()) - 20;
                if (barW > 60) barW = 60; // Giới hạn độ rộng cột
                
                int x = pad;
                
                // Tiêu đề
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("QUỸ LƯƠNG PHÒNG BAN", w/3, 30);

                int i = 0;
                Color[] colors = {new Color(231, 76, 60), new Color(52, 152, 219), new Color(46, 204, 113)};

                for (String key : mapSalary.keySet()) {
                    long val = mapSalary.get(key);
                    int barH = (int) ((val * (h - 100)) / maxSalary);
                    int y = h - pad - barH;

                    g2.setColor(colors[i % colors.length]);
                    g2.fillRect(x, y, barW, barH);
                    
                    // Vẽ số tiền trên cột (Rút gọn Triệu)
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    String money = String.format("%.1fM", val / 1000000.0);
                    g2.drawString(money, x + (barW - g2.getFontMetrics().stringWidth(money))/2, y - 5);
                    
                    // Vẽ tên phòng dưới cột (Cắt ngắn nếu dài)
                    String shortName = key.length() > 10 ? key.substring(0, 8) + ".." : key;
                    g2.drawString(shortName, x, h - pad + 20);

                    x += barW + 20;
                    i++;
                }
                
                // Vẽ trục hoành
                g2.setColor(Color.GRAY);
                g2.drawLine(pad - 10, h - pad, w - 10, h - pad);
            }
        };
        pnlBar.setBackground(Color.WHITE);
        pnlBar.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(230, 230, 230)));
        p.add(pnlBar);

        return p;
    }
    
 // Hàm vẽ 2 biểu đồ cho Tab Tài Chính
    private String layTenVietTat(String tenPhong) {
        String temp = tenPhong.toLowerCase().replace("phòng", "").trim();
        if (temp.isEmpty()) return tenPhong;
        
        String[] parts = temp.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.substring(0, 1).toUpperCase());
            }
        }
        return sb.toString();
    }

    private JPanel createDeptFinanceCharts(Map<String, Long> sumMap, Map<String, Integer> countMap) {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY), 
            "TRỰC QUAN HÓA SỐ LIỆU (TOP 5 + KHÁC)", 0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.GRAY
        ));

        Map<String, Long> sortedPieMap = new java.util.LinkedHashMap<>();
        List<Map.Entry<String, Long>> listPie = new java.util.ArrayList<>(sumMap.entrySet());
        listPie.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue())); 

        long otherSum = 0;
        for (int i = 0; i < listPie.size(); i++) {
            if (i < 5) {
                sortedPieMap.put(listPie.get(i).getKey(), listPie.get(i).getValue());
            } else {
                otherSum += listPie.get(i).getValue();
            }
        }
        if (otherSum > 0) sortedPieMap.put("Các phòng khác", otherSum);

        Map<String, Long> avgMapRaw = new HashMap<>();
        for (String key : sumMap.keySet()) {
            avgMapRaw.put(key, sumMap.get(key) / Math.max(1, countMap.get(key)));
        }
        
        List<Map.Entry<String, Long>> listBar = new java.util.ArrayList<>(avgMapRaw.entrySet());
        listBar.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<String, Long> sortedBarMap = new java.util.LinkedHashMap<>();
        long otherAvgSum = 0;
        int otherCount = 0;
        
        for (int i = 0; i < listBar.size(); i++) {
            if (i < 5) {
                sortedBarMap.put(listBar.get(i).getKey(), listBar.get(i).getValue());
            } else {
                otherAvgSum += listBar.get(i).getValue();
                otherCount++;
            }
        }
        if (otherCount > 0) sortedBarMap.put("Khác", otherAvgSum / otherCount);

        JPanel pnlPie = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                long totalSalary = 0;
                for (long v : sortedPieMap.values()) totalSalary += v;
                if (totalSalary == 0) totalSalary = 1;

                int size = Math.min(getWidth(), getHeight()) - 60;
                int x = 20;
                int y = (getHeight() - size) / 2;
                int startAngle = 90;
                
                Color[] colors = {COL_PRIMARY, COL_SUCCESS, COL_DANGER, COL_WARNING, COL_INFO, Color.GRAY};
                int i = 0;
                int legendY = y + 10;

                for (String key : sortedPieMap.keySet()) {
                    long val = sortedPieMap.get(key);
                    int angle = (int) Math.round((val * 360.0) / totalSalary);
                    
                    g2.setColor(key.equals("Các phòng khác") ? Color.LIGHT_GRAY : colors[i % (colors.length - 1)]);
                    g2.fillArc(x, y, size, size, startAngle, angle);
                    
                    g2.fillRect(x + size + 20, legendY, 12, 12);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    String percent = String.format("%.1f%%", (val * 100.0) / totalSalary);
                    
                    String displayName = key.length() > 25 ? key.substring(0, 22) + "..." : key;
                    g2.drawString(displayName + " (" + percent + ")", x + size + 40, legendY + 10);
                    
                    startAngle += angle;
                    legendY += 25;
                    i++;
                }
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.drawString("TỶ TRỌNG CHI PHÍ LƯƠNG", x + size/4, y - 10);
            }
        };
        pnlPie.setBackground(Color.WHITE);
        p.add(pnlPie);

        JPanel pnlBar = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int pad = 30;
                
                long maxAvg = 0;
                for (long val : sortedBarMap.values()) if (val > maxAvg) maxAvg = val;
                if (maxAvg == 0) maxAvg = 1;

                int numCols = sortedBarMap.size();
                int barW = (w - pad * 2) / Math.max(1, numCols) - 30;
                if (barW > 50) barW = 50;
                
                int x = pad + 10;
                int i = 0;
                Color[] colors = {COL_INFO, COL_WARNING, COL_SUCCESS, COL_PRIMARY, COL_DANGER, Color.GRAY};

                for (String key : sortedBarMap.keySet()) {
                    long avg = sortedBarMap.get(key);
                    int barH = (int) ((avg * (h - 80)) / maxAvg);
                    int y = h - pad - barH;

                    g2.setColor(key.equals("Khác") ? Color.LIGHT_GRAY : colors[i % (colors.length - 1)]);
                    g2.fillRect(x, y, barW, barH);
                    
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    String money = String.format("%.1fM", avg / 1000000.0);
                    g2.drawString(money, x + (barW - g2.getFontMetrics().stringWidth(money))/2, y - 5);
                    
                    String shortName;
                    if (key.equals("Khác")) {
                        shortName = "Khác";
                    } else {
                        shortName = layTenVietTat(key);
                    }
                    
                    g2.drawString(shortName, x + (barW - g2.getFontMetrics().stringWidth(shortName))/2, h - pad + 15);

                    x += barW + 30;
                    i++;
                }
                
                g2.setColor(Color.GRAY);
                g2.drawLine(pad, h - pad, w - pad, h - pad);
                
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.drawString("THU NHẬP TRUNG BÌNH CAO NHẤT", w/5, 20);
            }
        };
        pnlBar.setBackground(Color.WHITE);
        p.add(pnlBar);

        return p;
    }
}