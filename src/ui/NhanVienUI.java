package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import entity.NhanVien;

public class NhanVienUI {
    
    private QuanLyNhanVien nvUI;
    
    public NhanVienUI(QuanLyNhanVien nvUI) {
        this.nvUI = nvUI;
    }
    
    public JPanel createTabProfile(NhanVien myProfile) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JPanel pnlIdentity = new JPanel(null);
        pnlIdentity.setBounds(30, 30, 300, 400);
        pnlIdentity.setBackground(new Color(248, 250, 252));
        pnlIdentity.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "ĐỊNH DANH NHÂN SỰ", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 12), 
            Color.GRAY
        ));

        JLabel lblAvatar = new JLabel();
        lblAvatar.setBounds(50, 40, 200, 200);
        lblAvatar.setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY, 1));
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(Color.WHITE);
        lblAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        try {
            java.net.URL imgURL = getClass().getResource("/icon/user.png");
            if (imgURL != null) {
                javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imgURL);
                java.awt.Image img = icon.getImage().getScaledInstance(190, 190, java.awt.Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new javax.swing.ImageIcon(img));
            } else {
                lblAvatar.setText("NO IMG");
            }
        } catch (Exception ex) { lblAvatar.setText("ERR"); }
        pnlIdentity.add(lblAvatar);

        JLabel lblName = new JLabel(myProfile.getHoTen().toUpperCase(), JLabel.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(new Color(44, 62, 80));
        lblName.setBounds(10, 250, 280, 30);
        pnlIdentity.add(lblName);

        JLabel lblId = new JLabel("ID: " + myProfile.getMaNV(), JLabel.CENTER);
        lblId.setFont(new Font("Consolas", Font.BOLD, 14));
        lblId.setForeground(Color.GRAY);
        lblId.setBounds(10, 280, 280, 20);
        pnlIdentity.add(lblId);

        JButton btnDoiAnh = new JButton("Đổi Ảnh Đại Diện");
        btnDoiAnh.setBounds(75, 330, 150, 35);
        btnDoiAnh.setBackground(new Color(52, 152, 219));
        btnDoiAnh.setForeground(Color.WHITE);
        
        btnDoiAnh.addActionListener(e -> JOptionPane.showMessageDialog(p, "Tính năng đang phát triển!"));
        pnlIdentity.add(btnDoiAnh);

        p.add(pnlIdentity);

        JPanel pnlInfo = new JPanel(null);
        pnlInfo.setBounds(350, 30, 600, 400);
        pnlInfo.setBackground(Color.WHITE);
        pnlInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("HỒ SƠ CHI TIẾT"));

        int y = 40; int gap = 50;
        
        String emailFake = myProfile.getMaNV().toLowerCase() + "@konami.com";
        
        String dept = "";
        if (myProfile.getTenPB() != null && !myProfile.getTenPB().isEmpty()) {
            dept = myProfile.getTenPB();
        } else if (myProfile.getMaPB() != null && !myProfile.getMaPB().isEmpty()) {
            dept = "Mã phòng: " + myProfile.getMaPB();
        } else {
            dept = "Chưa phân bổ";
        }
        addFancyField(pnlInfo, "Phòng Ban Trực Thuộc:", dept, "/icon/department.png", 30, y);
        addFancyField(pnlInfo, "Email Công Việc:", emailFake, "/icon/email.png", 30, y + gap);
        
        String ngayVao = "N/A";
        int years = 0;
        if (myProfile.getNgayVaoLam() != null) {
            ngayVao = new java.text.SimpleDateFormat("dd/MM/yyyy").format(myProfile.getNgayVaoLam());
            LocalDate start = new java.util.Date(myProfile.getNgayVaoLam().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            years = Period.between(start, LocalDate.now()).getYears();
        }
        addFancyField(pnlInfo, "Ngày Gia Nhập:", ngayVao, "/icon/calendar.png", 30, y + gap*2);

        JLabel lblLevel = new JLabel("Cấp Độ Thâm Niên (Level " + years + ")");
        lblLevel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLevel.setBounds(30, y + gap*3 + 10, 200, 20);
        pnlInfo.add(lblLevel);

        javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar(0, 10);
        progressBar.setValue(years);
        progressBar.setStringPainted(true);
        progressBar.setString(years + " năm cống hiến");
        progressBar.setForeground(new Color(230, 126, 34));
        progressBar.setBounds(30, y + gap*3 + 35, 520, 25);
        pnlInfo.add(progressBar);

        JButton btnDB = new JButton("Tra Cứu Danh Bạ Đồng Nghiệp");
        btnDB.setBounds(30, 330, 300, 45);
        
        java.net.URL iconURL = getClass().getResource("/icon/search.png");
        if (iconURL != null) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(iconURL);
            java.awt.Image img = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
            btnDB.setIcon(new javax.swing.ImageIcon(img));
            btnDB.setIconTextGap(15);
        }
        

        btnDB.addActionListener(e -> nvUI.view.hienThiCuaSoDanhBa());
        pnlInfo.add(btnDB);

        p.add(pnlInfo);
        return p;
    }
    
    private void addFancyField(JPanel p, String title, String value, String iconPath, int x, int y) {
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        lblTitle.setBounds(x, y, 150, 20);
        p.add(lblTitle);

        JTextField txt = new JTextField(value);
        txt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txt.setBounds(x, y + 20, 520, 30);
        txt.setEditable(false);
        txt.setFocusable(false);
        txt.setBackground(new Color(250, 250, 250));
        txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 0)
        ));
        p.add(txt);
    }
    
    public JPanel createTabIncome(entity.NhanVien myProfile) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        long luongCung = (long) (myProfile.getLuongCoBan() * myProfile.getHeSoLuong());
        double phanTramTN = logic.ThuatToanTangLuong.tinhPhuCapThamNien(myProfile.getNgayVaoLam());
        long tienThamNien = (long) (luongCung * phanTramTN);
        long phuCapAn = 730000;
        long phuCapXang = 300000;
        long thuongDoanhSo = 500000;
        long thuongTet = 500000 * 7; 
        long phat = myProfile.getTienPhat();
        
        long tongPhuCap = phuCapAn + phuCapXang;
        long tongThuong = myProfile.getTienThuong() + thuongDoanhSo + thuongTet;
        long thucLinh = luongCung + tienThamNien + phuCapAn + phuCapXang + tongThuong - phat;
        
        JPanel pnlCharts = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlCharts.setBounds(30, 20, 400, 600);
        pnlCharts.setBackground(Color.WHITE);
        pnlCharts.setBorder(javax.swing.BorderFactory.createTitledBorder("TRỰC QUAN HÓA THU NHẬP"));

        JPanel pnlBarChart = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                int topMargin = 40, bottomMargin = 30, chartH = h - topMargin - bottomMargin;
                long maxVal = Math.max(thucLinh, Math.max(luongCung, tongThuong));
                if (maxVal == 0) maxVal = 1;
                int barW = (w - 80) / 3; if (barW > 60) barW = 60;
                int startX = (w - (barW * 3 + 40)) / 2;
                drawBar(g2, luongCung, maxVal, startX, topMargin, chartH, barW, new Color(52, 152, 219), "Lương");
                drawBar(g2, tongThuong + tongPhuCap, maxVal, startX + barW + 20, topMargin, chartH, barW, new Color(46, 204, 113), "Thưởng/PC");
                drawBar(g2, thucLinh, maxVal, startX + (barW + 20) * 2, topMargin, chartH, barW, new Color(231, 76, 60), "Thực Lĩnh");
            }
            private void drawBar(Graphics2D g, long val, long max, int x, int topY, int h, int w, Color c, String lbl) {
                int barHeight = (int)((double)val / max * h);
                int y = topY + h - barHeight;
                g.setColor(c); g.fillRect(x, y, w, barHeight);
                g.setColor(Color.BLACK); g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String txt = val > 1000000 ? String.format("%.1fM", val/1000000.0) : String.format("%.0fk", val/1000.0);
                g.drawString(txt, x + (w - g.getFontMetrics().stringWidth(txt))/2, y - 5);
                g.drawString(lbl, x + (w - g.getFontMetrics().stringWidth(lbl))/2, topY + h + 20);
            }
        };
        pnlBarChart.setBackground(Color.WHITE); pnlCharts.add(pnlBarChart);

        JPanel pnlPieChart = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                long total = luongCung + tongPhuCap + tongThuong; if (total == 0) return;
                int angleLuong = (int) (360.0 * luongCung / total);
                int anglePC = (int) (360.0 * tongPhuCap / total);
                int angleThuong = 360 - angleLuong - anglePC;
                int size = Math.min(getWidth(), getHeight()) - 110;
                int x = (getWidth() - size) / 2 - 80, y = (getHeight() - size) / 2;
                g2.setColor(new Color(52, 152, 219)); g2.fillArc(x, y, size, size, 90, angleLuong);
                g2.setColor(new Color(46, 204, 113)); g2.fillArc(x, y, size, size, 90 + angleLuong, anglePC);
                g2.setColor(new Color(243, 156, 18)); g2.fillArc(x, y, size, size, 90 + angleLuong + anglePC, angleThuong);
                drawLegend(g2, x + size + 20, y + size/2 - 40, new Color(52, 152, 219), "Lương (" + (int)(100.0*luongCung/total) + "%)");
                drawLegend(g2, x + size + 20, y + size/2 - 15, new Color(46, 204, 113), "Phụ Cấp (" + (int)(100.0*tongPhuCap/total) + "%)");
                drawLegend(g2, x + size + 20, y + size/2 + 10, new Color(243, 156, 18), "Khác (" + (int)(100.0*tongThuong/total) + "%)");
            }
            private void drawLegend(Graphics2D g, int x, int y, Color c, String text) {
                g.setColor(c); g.fillRect(x, y, 10, 10); g.setColor(Color.BLACK); g.setFont(new Font("Segoe UI", Font.PLAIN, 11)); g.drawString(text, x + 15, y + 10);
            }
        };
        pnlPieChart.setBackground(Color.WHITE); pnlCharts.add(pnlPieChart);
        p.add(pnlCharts);

        String[] columns = {"Khoản Mục", "Số Tiền (VNĐ)"};
        Object[][] data = {
            {"Lương Cứng (HS " + myProfile.getHeSoLuong() + ")", String.format("%,d", luongCung)},
            {"Phụ Cấp Thâm Niên (" + String.format("%.0f%%", phanTramTN * 100) + ")", String.format("%,d", tienThamNien)},
            {"Phụ Cấp Ăn Trưa", String.format("%,d", phuCapAn)},
            {"Phụ Cấp Xăng Xe", String.format("%,d", phuCapXang)},
            {"Thưởng Doanh Số", String.format("%,d", thuongDoanhSo)},
            {"Thưởng Tết (Dự kiến)", String.format("%,d", thuongTet)},
            {"Trừ Phạt Đi Trễ", String.format("%,d", -phat)}, 
            {"TỔNG THỰC LĨNH", String.format("%,d", thucLinh)}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30); 
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getRowCount() - 1) { 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14)); c.setForeground(Color.BLUE);
                } else if (value.toString().startsWith("-")) { 
                    c.setForeground(Color.RED);
                } else { c.setForeground(Color.BLACK); }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(450, 25, 500, 235); 
        p.add(scroll);

        int thamNien = 0;
        if (myProfile.getNgayVaoLam() != null) {
            LocalDate start = new java.util.Date(myProfile.getNgayVaoLam().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            thamNien = Period.between(start, LocalDate.now()).getYears();
        }
        
        JPanel pnlPhucLoi = new JPanel(new GridLayout(2, 2, 10, 10)); 
        pnlPhucLoi.setBounds(450, 280, 500, 320);
        pnlPhucLoi.setBackground(Color.WHITE);
        pnlPhucLoi.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            "CHẾ ĐỘ PHÚC LỢI & ĐÃI NGỘ (Level " + thamNien + ")",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 13), new Color(0, 150, 136)
        ));

        int phepNam = 12 + (thamNien / 5); 
        String levelBh = thamNien >= 3 ? "Bảo Việt Care (VIP)" : "BHYT Cơ Bản";
        Color colorBh = thamNien >= 3 ? new Color(255, 193, 7) : new Color(33, 150, 243); 
        
        pnlPhucLoi.add(createBenefitItem("🏥 Bảo Hiểm Sức Khỏe", levelBh, "Đóng 100% chi phí", colorBh));
        pnlPhucLoi.add(createBenefitItem("🏖️ Nghỉ Phép Năm", phepNam + " ngày/năm", "Được trả lương nếu không nghỉ hết", new Color(76, 175, 80)));
        pnlPhucLoi.add(createBenefitItem("✈️ Du Lịch Công Ty", "Gói Standard", "1 lần/năm (Team Building)", new Color(156, 39, 176)));
        pnlPhucLoi.add(createBenefitItem("🎁 Quà Sinh Nhật", "500,000 VNĐ", "Cộng trực tiếp vào lương tháng", new Color(233, 30, 99)));

        p.add(pnlPhucLoi);

        return p;
    }
    
    private JPanel createBenefitItem(String title, String value, String subtext, Color accentColor) {
        JPanel p = new JPanel(new GridLayout(3, 1));
        p.setBackground(new Color(250, 250, 250));
        p.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 12));
        lblTitle.setForeground(Color.GRAY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValue.setForeground(Color.BLACK);
        
        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblSub.setForeground(new Color(100, 100, 100));

        p.add(lblTitle);
        p.add(lblValue);
        p.add(lblSub);
        return p;
    }

    public JPanel createTabHistory(NhanVien myProfile) {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("NHẬT KÝ HOẠT ĐỘNG & BIẾN ĐỘNG NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(44, 62, 80));
        lblTitle.setBounds(30, 20, 450, 30);
        p.add(lblTitle);
        
        String[] cols = {"Thời Gian", "Hành Động", "Chi Tiết Thay Đổi", "Người Thực Hiện"};
        
        DefaultTableModel modelLS = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JTable tblLS = new JTable(modelLS);
        tblLS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblLS.setRowHeight(30);
        tblLS.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tblLS.getColumnModel().getColumn(0).setPreferredWidth(140); 
        tblLS.getColumnModel().getColumn(1).setPreferredWidth(120); 
        tblLS.getColumnModel().getColumn(2).setPreferredWidth(450); 
        tblLS.getColumnModel().getColumn(3).setPreferredWidth(120); 
        
        List<String[]> logs = nvUI.dao.layLichSuCuaNhanVien(myProfile.getMaNV());
        
        if (logs.isEmpty()) {
            modelLS.addRow(new Object[]{"", "Chưa có dữ liệu", "Nhân viên này chưa có ghi nhận hoạt động nào trên hệ thống.", ""});
        } else {
            for (String[] row : logs) {
                modelLS.addRow(row);
            }
        }
        
        for (int i = 0; i < tblLS.getRowCount(); i++) {
            Object val = tblLS.getValueAt(i, 2);
            if (val != null) {
                String noiDung = val.toString(); 
                int soDong = noiDung.split("<br>").length; 
                if (soDong > 1) {
                    int chieuCaoCanThiet = soDong * 22 + 10;
                    tblLS.setRowHeight(i, chieuCaoCanThiet);
                }
            }
        }
        
        JScrollPane s = new JScrollPane(tblLS);
        s.setBounds(30, 60, 900, 300);
        s.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(220, 220, 220)));
        p.add(s);
        
        JLabel lblNote = new JLabel("<html><i>* Bảng này hiển thị toàn bộ lịch sử tăng/giảm lương, phạt vi phạm và cập nhật thông tin cá nhân.</i></html>");
        lblNote.setBounds(30, 370, 800, 20);
        lblNote.setForeground(Color.GRAY);
        p.add(lblNote);

        return p;
    }

    public JPanel createTabMailbox(NhanVien myProfile) {
        JPanel p = new JPanel(new java.awt.BorderLayout());
        p.setBackground(Color.WHITE);

        JPanel pnlHead = new JPanel(new java.awt.BorderLayout());
        pnlHead.setBackground(Color.WHITE);
        pnlHead.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("HỘP THƯ CÁ NHÂN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        pnlHead.add(lblTitle, java.awt.BorderLayout.WEST);

        JButton btnRefresh = new JButton("Làm mới 🔄");
        btnRefresh.setFont(new Font("Dialog", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(240, 240, 240));
        pnlHead.add(btnRefresh, java.awt.BorderLayout.EAST);
        
        p.add(pnlHead, java.awt.BorderLayout.NORTH);

        String[] cols = {"ID", "Tiêu Đề", "Ngày Nhận", "Trạng Thái"};
        DefaultTableModel modelThu = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tblThu = new JTable(modelThu);
        tblThu.setRowHeight(35);
        tblThu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tblThu.setShowVerticalLines(false);     
        tblThu.setShowHorizontalLines(true);    
        tblThu.setGridColor(new Color(220, 220, 220)); 
        tblThu.setIntercellSpacing(new java.awt.Dimension(0, 1)); 

        tblThu.getColumnModel().getColumn(0).setPreferredWidth(50);  
        tblThu.getColumnModel().getColumn(0).setMaxWidth(60);        
        
        tblThu.getColumnModel().getColumn(1).setPreferredWidth(500); 
        
        tblThu.getColumnModel().getColumn(2).setPreferredWidth(150); 
        
        tblThu.getColumnModel().getColumn(3).setPreferredWidth(100); 
        tblThu.getColumnModel().getColumn(3).setMaxWidth(120);       
        
        tblThu.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = table.getValueAt(row, 3).toString();
                
                if (status.equals("Chưa xem")) {
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    c.setForeground(new Color(0, 0, 0));
                } else {
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    c.setForeground(new Color(80, 80, 80)); 
                }
                
                if (isSelected) {
                    c.setBackground(new Color(232, 240, 254));
                } else {
                    c.setBackground(Color.WHITE); 
                }
                
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)); 
                return c;
            }
        });

        Runnable loadData = () -> {
            modelThu.setRowCount(0);
            List<String[]> listThu = nvUI.dao.layDanhSachThu(myProfile.getMaNV());
            tblThu.putClientProperty("dataHidden", listThu); 
            
            for (String[] row : listThu) {
                String trangThaiText = row[4].equals("0") || row[4].equals("Chưa xem") ? "Chưa xem" : "Đã xem";
                modelThu.addRow(new Object[]{row[0], row[1], row[3], trangThaiText});
            }
        };

        tblThu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = tblThu.getSelectedRow();
                    if (row >= 0) {
                        String idThu = tblThu.getValueAt(row, 0).toString();
                        String tieuDe = tblThu.getValueAt(row, 1).toString();
                        
                        @SuppressWarnings("unchecked")
                        List<String[]> listData = (List<String[]>) tblThu.getClientProperty("dataHidden");
                        String noiDung = "Lỗi tải nội dung";
                        for(String[] item : listData) {
                            if(item[0].equals(idThu)) {
                                noiDung = item[2];
                                break;
                            }
                        }

                        nvUI.dao.danhDauDaXem(idThu);
                        loadData.run();

                        javax.swing.JTextArea txtArea = new javax.swing.JTextArea(noiDung);
                        txtArea.setEditable(false);
                        txtArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
                        txtArea.setLineWrap(true);
                        txtArea.setWrapStyleWord(true);
                        txtArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        
                        JScrollPane scrollMsg = new JScrollPane(txtArea);
                        scrollMsg.setPreferredSize(new java.awt.Dimension(500, 300));
                        scrollMsg.setBorder(null);
                        
                        JOptionPane.showMessageDialog(p, scrollMsg, "📩 " + tieuDe, JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        });

        btnRefresh.addActionListener(e -> loadData.run());

        loadData.run();

        p.add(new JScrollPane(tblThu), java.awt.BorderLayout.CENTER);
        
        JLabel lblHint = new JLabel("<html><i>* Click đúp vào thư để xem chi tiết. Thư chưa đọc sẽ được in đậm.</i></html>", JLabel.CENTER);
        lblHint.setPreferredSize(new java.awt.Dimension(100, 30));
        lblHint.setForeground(Color.GRAY);
        p.add(lblHint, java.awt.BorderLayout.SOUTH);

        return p;
    }
}