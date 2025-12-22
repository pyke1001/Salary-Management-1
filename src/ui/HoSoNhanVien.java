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

public class HoSoNhanVien {
    
    private QuanLyNhanVien nvUI;
    
    public HoSoNhanVien(QuanLyNhanVien nvUI) {
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
        // Sửa: Dùng 'p' hoặc 'nvUI' làm parentComponent thay vì 'this'
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
        long phuCapAn = 730000;
        long phuCapXang = 300000;
        long thuongDoanhSo = 500000;
        long thuongTet = 500000 * 7; 
        long phat = myProfile.getTienPhat();
        
        long tongPhuCap = phuCapAn + phuCapXang;
        long tongThuong = myProfile.getTienThuong() + thuongDoanhSo + thuongTet;
        long thucLinh = luongCung + tongPhuCap + tongThuong - phat;

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

                int w = getWidth();
                int h = getHeight();
                
                int topMargin = 40; 
                int bottomMargin = 30;
                int chartH = h - topMargin - bottomMargin;
                
                long maxVal = Math.max(thucLinh, Math.max(luongCung, tongThuong));
                if (maxVal == 0) maxVal = 1;

                int barW = (w - 80) / 3; 
                if (barW > 60) barW = 60;
                
                int startX = (w - (barW * 3 + 40)) / 2;
                
                drawBar(g2, luongCung, maxVal, startX, topMargin, chartH, barW, new Color(52, 152, 219), "Lương");
                drawBar(g2, tongThuong + tongPhuCap, maxVal, startX + barW + 20, topMargin, chartH, barW, new Color(46, 204, 113), "Thưởng/PC");
                drawBar(g2, thucLinh, maxVal, startX + (barW + 20) * 2, topMargin, chartH, barW, new Color(231, 76, 60), "Thực Lĩnh");
            }

            private void drawBar(Graphics2D g, long val, long max, int x, int topY, int h, int w, Color c, String lbl) {
                int barHeight = (int)((double)val / max * h);
                int y = topY + h - barHeight;
                
                g.setColor(c);
                g.fillRect(x, y, w, barHeight);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, w, barHeight);
                
                g.setColor(Color.BLACK);
                g.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String txt = val > 1000000 ? String.format("%.1fM", val/1000000.0) : String.format("%.0fk", val/1000.0);
                g.drawString(txt, x + (w - g.getFontMetrics().stringWidth(txt))/2, y - 5);
                
                g.drawString(lbl, x + (w - g.getFontMetrics().stringWidth(lbl))/2, topY + h + 20);
            }
        };
        pnlBarChart.setBackground(Color.WHITE);
        pnlCharts.add(pnlBarChart);

        JPanel pnlPieChart = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                long total = luongCung + tongPhuCap + tongThuong;
                if (total == 0) return;

                int angleLuong = (int) (360.0 * luongCung / total);
                int anglePC = (int) (360.0 * tongPhuCap / total);
                int angleThuong = 360 - angleLuong - anglePC;

                int size = Math.min(getWidth(), getHeight()) - 110;
                int x = (getWidth() - size) / 2 - 80;
                int y = (getHeight() - size) / 2;
                
                g2.setColor(new Color(52, 152, 219));
                g2.fillArc(x, y, size, size, 90, angleLuong);
                
                g2.setColor(new Color(46, 204, 113));
                g2.fillArc(x, y, size, size, 90 + angleLuong, anglePC);
                
                g2.setColor(new Color(243, 156, 18));
                g2.fillArc(x, y, size, size, 90 + angleLuong + anglePC, angleThuong);

                int lx = x + size + 20;
                int ly = y + size/2 - 40;
                drawLegend(g2, lx, ly, new Color(52, 152, 219), "Lương Cứng (" + (int)(100.0*luongCung/total) + "%)");
                drawLegend(g2, lx, ly + 25, new Color(46, 204, 113), "Phụ Cấp (" + (int)(100.0*tongPhuCap/total) + "%)");
                drawLegend(g2, lx, ly + 50, new Color(243, 156, 18), "Thưởng & Khác (" + (int)(100.0*tongThuong/total) + "%)");
                
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                g2.drawString("Biểu đồ cơ cấu thu nhập", x + size/4, y + size + 20);
            }
            
            private void drawLegend(Graphics2D g, int x, int y, Color c, String text) {
                g.setColor(c);
                g.fillRect(x, y, 15, 15);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g.drawString(text, x + 20, y + 12);
            }
        };
        pnlPieChart.setBackground(Color.WHITE);
        pnlCharts.add(pnlPieChart);
        
        p.add(pnlCharts);

        String[] columns = {"Khoản Mục", "Số Tiền (VNĐ)"};
        Object[][] data = {
            {"Lương Cứng (HS " + myProfile.getHeSoLuong() + ")", String.format("%,d", luongCung)},
            {"Phụ Cấp Ăn Trưa", String.format("%,d", phuCapAn)},
            {"Phụ Cấp Xăng Xe", String.format("%,d", phuCapXang)},
            {"Thưởng Doanh Số", String.format("%,d", thuongDoanhSo)},
            {"Thưởng Tết (7 năm)", String.format("%,d", thuongTet)},
            {"Trừ Phạt Đi Trễ", String.format("%,d", -phat)}, 
            {"TỔNG THỰC LĨNH", String.format("%,d", thucLinh)}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row == table.getRowCount() - 1) { 
                    c.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    c.setForeground(Color.BLUE);
                } else if (value.toString().startsWith("-")) { 
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(450, 30, 500, 300); 
        p.add(scroll);

        JLabel lblNote = new JLabel("<html><i>* Số liệu trên bao gồm các khoản phụ cấp dự kiến chưa trừ BHXH.<br>* Biểu đồ tròn thể hiện tỷ trọng các nguồn thu nhập.</i></html>");
        lblNote.setBounds(450, 340, 400, 40);
        lblNote.setForeground(Color.GRAY);
        p.add(lblNote);

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

}