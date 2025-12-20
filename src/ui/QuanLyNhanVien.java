package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.NhanVienDAO;
import entity.NhanVien;
import logic.XuLyTangLuong;

public class QuanLyNhanVien extends NhanVienUI {

    private String lastMa = "";
    private String lastTen = "";
    private String lastPhong = "";
    private String lastLuong = "";
    private String taiKhoanHienTai;
    private String quyenHienTai;
    private String lastHeSo = "";
    private java.util.Set<String> secretsFound = new java.util.HashSet<>();
    private boolean isNeonUnlocked = false;
    private boolean isNeonActive = false;
    
    private JLabel lblContraHint;
    private JLabel lblSnake;
    private JLabel lblNeon;
    
    private NhanVienDAO dao = new NhanVienDAO();
    private static final long serialVersionUID = 2L;

    public QuanLyNhanVien(String username, String role) {
    	
        super();
        this.taiKhoanHienTai = username;
        this.quyenHienTai = role;
        
        napDuLieuPhongBan();

        cboLuongCoBan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Tất cả mức lương", "Dưới 5 triệu", "5 triệu - 10 triệu", 
            "10 triệu - 20 triệu", "Trên 20 triệu"
        }));

        cboHeSo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            "Tất cả hệ số", "Dưới 2.0", "2.0 - 3.0", "Trên 3.0"
        }));
        initEvents();
        phanQuyen();
        hienThiGoiYCheat();
    }
    
    private void napDuLieuPhongBan() {
        cboPhongBan.removeAllItems();
        cboPhongBan.addItem("Tất cả Phòng Ban"); 
        List<String> pbList = dao.layDanhSachPhongBan();
        for (String pb : pbList) {
            cboPhongBan.addItem(pb);
        }
    }

    private void phanQuyen() {
        if (quyenHienTai.equalsIgnoreCase("Admin")) {
            btnQuanLyTK.addActionListener(e -> hienThiDanhSachTaiKhoanAdmin());
            btnThuongNong.addActionListener(e -> xuLyThuongNong());
            btnChotThang.addActionListener(e -> xuLyChotThang());
            btnXuatExcel.addActionListener(e -> xuLyXuatExcel()); 
            
            btnPhat.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần phạt!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String maNV = table.getValueAt(row, 0).toString();
                String hoTen = table.getValueAt(row, 1).toString();
                
                String input = JOptionPane.showInputDialog(this, 
                    "Nhập số ngày đi trễ của " + hoTen + ":\n(Ví dụ: 1, 2, 3...)", 
                    "Xử Lý Vi Phạm", JOptionPane.QUESTION_MESSAGE);
                    
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        int soNgay = Integer.parseInt(input.trim());
                        if (soNgay < 0) {
                            JOptionPane.showMessageDialog(this, "Số ngày không được âm!");
                            return;
                        }
                        dao.capNhatPhat(maNV, soNgay); 
                        dao.ghiLichSu(maNV, "Phạt đi trễ", "Số ngày trễ: " + soNgay, taiKhoanHienTai);
                        JOptionPane.showMessageDialog(this, "✅ Đã ghi nhận phạt cho: " + hoTen);
                        loadData("NV.MaNV ASC");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên hợp lệ!");
                    }
                }
            });
            return;
        }

        if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
            getContentPane().removeAll();
            getContentPane().setLayout(new BorderLayout());

            NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
            String title = (myProfile != null) ? myProfile.getHoTen() : taiKhoanHienTai;
            setTitle("Hồ Sơ Cá Nhân - " + title);

            JTabbedPane tabPane = new JTabbedPane();
            tabPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

            tabPane.addTab("   Thông Tin Chung   ", null, createTabProfile(myProfile));
            tabPane.addTab("   Thu Nhập & Phúc Lợi   ", null, createTabIncome(myProfile));
            tabPane.addTab("   Lịch Sử & Vi Phạm   ", null, createTabHistory(myProfile));

            getContentPane().add(tabPane, BorderLayout.CENTER);
            
            JPanel pnlBot = new JPanel();
            
            JButton btnDoiMK = new JButton("Đổi Mật Khẩu");
            btnDoiMK.addActionListener(e -> hienThiFormDoiMatKhau());
            pnlBot.add(btnDoiMK);
            
            JButton btnLogOut = new JButton("Đăng Xuất");
            btnLogOut.addActionListener(e -> {
                dispose();
                new DangNhapUI().setVisible(true);
            });
            pnlBot.add(btnLogOut);
            
            getContentPane().add(pnlBot, BorderLayout.SOUTH);

            getContentPane().revalidate();
            getContentPane().repaint();
        }
    }
    
    private JPanel createTabProfile(NhanVien myProfile) {
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
        btnDoiAnh.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng đang phát triển!"));
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
        
        btnDB.addActionListener(e -> hienThiCuaSoDanhBa());
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

    private JPanel createTabIncome(entity.NhanVien myProfile) {
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
                return false; // Trả về false nghĩa là "Cấm sờ vào hiện vật"
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

    private JPanel createTabHistory(NhanVien myProfile) {
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
        
        List<String[]> logs = dao.layLichSuCuaNhanVien(myProfile.getMaNV());
        
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
    
    private void initEvents() {
        btnSortMa.addActionListener(e -> reloadTable("NV.MaNV ASC")); 
        btnSortTen.addActionListener(e -> reloadTable("NV.HoTen ASC")); 
        btnSortLuong.addActionListener(e -> reloadTable("NV.LuongCoBan DESC")); 

        btnTimKiem.addActionListener(e -> xuLyTimKiemDaNang()); 

        table.addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String maNV = table.getValueAt(row, 0).toString().trim();
                    txtMaNV.setText(maNV);
                    txtHoTen.setText(table.getValueAt(row, 1).toString());
                    cboPhongBan.setSelectedItem(table.getValueAt(row, 2).toString());
                    String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "");
                    cboLuongCoBan.setSelectedItem(luongStr);
                    cboHeSo.setSelectedItem(table.getValueAt(row, 5).toString());
                    txtMaNV.setEditable(false);
                    
                    if (isCheatMode()) { 
                        if (maNV.equalsIgnoreCase("NV30")) {
                        	unlockSecret("CONTRA");
                        }
                    }
                }
            }
        });

        btnThem.addActionListener(e -> { 
            if (txtMaNV.getText().equals("") || txtHoTen.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            try {
                long luong = layGiaTriTuCbo(cboLuongCoBan);
                float heSo = layGiaTriFloatTuCbo(cboHeSo);
                String phong = cboPhongBan.getSelectedItem() != null ? cboPhongBan.getSelectedItem().toString() : "";
                
                String maPB = dao.chuyenTenPhongThanhMa(phong);
                if (maPB == null || maPB.isEmpty()) maPB = phong; 

                NhanVien nv = new NhanVien(txtMaNV.getText(), txtHoTen.getText(), maPB, luong, heSo);

                if (dao.themNhanVien(nv)) {
                    JOptionPane.showMessageDialog(null, "✅ Thêm thành công!");
                    loadData("NV.MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Lỗi: Mã NV trùng hoặc sai định dạng!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Lỗi nhập liệu: " + ex.getMessage());
            }
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String maNV = txtMaNV.getText();
            NhanVien nvCu = dao.getNhanVienTheoMa(maNV);
            if (nvCu == null) return; 

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn sửa thông tin nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                String hoTenMoi = txtHoTen.getText();
                String phongMoi = cboPhongBan.getSelectedItem().toString(); 
                String maPBMoi = dao.chuyenTenPhongThanhMa(phongMoi);
                if (maPBMoi == null || maPBMoi.isEmpty()) maPBMoi = phongMoi;

                long luongMoi = layGiaTriTuCbo(cboLuongCoBan);
                float heSoMoi = layGiaTriFloatTuCbo(cboHeSo);

                NhanVien nvMoi = new NhanVien(maNV, hoTenMoi, maPBMoi, luongMoi, heSoMoi);
                
                if (dao.suaNhanVien(nvMoi)) {
                    StringBuilder sb = new StringBuilder("<html>");
                    boolean coThayDoi = false;

                    if (nvCu.getLuongCoBan() != luongMoi) {
                        sb.append(String.format("- Lương: %,d -> <font color='red'><b>%,d</b></font><br>", nvCu.getLuongCoBan(), luongMoi));
                        coThayDoi = true;
                    }
                    if (Float.compare(nvCu.getHeSoLuong(), heSoMoi) != 0) {
                        sb.append(String.format("- Hệ số: %s -> <font color='blue'><b>%s</b></font><br>", nvCu.getHeSoLuong(), heSoMoi));
                        coThayDoi = true;
                    }
                    String tenPBCu = nvCu.getTenPB() != null ? nvCu.getTenPB() : nvCu.getMaPB();
                    if (!tenPBCu.equals(phongMoi)) {
                        sb.append(String.format("- Phòng: %s -> <b>%s</b><br>", tenPBCu, phongMoi));
                        coThayDoi = true;
                    }
                    if (!nvCu.getHoTen().equals(hoTenMoi)) {
                        sb.append(String.format("- Tên: %s -> %s<br>", nvCu.getHoTen(), hoTenMoi));
                        coThayDoi = true;
                    }

                    sb.append("</html>");

                    if (coThayDoi) {
                        dao.ghiLichSu(maNV, "Sửa thông tin", sb.toString(), taiKhoanHienTai);
                    } else {
                        dao.ghiLichSu(maNV, "Sửa thông tin", "Không có thay đổi nào", taiKhoanHienTai);
                    }
                    
                    JOptionPane.showMessageDialog(this, "✅ Sửa thành công!");
                    loadData("NV.MaNV ASC");
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Sửa thất bại! (Kiểm tra lại Mã Phòng Ban)");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        });

        btnXoa.addActionListener(e -> {
            if (txtMaNV.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn nhân viên cần xóa!");
                return;
            }
            
            if (isCheatMode()) {
                java.awt.Toolkit.getDefaultToolkit().beep(); 
                int hoi = JOptionPane.showConfirmDialog(null, 
                    "⚠️ ALERT! Enemy spotted!\nBạn có chắc muốn 'tiêu diệt' dữ liệu này không?", 
                    "Metal Gear Alert", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE); 
                
                if (hoi != JOptionPane.YES_OPTION) return;
                unlockSecret("SNAKE");
            } else {
                int hoi = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa " + txtHoTen.getText() + "?\n(Cảnh báo: Tài khoản đăng nhập của người này cũng sẽ bị xóa)", "Cảnh báo", JOptionPane.YES_NO_OPTION);
                if (hoi != JOptionPane.YES_OPTION) return;
            }
            try {
                dao.xoaTaiKhoan(txtMaNV.getText()); 
            } catch (Exception ex) {
            }

            if (dao.xoaNhanVien(txtMaNV.getText())) {
                JOptionPane.showMessageDialog(null, "✅ Đã chuyển hồ sơ vào lưu trữ!");
                loadData("NV.MaNV ASC");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(null, "❌ Lỗi hệ thống!");
            }
        });
        btnLamMoi.addActionListener(e -> { 
            resetForm();
            lastMa = ""; lastTen = ""; lastPhong = ""; lastLuong = "";
            reloadTable("NV.MaNV ASC");
        });

        btnTangLuong.addActionListener(e -> xuLyTangLuong()); 
        btnGiamLuong.addActionListener(e -> xuLyGiamLuong()); 
        btnBaoLoi.addActionListener(e -> hienThiFormBaoLoi()); 
        
        btnMoTinhLuong.addActionListener(e -> {
            String hoTen = "";
            long luongCoBan = 0;
            
            if (quyenHienTai.equalsIgnoreCase("NhanVien")) {
                NhanVien myProfile = dao.getNhanVienTheoMa(taiKhoanHienTai);
                if (myProfile != null) {
                    hoTen = myProfile.getHoTen();
                    luongCoBan = myProfile.getLuongCoBan();
                }
            } else {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tính lương!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                hoTen = table.getValueAt(row, 1).toString();
                String luongStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "").trim();
                luongCoBan = Long.parseLong(luongStr);
            }

            TinhLuongUI cuaSoTinhLuong = new TinhLuongUI(hoTen, luongCoBan);
            cuaSoTinhLuong.setVisible(true);
        });

        btnThongKe.addActionListener(e -> { 
            ui.ThongKeUI thongKeForm = new ui.ThongKeUI(model);
            thongKeForm.setVisible(true);
        });
        
        btnLichSu.addActionListener(e -> hienThiBangLichSu());

        setHienThi(false);

        btnLoad.addActionListener(e -> { 
            setHienThi(true);
            loadData("NV.MaNV ASC");
            if (lblContraHint != null && isCheatMode()) {
                lblContraHint.setVisible(true);
                lblSnake.setVisible(true);
                lblNeon.setVisible(true);
                }
        });
        
        btnKhoiPhuc.addActionListener(e -> hienThiCuaSoKhoiPhuc());
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                requestFocusInWindow();
            }
        });

     // Dùng KeyboardFocusManager để bắt phím toàn cục (Global Hotkey)
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(new java.awt.KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_V) {
                        if (isNeonUnlocked) {
                            if (!isNeonActive) {
                                kichHoatGiaoDienHoangKim();
                                isNeonActive = true; // Đánh dấu đã bật
                            } else {
                                khoiPhucGiaoDienGoc();
                                isNeonActive = false; // Đánh dấu đã tắt
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    	private long layGiaTriTuCbo(JComboBox<String> cbo) throws NumberFormatException {
        String val = cbo.getSelectedItem() != null ? cbo.getSelectedItem().toString() : "0";
        val = val.replaceAll("[^0-9]", "");
        if (val.isEmpty()) return 0;
        return Long.parseLong(val);
    }
    
    private float layGiaTriFloatTuCbo(JComboBox<String> cbo) throws NumberFormatException {
        String val = cbo.getSelectedItem() != null ? cbo.getSelectedItem().toString() : "0";
        String[] parts = val.split(" ");
        return Float.parseFloat(parts[0]);
    }

    private void resetForm() { 
        txtMaNV.setText("");
        txtHoTen.setText("");
        cboPhongBan.setSelectedIndex(-1);
        cboLuongCoBan.setSelectedIndex(-1);
        cboHeSo.setSelectedIndex(-1);
        txtMaNV.setEditable(true);
    }

    private void setHienThi(boolean hien) { 
        lblMa.setVisible(hien); lblTen.setVisible(hien);
        lblPhong.setVisible(hien); lblLuong.setVisible(hien);
        lblHS.setVisible(hien); lblSort.setVisible(hien);

        txtMaNV.setVisible(hien); txtHoTen.setVisible(hien);
        cboPhongBan.setVisible(hien); cboLuongCoBan.setVisible(hien);
        cboHeSo.setVisible(hien);
        
        btnLichSu.setVisible(hien); 
        btnThem.setVisible(hien); btnSua.setVisible(hien);
        btnXoa.setVisible(hien); btnLamMoi.setVisible(hien);
        btnTangLuong.setVisible(hien); btnMoTinhLuong.setVisible(hien);
        btnThongKe.setVisible(hien); btnTimKiem.setVisible(hien);
        btnQuanLyTK.setVisible(hien); btnGiamLuong.setVisible(hien);
        btnBaoLoi.setVisible(hien); btnChotThang.setVisible(hien);
        btnXuatExcel.setVisible(hien); btnThuongNong.setVisible(hien);
        btnPhat.setVisible(hien);
   
        btnKhoiPhuc.setVisible(hien);

        btnSortMa.setVisible(hien); btnSortTen.setVisible(hien);
        btnSortLuong.setVisible(hien);
    }

    private void xuLyTangLuong() { 
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần tăng lương!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = table.getValueAt(row, 0).toString();
        String hoTen = table.getValueAt(row, 1).toString();
        String luongCuStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "").trim();
        double luongCu = Double.parseDouble(luongCuStr);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Chọn hình thức tăng lương cho: " + hoTen));

        String[] options = { 
            "KPI Loại A (Xuất sắc - 20%)", 
            "KPI Loại B (Giỏi - 10%)", 
            "KPI Loại C (Khá - 5%)", 
            "Theo Thâm Niên (Tự động)", 
            "Nhập tay %" 
        };
        JComboBox<String> cboOption = new JComboBox<>(options);
        panel.add(cboOption);

        int result = JOptionPane.showConfirmDialog(this, panel, "Xét Duyệt Tăng Lương", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            double luongMoi = 0;
            double phanTram = 0;
            int selectedIndex = cboOption.getSelectedIndex();

            try {
                if (selectedIndex == 0) luongMoi = XuLyTangLuong.tinhLuongTheoKPI(luongCu, "A");
                else if (selectedIndex == 1) luongMoi = XuLyTangLuong.tinhLuongTheoKPI(luongCu, "B");
                else if (selectedIndex == 2) luongMoi = XuLyTangLuong.tinhLuongTheoKPI(luongCu, "C");
                else if (selectedIndex == 3) { 
                    NhanVien nvFull = dao.getNhanVienTheoMa(maNV); 
                    if (nvFull != null && nvFull.getNgayVaoLam() != null) {
                        luongMoi = XuLyTangLuong.tinhLuongTheoThamNien(luongCu, nvFull.getNgayVaoLam());
                        if (luongMoi == luongCu) {
                            JOptionPane.showMessageDialog(this, "Nhân viên này thâm niên dưới 1 năm, chưa đủ điều kiện tăng!");
                            return;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu ngày vào làm!");
                        return;
                    }
                } else { 
                    String input = JOptionPane.showInputDialog(this, "Nhập % muốn tăng:", "5");
                    if (input == null || input.trim().isEmpty()) return;
                    phanTram = Double.parseDouble(input);
                    luongMoi = luongCu * (1 + phanTram / 100);
                }

                if (selectedIndex <= 3) {
                    phanTram = ((luongMoi - luongCu) / luongCu) * 100;
                }

                String msg = String.format("Lương cũ: %,.0f VNĐ\nLương mới: %,.0f VNĐ\n(Tăng: %.1f%%)\n\nXác nhận cập nhật?",
                    luongCu, luongMoi, phanTram);

                int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác Nhận", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    dao.tangLuong(maNV, phanTram);
                    String lyDo = options[selectedIndex]; 
                    String chiTiet = String.format("<html>- Lý do: %s<br>- Lương cũ: %,.0f VNĐ<br>- Lương mới: <font color='red'><b>%,.0f VNĐ</b></font> (Tăng %.1f%%)</html>", lyDo, luongCu, luongMoi, phanTram);
                    dao.ghiLichSu(maNV, "Tăng Lương", chiTiet, taiKhoanHienTai);
                    JOptionPane.showMessageDialog(this, "Đã tăng lương thành công!");
                    loadData("NV.MaNV ASC");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void fillTable(List < NhanVien > list) { 
        model.setRowCount(0);
        for (NhanVien nv: list) {
            java.util.Vector < Object > row = new java.util.Vector < > ();
            row.add(nv.getMaNV());
            row.add(nv.getHoTen());
            row.add(nv.getTenPB() != null ? nv.getTenPB() : nv.getMaPB());
            row.add(String.format("%,d", nv.getLuongCoBan()));
            if (nv.getNgayVaoLam() != null) {
                LocalDate start = new java.util.Date(nv.getNgayVaoLam().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate now = LocalDate.now();
                int soNam = Period.between(start, now).getYears();
                row.add(soNam + " năm");
            } else {
                row.add("Mới vào");
            }
            row.add(nv.getHeSoLuong());
            row.add(String.format("%,d", nv.getTienThuong()));
            row.add(nv.getSoNgayDiTre() + " ngày");
            row.add(String.format("%,d", nv.getTienPhat()));
            row.add(String.format("%,d", nv.getThucLinh()));
            model.addRow(row);
        }
    }

    private void loadData(String orderBy) { 
        String[] columns = {"Mã NV", "Họ Tên", "Phòng Ban", "Lương Cơ Bản", "Thâm Niên", "Hệ Số", "Thưởng", "Đi Trễ", "Tiền Phạt", "Thực Lĩnh"};
        model = new DefaultTableModel(columns, 0);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        List < NhanVien > list = dao.layDanhSachNhanVien(orderBy);
        fillTable(list);
    }

    private void reloadTable(String orderBy) { 
        List < NhanVien > list = dao.timKiemDaNang(lastMa, lastTen, lastPhong, lastLuong,lastHeSo, orderBy);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu!");
            return;
        }
        if (orderBy.contains("HoTen")) {
            Collections.sort(list, (nv1, nv2) -> {
                String ten1 = getTen(nv1.getHoTen());
                String ten2 = getTen(nv2.getHoTen());
                Collator collator = Collator.getInstance(Locale.of("vi", "VN"));
                int result = collator.compare(ten1, ten2);
                return result == 0 ? collator.compare(nv1.getHoTen(), nv2.getHoTen()) : result;
            });
        }
        fillTable(list);
    }

    private String getTen(String hoTen) { 
        if (hoTen == null || hoTen.trim().isEmpty()) return "";
        hoTen = hoTen.trim();
        String[] parts = hoTen.split("\\s+");
        return parts[parts.length - 1];
    }

    private void xuLyTimKiemDaNang() { 
        lastMa = txtMaNV.getText().trim();
        lastTen = txtHoTen.getText().trim();
        
        String selectedPhong = cboPhongBan.getSelectedItem() != null ? cboPhongBan.getSelectedItem().toString() : "";
        lastPhong = selectedPhong.equals("Tất cả Phòng Ban") ? "" : selectedPhong;
        
        lastLuong = cboLuongCoBan.getSelectedItem() != null ? cboLuongCoBan.getSelectedItem().toString() : "";
        
        lastHeSo = cboHeSo.getSelectedItem() != null ? cboHeSo.getSelectedItem().toString() : "";
        
        reloadTable("NV.MaNV ASC");
    }

    private void hienThiCuaSoDanhBa() { 
        JDialog dialog = new JDialog(this, "Danh Bạ Nhân Viên", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblLoc = new JLabel("Lọc Phòng Ban:");
        lblLoc.setBounds(20, 20, 100, 30);
        dialog.getContentPane().add(lblLoc);

        JComboBox < String > cboPhong = new JComboBox < > ();
        cboPhong.setBounds(130, 20, 200, 30);
        cboPhong.addItem("Tất cả");
        for (String p: dao.layDanhSachPhongBan()) {
            cboPhong.addItem(p);
        }
        dialog.getContentPane().add(cboPhong);

        JLabel lblTim = new JLabel("🔍 Tìm nhanh:");
        lblTim.setBounds(20, 60, 100, 30);
        dialog.getContentPane().add(lblTim);

        JTextField txtTimDanhBa = new JTextField();
        txtTimDanhBa.setBounds(130, 60, 430, 30);
        txtTimDanhBa.setToolTipText("Nhập Tên hoặc Mã NV để tìm...");
        dialog.getContentPane().add(txtTimDanhBa);

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban"};
        DefaultTableModel modelDanhBa = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tableDanhBa = new JTable(modelDanhBa);
        tableDanhBa.setFocusable(false);
        tableDanhBa.setRowSelectionAllowed(false);
        tableDanhBa.setColumnSelectionAllowed(false);
        tableDanhBa.setShowGrid(true);
        tableDanhBa.setGridColor(Color.LIGHT_GRAY);

        JScrollPane sp = new JScrollPane(tableDanhBa);
        sp.setBounds(20, 100, 540, 380);
        dialog.getContentPane().add(sp);

        Runnable napDuLieu = () -> {
            String phongDuocChon = cboPhong.getSelectedItem().toString();
            String tuKhoa = txtTimDanhBa.getText().trim();
            List < NhanVien > list = dao.timKiemDanhBa(phongDuocChon, tuKhoa);
            modelDanhBa.setRowCount(0);
            for (NhanVien nv: list) {
                modelDanhBa.addRow(new Object[] { nv.getMaNV(), nv.getHoTen(), nv.getTenPB() });
            }
        };

        cboPhong.addActionListener(e -> napDuLieu.run());
        txtTimDanhBa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { napDuLieu.run(); }
        });

        napDuLieu.run();
        dialog.setVisible(true);
    }

    private void hienThiFormDoiMatKhau() { 
        JDialog dialog = new JDialog(this, "Đổi Mật Khẩu", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblCu = new JLabel("Mật khẩu cũ:");
        lblCu.setBounds(30, 30, 100, 30);
        dialog.getContentPane().add(lblCu);
        JPasswordField txtPassCu = new JPasswordField();
        txtPassCu.setBounds(140, 30, 200, 30);
        dialog.getContentPane().add(txtPassCu);

        JLabel lblMoi = new JLabel("Mật khẩu mới:");
        lblMoi.setBounds(30, 80, 100, 30);
        dialog.getContentPane().add(lblMoi);
        JPasswordField txtPassMoi = new JPasswordField();
        txtPassMoi.setBounds(140, 80, 200, 30);
        dialog.getContentPane().add(txtPassMoi);

        JLabel lblXacNhan = new JLabel("Nhập lại MK:");
        lblXacNhan.setBounds(30, 130, 100, 30);
        dialog.getContentPane().add(lblXacNhan);
        JPasswordField txtPassXacNhan = new JPasswordField();
        txtPassXacNhan.setBounds(140, 130, 200, 30);
        dialog.getContentPane().add(txtPassXacNhan);

        JButton btnLuu = new JButton("💾 Lưu Thay Đổi");
        btnLuu.setBounds(100, 190, 180, 40);
        btnLuu.setBackground(Color.GREEN);
        dialog.getContentPane().add(btnLuu);

        btnLuu.addActionListener(e -> {
            String cu = new String(txtPassCu.getPassword());
            String moi = new String(txtPassMoi.getPassword());
            String xacNhan = new String(txtPassXacNhan.getPassword());

            if (cu.isEmpty() || moi.isEmpty() || xacNhan.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (!moi.equals(xacNhan)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu xác nhận không trùng khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dao.doiMatKhau(taiKhoanHienTai, cu, moi)) {
                JOptionPane.showMessageDialog(dialog, "✅ Đổi mật khẩu thành công!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "❌ Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.setVisible(true);
    }

    private void hienThiDanhSachTaiKhoanAdmin() { 
        JDialog dialog = new JDialog(this, "Danh Sách Tài Khoản & Mật Khẩu", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("BẢNG THEO DÕI TÀI KHOẢN NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.RED);
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setBounds(0, 10, 780, 30);
        dialog.getContentPane().add(lblTitle);

        JLabel lblTim = new JLabel("🔍 Tìm nhanh:");
        lblTim.setBounds(30, 50, 100, 30);
        lblTim.setFont(new Font("Dialog", Font.BOLD, 12));
        dialog.getContentPane().add(lblTim);

        JTextField txtTimKiem = new JTextField();
        txtTimKiem.setBounds(120, 50, 630, 30);
        txtTimKiem.setToolTipText("Nhập Mã NV, Tên hoặc Tài khoản để tìm...");
        dialog.getContentPane().add(txtTimKiem);

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban", "Tài Khoản", "Mật Khẩu"};
        DefaultTableModel modelTK = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };

        JTable tableTK = new JTable(modelTK);
        tableTK.setRowHeight(25);
        tableTK.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableTK.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableTK.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableTK.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JScrollPane sp = new JScrollPane(tableTK);
        sp.setBounds(30, 90, 720, 350);
        dialog.getContentPane().add(sp);

        List < String[] > listGoc = dao.layDanhSachTaiKhoan();

        modelTK.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 4 && row >= 0) {
                    String passMoi = modelTK.getValueAt(row, column).toString();
                    String username = modelTK.getValueAt(row, 3).toString();
                    String maNV = modelTK.getValueAt(row, 0).toString();
                    if (dao.capNhatMatKhau(username, passMoi)) {
                        for (String[] item: listGoc) {
                            if (item[0].equals(maNV)) {
                                item[4] = passMoi;
                                break;
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Lỗi cập nhật mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        Runnable boLocDuLieu = () -> {
            String tuKhoa = txtTimKiem.getText().toLowerCase().trim();
            modelTK.setRowCount(0);
            for (String[] row: listGoc) {
                if (row[0].toLowerCase().contains(tuKhoa) || row[1].toLowerCase().contains(tuKhoa) || row[3].toLowerCase().contains(tuKhoa)) {
                    modelTK.addRow(row);
                }
            }
        };

        txtTimKiem.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { boLocDuLieu.run(); }
        });

        boLocDuLieu.run();
        dialog.setVisible(true);
    }
    
    private void xuLyGiamLuong() { 
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần giảm lương!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maNV = table.getValueAt(row, 0).toString();
        String hoTen = table.getValueAt(row, 1).toString();
        String luongCuStr = table.getValueAt(row, 3).toString().replace(",", "").replace(" VNĐ", "").trim();
        double luongCu = Double.parseDouble(luongCuStr);

        String input = JOptionPane.showInputDialog(this, "Nhập % muốn giảm cho " + hoTen + ":", "10");
        if (input == null || input.trim().isEmpty()) return;

        double phanTram = 0;
        try {
            phanTram = Double.parseDouble(input);
            if (phanTram <= 0 || phanTram >= 100) {
                JOptionPane.showMessageDialog(this, "Phần trăm giảm phải lớn hơn 0 và nhỏ hơn 100!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
            return;
        }

        double luongMoi = luongCu * (1 - phanTram / 100);
        String msg = String.format("CẢNH BÁO GIẢM LƯƠNG\n\nNhân viên: %s\nLương cũ: %,.0f VNĐ\nLương mới: %,.0f VNĐ\n(Giảm: %.1f%%)\n\nXác nhận thực hiện?", hoTen, luongCu, luongMoi, phanTram);
        int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác Nhận Giảm Lương", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                NhanVienDAO dao = new NhanVienDAO();
                dao.tangLuong(maNV, -phanTram);
                JOptionPane.showMessageDialog(this, "Đã giảm lương thành công!");
                loadData("NV.MaNV ASC");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void hienThiFormBaoLoi() { 
        JDialog dialog = new JDialog(this, "Gửi Báo Cáo Lỗi", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setLayout(null);
        
        if (isCheatMode()) {
            JLabel lblGradius = new JLabel("Nothing here but GRADIUS_1986.");
            lblGradius.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 10));
            lblGradius.setForeground(Color.GRAY);
            lblGradius.setBounds(120, 50, 200, 15); // Nằm ngay dưới ô nhập tiêu đề
            dialog.getContentPane().add(lblGradius);
        }

        JLabel lblTieuDe = new JLabel("Tiêu đề lỗi:");
        lblTieuDe.setBounds(20, 20, 100, 30);
        dialog.getContentPane().add(lblTieuDe);

        JTextField txtTieuDe = new JTextField();
        txtTieuDe.setBounds(120, 20, 240, 30);
        dialog.getContentPane().add(txtTieuDe);

        JLabel lblNoiDung = new JLabel("Mô tả chi tiết:");
        lblNoiDung.setBounds(20, 60, 100, 30);
        dialog.getContentPane().add(lblNoiDung);

        JTextArea txtNoiDung = new JTextArea();
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);

        JScrollPane sp = new JScrollPane(txtNoiDung);
        sp.setBounds(20, 90, 340, 100);
        dialog.getContentPane().add(sp);

        JButton btnGui = new JButton("Gửi báo cáo");
        btnGui.setBounds(130, 210, 120, 30);

        btnGui.addActionListener(e -> {
            String tieuDe = txtTieuDe.getText().trim();
            String noiDung = txtNoiDung.getText().trim();

            if (tieuDe.isEmpty() || noiDung.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (isCheatMode() && tieuDe.equals("GRADIUS_1986")) {
                // Gọi về hàm của cửa sổ cha để ghi nhận
                QuanLyNhanVien.this.unlockSecret("GRADIUS"); 
                dialog.dispose();
                return; 
            }

            NhanVienDAO dao = new NhanVienDAO();
            dao.guiBaoLoi(tieuDe, noiDung);
            JOptionPane.showMessageDialog(dialog, "Cảm ơn! Báo cáo của bạn đã được gửi.");
            dialog.dispose();
        });

        dialog.getContentPane().add(btnGui); 
        dialog.setVisible(true);
    }
    
    private void xuLyThuongNong() { 
        String[] options = {"Toàn Công Ty", "Theo Phòng Ban", "Hủy"};
        int choice = JOptionPane.showOptionDialog(this, "Bạn muốn thưởng nóng cho đối tượng nào?", "Chọn Chế Độ Thưởng", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 2 || choice == -1) return;

        String moneyStr = JOptionPane.showInputDialog(this, "Nhập số tiền thưởng (VNĐ):", "500000");
        if (moneyStr == null || moneyStr.trim().isEmpty()) return;

        long tienThuong = 0;
        try {
            tienThuong = Long.parseLong(moneyStr.replace(",", "").replace(".", ""));
            if (tienThuong <= 0) {
                JOptionPane.showMessageDialog(this, "Tiền thưởng phải lớn hơn 0!");
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Nhập tiền sai định dạng!");
            return;
        }

        if (choice == 0) {
            if (dao.congTienThuong(tienThuong)) {
                JOptionPane.showMessageDialog(this, "✅ Đã thưởng " + String.format("%,d", tienThuong) + " VNĐ cho TOÀN CÔNG TY!");
                loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi kết nối!");
            }
        } 
        else if (choice == 1) {
            java.util.Set<String> danhSachPhong = new java.util.HashSet<>();
            for (int i = 0; i < table.getRowCount(); i++) {
                danhSachPhong.add(table.getValueAt(i, 2).toString());
            }
            if (danhSachPhong.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Danh sách trống, không tìm thấy phòng ban nào!");
                return;
            }
            String[] cacPhong = danhSachPhong.toArray(new String[0]);
            String phongDuocChon = (String) JOptionPane.showInputDialog(this, "Chọn phòng ban cần thưởng:", "Danh Sách Phòng", JOptionPane.QUESTION_MESSAGE, null, cacPhong, cacPhong[0]);

            if (phongDuocChon != null) {
                if (dao.congTienThuongTheoPhong(phongDuocChon, tienThuong)) {
                    JOptionPane.showMessageDialog(this, "✅ Đã thưởng " + String.format("%,d", tienThuong) + " VNĐ cho " + phongDuocChon.toUpperCase() + "!");
                    loadData("NV.MaNV ASC");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Lỗi: Không cập nhật được (Có thể sai tên phòng trong DB)!");
                }
            }
        }
    }

    private void xuLyChotThang() { 
        int confirm = JOptionPane.showConfirmDialog(this, "BẠN CÓ CHẮC MUỐN CHỐT SỔ THÁNG NÀY?\n\nHành động này sẽ:\n- Xóa hết số ngày đi trễ.\n- Xóa hết tiền phạt.\n- Xóa hết tiền thưởng.\n\nĐể bắt đầu tính lương cho tháng mới.", "Cảnh báo Reset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.resetThangMoi()) {
                JOptionPane.showMessageDialog(this, "✅ Đã reset dữ liệu cho tháng mới!");
                loadData("NV.MaNV ASC");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Lỗi hệ thống!");
            }
        }
    }

    private void xuLyXuatExcel() { 
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel File (*.csv)", "csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(filePath), java.nio.charset.StandardCharsets.UTF_8))) {
                bw.write("\uFEFF"); 
                for (int i = 0; i < table.getColumnCount(); i++) {
                    bw.write(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) bw.write(",");
                }
                bw.newLine();
                for (int i = 0; i < table.getRowCount(); i++) {
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        String val = table.getValueAt(i, j).toString();
                        val = val.replace(",", ""); 
                        bw.write(val);
                        if (j < table.getColumnCount() - 1) bw.write(",");
                    }
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(this, "✅ Xuất file Excel thành công!\n" + filePath);
                java.awt.Desktop.getDesktop().open(new java.io.File(filePath));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Lỗi khi xuất file: " + ex.getMessage());
            }
        }
    }
    
    private void hienThiBangLichSu() {
        JDialog dialog = new JDialog(this, "Nhật Ký Hoạt Động Hệ Thống", true);
        dialog.setSize(1200, 600); 
        dialog.setLocationRelativeTo(this);
        
        String[] cols = {"ID", "Nhân Viên Bị Tác Động", "Hành Động", "Chi Tiết Thay Đổi", "Người Thực Hiện", "Thời Gian"};
        
        DefaultTableModel modelLS = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JTable tblLS = new JTable(modelLS);
        tblLS.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        tblLS.getColumnModel().getColumn(0).setPreferredWidth(50);  
        tblLS.getColumnModel().getColumn(1).setPreferredWidth(200); 
        tblLS.getColumnModel().getColumn(2).setPreferredWidth(120); 
        tblLS.getColumnModel().getColumn(3).setPreferredWidth(450); 
        tblLS.getColumnModel().getColumn(4).setPreferredWidth(120); 
        tblLS.getColumnModel().getColumn(5).setPreferredWidth(150); 
        
        List<String[]> logs = dao.layDanhSachLichSu();
        for (String[] row : logs) {
            modelLS.addRow(row);
        }
        
        for (int i = 0; i < tblLS.getRowCount(); i++) {
            String noiDung = tblLS.getValueAt(i, 3).toString(); 
            int soDong = noiDung.split("<br>").length;
            int chieuCaoCanThiet = Math.max(40, soDong * 22 + 15);
            tblLS.setRowHeight(i, chieuCaoCanThiet);
        }
        
        dialog.add(new JScrollPane(tblLS));
        dialog.setVisible(true);
    }

    private boolean isCheatMode() {
        return "K_HASHIMOTO".equalsIgnoreCase(taiKhoanHienTai) || "SuperAdmin".equalsIgnoreCase(taiKhoanHienTai);
    }

    public void kichHoatGiaoDienHoangKim() {
        Color Light = new Color(129, 212, 250);
        Color White = new Color(236, 240, 241);
        Color grayBackground = new Color(38, 50, 56);

        this.getContentPane().setBackground(grayBackground);
        
        toMauNeonToanBo(this.getContentPane(), Light, White, grayBackground);
        
        table.setBackground(grayBackground);
        table.setForeground(White);
        table.setGridColor(new Color(55, 71, 79));
        table.getTableHeader().setBackground(new Color(55, 71, 79));
        table.getTableHeader().setForeground(Light);
        
        this.revalidate();
        this.repaint();
        
        JOptionPane.showMessageDialog(this, "NEON MODE ACTIVATED! ⚡", "Easter Egg", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void toMauNeonToanBo(java.awt.Container container, Color pink, Color blue, Color bg) {
        for (java.awt.Component c : container.getComponents()) {
            c.setBackground(bg); // Mặc định các thành phần khác theo màu nền đen
            
            if (c instanceof javax.swing.JButton) {
                c.setForeground(blue); // Chữ vẫn màu xanh Neon
                
                // --- [THÊM DÒNG NÀY] ---
                c.setBackground(Color.DARK_GRAY); // Đổi nền nút thành màu Xám Đậm cho ngầu
                // Nếu cậu thích xám sáng hơn thì đổi thành: c.setBackground(Color.GRAY);
                // -----------------------
                
                ((javax.swing.JButton) c).setBorder(javax.swing.BorderFactory.createLineBorder(pink, 2)); // Viền hồng
            } 
            else if (c instanceof javax.swing.JLabel) {
                c.setForeground(pink);
            } 
            else if (c instanceof javax.swing.JPanel) {
                c.setBackground(bg);
                ((javax.swing.JPanel) c).setBorder(javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createLineBorder(pink), 
                    ((javax.swing.JPanel) c).getBorder() != null ? "SYSTEM INFO" : "", 
                    0, 0, null, pink));
                toMauNeonToanBo((java.awt.Container) c, pink, blue, bg);
            }
            else if (c instanceof javax.swing.text.JTextComponent) { 
                c.setForeground(Color.WHITE); 
                ((javax.swing.text.JTextComponent) c).setCaretColor(pink); 
                ((javax.swing.JComponent) c).setBorder(javax.swing.BorderFactory.createLineBorder(blue)); 
            }
        }
    }

    private void hienThiCuaSoKhoiPhuc() {
        JDialog dialog = new JDialog(this, "Thùng Rác Hệ Thống", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        String[] cols = {"Mã NV", "Họ Tên", "Phòng Ban"};
        DefaultTableModel m = new DefaultTableModel(cols, 0);
        JTable t = new JTable(m);
        
        List<NhanVien> list = dao.layDanhSachNhanVienDaXoa();
        for (NhanVien nv : list) {
            m.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(), nv.getTenPB()});
        }

        JButton btnRestore = new JButton("Khôi Phục Nhân Viên");
        btnRestore.addActionListener(e -> {
            int row = t.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn nhân viên!");
                return;
            }
            String ma = t.getValueAt(row, 0).toString();
            if (dao.khoiPhucNhanVien(ma)) {
                JOptionPane.showMessageDialog(dialog, "✅ Khôi phục thành công!");
                dialog.dispose();
                loadData("NV.MaNV ASC");
            }
        });

        dialog.add(new JScrollPane(t), BorderLayout.CENTER);
        dialog.add(btnRestore, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void unlockSecret(String codeName) {
        if (!secretsFound.contains(codeName)) {
            secretsFound.add(codeName);
            // Thông báo riêng cho từng cái
            if (codeName.equals("CONTRA")) 
                JOptionPane.showMessageDialog(this, "🔫 CONTRA SURVIVOR: Tìm thấy nhân viên bất tử (30 Lives)!");
            else if (codeName.equals("SNAKE")) 
                JOptionPane.showMessageDialog(this, "🐍 SNAKE EATER: Nhiệm vụ xóa dữ liệu bí mật hoàn tất!");
            else if (codeName.equals("GRADIUS")) 
                JOptionPane.showMessageDialog(this, "🚀 GRADIUS OPTION: Hệ thống đã được nâng cấp!");

            // Kiểm tra đủ 3 cái chưa
            if (secretsFound.size() >= 3 && !isNeonUnlocked) {
                isNeonUnlocked = true;
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "🎉 CHÚC MỪNG! BẠN ĐÃ MỞ KHÓA GIAO DIỆN NEON!\n\n👉 Hãy nhấn phím 'V' để kích hoạt ngay!", "Easter Egg Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void khoiPhucGiaoDienGoc() {
        // Màu mặc định của Swing/Windows (hoặc màu cậu tự định nghĩa ban đầu)
        Color defaultBg = new Color(240, 240, 240); // Màu xám nhạt chuẩn
        Color defaultText = Color.BLACK;
        
        this.getContentPane().setBackground(defaultBg);
        
        // Gọi hàm đệ quy để reset màu toàn bộ
        resetMauToanBo(this.getContentPane(), defaultBg, defaultText);
        
        // Reset bảng
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setGridColor(new Color(200, 200, 200)); // Màu kẻ bảng xám nhạt
        table.getTableHeader().setBackground(new Color(230, 230, 230)); // Header xám
        table.getTableHeader().setForeground(Color.BLACK);
        
        this.revalidate();
        this.repaint();
        
        JOptionPane.showMessageDialog(this, "Back to Reality! 🌍", "Deactivated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetMauToanBo(java.awt.Container container, Color bg, Color text) {
        for (java.awt.Component c : container.getComponents()) {
            // Khôi phục nền
            if (c instanceof javax.swing.JPanel) {
                c.setBackground(bg); // Panel về màu nền chuẩn
                // Khôi phục viền panel (TitledBorder)
                if (((javax.swing.JPanel) c).getBorder() instanceof javax.swing.border.TitledBorder) {
                    ((javax.swing.border.TitledBorder)((javax.swing.JPanel) c).getBorder()).setTitleColor(Color.BLACK);
                    ((javax.swing.border.TitledBorder)((javax.swing.JPanel) c).getBorder()).setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY));
                }
            } 
            else if (c instanceof javax.swing.JButton) {
                c.setForeground(text);
                ((javax.swing.JButton) c).setBorder(javax.swing.UIManager.getBorder("Button.border")); // Trả về viền mặc định
            }
            else if (c instanceof javax.swing.JLabel) {
                c.setForeground(text);
            }
            else if (c instanceof javax.swing.text.JTextComponent) {
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE); // Ô nhập liệu về nền trắng
                ((javax.swing.text.JTextComponent) c).setCaretColor(Color.BLACK);
                ((javax.swing.JComponent) c).setBorder(javax.swing.UIManager.getBorder("TextField.border"));
            }
            
            // Đệ quy
            if (c instanceof java.awt.Container) {
                resetMauToanBo((java.awt.Container) c, bg, text);
            }
        }
    }
    
    private void hienThiGoiYCheat() {
        if (!isCheatMode()) return; 

        Font fontHint = new Font("Segoe UI", Font.ITALIC | Font.BOLD, 11);
        Color colorHint = new Color(120, 120, 120);

        // --- HINT 1: SNAKE ---
        // [SỬA] Bỏ chữ "JLabel" đi -> lblSnake = ...
        lblSnake = new JLabel("Try Delete Some Enemy, Huh?");
        lblSnake.setFont(fontHint);
        lblSnake.setForeground(colorHint);
        lblSnake.setBounds(630, 167, 200, 20); 
        
        // [THÊM] Nếu cậu muốn mặc định nó ẩn đi thì thêm dòng này:
        lblSnake.setVisible(false); 
        
        this.getLayeredPane().add(lblSnake, javax.swing.JLayeredPane.POPUP_LAYER);

        // --- HINT 2: CONTRA ---
        lblContraHint = new JLabel("Go Find 30 Lives, Nigga.");
        lblContraHint.setFont(fontHint);
        lblContraHint.setForeground(colorHint);
        lblContraHint.setBounds(430, 167, 200, 20); 
        lblContraHint.setVisible(false); 
        this.getLayeredPane().add(lblContraHint, javax.swing.JLayeredPane.POPUP_LAYER);

        // --- HINT 3: NEON ---
        // [SỬA] Bỏ chữ "JLabel" đi -> lblNeon = ...
        lblNeon = new JLabel("Press 'V' When You Know To Press...");
        lblNeon.setFont(fontHint);
        lblNeon.setForeground(colorHint);
        lblNeon.setBounds(410, 127, 200, 20); 
        
        // [THÊM] Nếu muốn ẩn luôn cái này thì thêm:
        lblNeon.setVisible(false); 
        
        this.getLayeredPane().add(lblNeon, javax.swing.JLayeredPane.POPUP_LAYER);
        
        this.repaint();
    }
}