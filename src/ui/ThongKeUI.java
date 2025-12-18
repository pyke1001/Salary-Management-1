package ui;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
														// Thống kê UI - Hướng
public class ThongKeUI extends JFrame {
	private static final long serialVersionUID = 2L;
    private DefaultTableModel mainModel;

    public ThongKeUI(DefaultTableModel model) {
        this.mainModel = model;
        initUI();
        tinhToanDuLieu();
    }

    private void initUI() {
        setTitle("BÁO CÁO THỐNG KÊ TỔNG HỢP - KONAMI ENTERPRISE");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("TRUNG TÂM PHÂN TÍCH DỮ LIỆU NHÂN SỰ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("   Tổng Quan   ", new ImageIcon(), taoPanelTongQuan());
        tabbedPane.addTab("   Tài Chính Phòng Ban   ", new ImageIcon(), taoPanelPhongBan());
        tabbedPane.addTab("   Top Nhân Viên   ", new ImageIcon(), taoPanelXepHang());

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng Báo Cáo");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(_ -> dispose());
        JPanel pnlBot = new JPanel();
        pnlBot.add(btnClose);
        add(pnlBot, BorderLayout.SOUTH);
    }

    private JPanel taoPanelTongQuan() {
        JPanel pnl = new JPanel(new GridLayout(2, 2, 20, 20));
        pnl.setBorder(new EmptyBorder(20, 50, 20, 50));
        pnl.setBackground(Color.WHITE);

        int tongNV = mainModel.getRowCount();
        long tongLuong = 0;
        long luongCaoNhat = 0;
        
        for (int i = 0; i < tongNV; i++) {
            String luongStr = mainModel.getValueAt(i, 9).toString().replace(",", "").replace(".", "");
            long luong = Long.parseLong(luongStr);
            tongLuong += luong;
            if (luong > luongCaoNhat) luongCaoNhat = luong;
        }
        long luongTB = tongNV > 0 ? tongLuong / tongNV : 0;

        pnl.add(taoOThongKe("TỔNG NHÂN SỰ", tongNV + " nhân viên", new Color(255, 193, 7)));
        pnl.add(taoOThongKe("TỔNG QUỸ LƯƠNG", String.format("%,d", tongLuong) + " VNĐ", new Color(76, 175, 80)));
        pnl.add(taoOThongKe("LƯƠNG CAO NHẤT", String.format("%,d", luongCaoNhat) + " VNĐ", new Color(33, 150, 243)));
        pnl.add(taoOThongKe("THU NHẬP TRUNG BÌNH", String.format("%,d", luongTB) + " VNĐ", new Color(156, 39, 176)));

        return pnl;
    }

    private JPanel taoPanelPhongBan() {
        JPanel pnl = new JPanel(new BorderLayout());
        
        String[] cols = {"Tên Phòng Ban", "Số Nhân Viên", "Tổng Lương (VNĐ)", "Lương Trung Bình (VNĐ)", "Tỷ Trọng Quỹ Lương"};
        DefaultTableModel modelPB = new DefaultTableModel(cols, 0);
        JTable tblPB = new JTable(modelPB);
        styleTable(tblPB);

        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Long> sumMap = new HashMap<>();
        long totalCompanySalary = 0;

        for (int i = 0; i < mainModel.getRowCount(); i++) {
            String phong = mainModel.getValueAt(i, 2).toString();
            String luongStr = mainModel.getValueAt(i, 9).toString().replace(",", "");
            long luong = Long.parseLong(luongStr);

            countMap.put(phong, countMap.getOrDefault(phong, 0) + 1);
            sumMap.put(phong, sumMap.getOrDefault(phong, 0L) + luong);
            totalCompanySalary += luong;
        }

        for (String phong : countMap.keySet()) {
            int soNV = countMap.get(phong);
            long tongL = sumMap.get(phong);
            long tbL = tongL / soNV;
            double tyTrong = (double) tongL / totalCompanySalary * 100;

            modelPB.addRow(new Object[]{
                phong, 
                soNV + " người", 
                String.format("%,d", tongL), 
                String.format("%,d", tbL),
                String.format("%.1f", tyTrong) + "%"
            });
        }

        pnl.add(new JScrollPane(tblPB), BorderLayout.CENTER);
        return pnl;
    }

    private JPanel taoPanelXepHang() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 15, 0));
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlLeft = new JPanel(new BorderLayout());
        JLabel lblTopLuong = new JLabel("🏆 TOP 5 LƯƠNG CAO NHẤT", SwingConstants.CENTER);
        lblTopLuong.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTopLuong.setForeground(new Color(255, 152, 0));
        
        String[] cols = {"Họ Tên", "Phòng", "Thực Lĩnh"};
        DefaultTableModel modelTopLuong = new DefaultTableModel(cols, 0);
        JTable tblTopLuong = new JTable(modelTopLuong);
        styleTable(tblTopLuong);
        
        pnlLeft.add(lblTopLuong, BorderLayout.NORTH);
        pnlLeft.add(new JScrollPane(tblTopLuong), BorderLayout.CENTER);

        JPanel pnlRight = new JPanel(new BorderLayout());
        JLabel lblTopTre = new JLabel("⚠️ TOP NHÂN VIÊN ĐI TRỄ", SwingConstants.CENTER);
        lblTopTre.setFont(new Font("Dialog", Font.BOLD, 16));
        lblTopTre.setForeground(new Color(244, 67, 54));

        String[] colsTre = {"Họ Tên", "Phòng", "Số Ngày Trễ"};
        DefaultTableModel modelTopTre = new DefaultTableModel(colsTre, 0);
        JTable tblTopTre = new JTable(modelTopTre);
        styleTable(tblTopTre);

        pnlRight.add(lblTopTre, BorderLayout.NORTH);
        pnlRight.add(new JScrollPane(tblTopTre), BorderLayout.CENTER);

        List<Object[]> listNV = new ArrayList<>();
        for (int i = 0; i < mainModel.getRowCount(); i++) {
            listNV.add(new Object[]{
                mainModel.getValueAt(i, 1),
                mainModel.getValueAt(i, 2),
                mainModel.getValueAt(i, 9),
                mainModel.getValueAt(i, 7)
            });
        }

        listNV.sort((o1, o2) -> {
            long l1 = Long.parseLong(o1[2].toString().replace(",", ""));
            long l2 = Long.parseLong(o2[2].toString().replace(",", ""));
            return Long.compare(l2, l1);
        });
        
        for (int i = 0; i < Math.min(5, listNV.size()); i++) {
            modelTopLuong.addRow(new Object[]{listNV.get(i)[0], listNV.get(i)[1], listNV.get(i)[2]});
        }

        listNV.sort((o1, o2) -> {
            int t1 = Integer.parseInt(o1[3].toString().replace(" ngày", "").trim());
            int t2 = Integer.parseInt(o2[3].toString().replace(" ngày", "").trim());
            return Integer.compare(t2, t1);
        });

        for (int i = 0; i < Math.min(5, listNV.size()); i++) {
            String soNgay = listNV.get(i)[3].toString();
            if (!soNgay.startsWith("0")) {
                modelTopTre.addRow(new Object[]{listNV.get(i)[0], listNV.get(i)[1], soNgay});
            }
        }

        pnl.add(pnlLeft);
        pnl.add(pnlRight);
        return pnl;
    }

    private JPanel taoOThongKe(String title, String value, Color color) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(color);
        pnl.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
        
        JLabel lblVal = new JLabel(value, SwingConstants.CENTER);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblVal.setForeground(Color.WHITE);
        
        JLabel lblTit = new JLabel(title, SwingConstants.CENTER);
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTit.setForeground(new Color(255, 255, 255, 200));
        lblTit.setBorder(new EmptyBorder(10, 0, 10, 0));

        pnl.add(lblVal, BorderLayout.CENTER);
        pnl.add(lblTit, BorderLayout.SOUTH);
        return pnl;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
    }

    private void tinhToanDuLieu() {
    }
}