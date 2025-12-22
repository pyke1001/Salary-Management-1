package ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Secret {
    
    private QuanLyNhanVien ui;

    public Secret(QuanLyNhanVien ui) {
        this.ui = ui;
    }
    
    public boolean isCheatMode() {
        return "K_HASHIMOTO".equalsIgnoreCase(ui.taiKhoanHienTai) || "SuperAdmin".equalsIgnoreCase(ui.taiKhoanHienTai);
    }
    
    public void hienThiGoiYCheat() {
        if (!isCheatMode()) return; 

        Font fontHint = new Font("Segoe UI", Font.ITALIC | Font.BOLD, 11);
        Color colorHint = new Color(120, 120, 120);

        // Hint Snake
        ui.lblSnake = new JLabel("Solid Snake..."); // Sửa: ui.lblSnake
        ui.lblSnake.setFont(fontHint);
        ui.lblSnake.setForeground(colorHint);
        ui.lblSnake.setBounds(580, 67, 200, 20); 
        ui.lblSnake.setVisible(false);      
        ui.getLayeredPane().add(ui.lblSnake, javax.swing.JLayeredPane.POPUP_LAYER); // Sửa: ui.getLayeredPane

        // Hint Contra
        ui.lblContraHint = new JLabel("Where are the 30 lives?"); // Sửa: ui.lblContraHint
        ui.lblContraHint.setFont(fontHint);
        ui.lblContraHint.setForeground(colorHint);
        ui.lblContraHint.setBounds(430, 167, 200, 20); 
        ui.lblContraHint.setVisible(false); 
        ui.getLayeredPane().add(ui.lblContraHint, javax.swing.JLayeredPane.POPUP_LAYER); // Sửa: ui.getLayeredPane
        
        // Hint Giao Diện Ẩn
        ui.lblNeon = new JLabel("Press 'V' once it is unlocked..."); // Sửa: ui.lblNeon
        ui.lblNeon.setFont(fontHint);
        ui.lblNeon.setForeground(colorHint);
        ui.lblNeon.setBounds(410, 127, 200, 20); 
        ui.lblNeon.setVisible(false);        
        ui.getLayeredPane().add(ui.lblNeon, javax.swing.JLayeredPane.POPUP_LAYER); // Sửa: ui.getLayeredPane
        
        ui.repaint(); // Sửa: ui.repaint()
    }
    
    public void unlockSecret(String codeName) {
        if (!ui.secretsFound.contains(codeName)) { // Sửa: ui.secretsFound
            ui.secretsFound.add(codeName);
            
            // Sửa: JOptionPane phải hiện lên trên 'ui', không phải 'this'
            if (codeName.equals("CONTRA")) 
                JOptionPane.showMessageDialog(ui, "🔫 CONTRA SURVIVOR: Tìm thấy nhân viên thứ 30 (30 Lives)!");
            else if (codeName.equals("SNAKE")) 
                JOptionPane.showMessageDialog(ui, "🐍 SNAKE EATER: Nhiệm vụ xóa dữ liệu bí mật hoàn tất!");
            else if (codeName.equals("GRADIUS")) 
                JOptionPane.showMessageDialog(ui, "🚀 GRADIUS OPTION: Hệ thống đã được nâng cấp!");

            if (ui.secretsFound.size() >= 3 && !ui.isNeonUnlocked) { // Sửa: ui.isNeonUnlocked
                ui.isNeonUnlocked = true;
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(ui, "🎉 CHÚC MỪNG! BẠN ĐÃ MỞ KHÓA GIAO DIỆN ẨN!\n\n👉 Hãy nhấn phím 'V' để kích hoạt ngay!", "Easter Egg Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    public void kichHoatGiaoDienAn() {
        Color Light = new Color(129, 212, 250);
        Color White = new Color(236, 240, 241);
        Color grayBackground = new Color(38, 50, 56);

        ui.getContentPane().setBackground(grayBackground); // Sửa: ui.getContentPane
        
        toMauNeonToanBo(ui.getContentPane(), Light, White, grayBackground);
        
        ui.table.setBackground(grayBackground); // Sửa: ui.table
        ui.table.setForeground(White);
        ui.table.setGridColor(new Color(55, 71, 79));
        ui.table.getTableHeader().setBackground(new Color(55, 71, 79));
        ui.table.getTableHeader().setForeground(Light);
        
        ui.revalidate(); // Sửa: ui.revalidate
        ui.repaint();    // Sửa: ui.repaint
    }
    
    public void toMauNeonToanBo(java.awt.Container container, Color pink, Color blue, Color bg) {
        for (java.awt.Component c : container.getComponents()) {
            c.setBackground(bg);
            
            if (c instanceof javax.swing.JButton) {
                c.setForeground(blue);
                c.setBackground(Color.DARK_GRAY); 
                ((javax.swing.JButton) c).setBorder(javax.swing.BorderFactory.createLineBorder(pink, 2)); 
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
    
    public void khoiPhucGiaoDienGoc() {
        Color defaultBg = new Color(240, 240, 240);
        Color defaultText = Color.BLACK;
        
        ui.getContentPane().setBackground(defaultBg); // Sửa: ui.getContentPane

        resetMauToanBo(ui.getContentPane(), defaultBg, defaultText);
        
        ui.table.setBackground(Color.WHITE); // Sửa: ui.table
        ui.table.setForeground(Color.BLACK);
        ui.table.setGridColor(new Color(200, 200, 200)); 
        ui.table.getTableHeader().setBackground(new Color(230, 230, 230));
        ui.table.getTableHeader().setForeground(Color.BLACK);
        
        ui.revalidate();
        ui.repaint();
        
        JOptionPane.showMessageDialog(ui, "Back to Reality! 🌍", "Deactivated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void resetMauToanBo(java.awt.Container container, Color bg, Color text) {
        for (java.awt.Component c : container.getComponents()) {

            if (c instanceof javax.swing.JPanel) {
                c.setBackground(bg); 
                if (((javax.swing.JPanel) c).getBorder() instanceof javax.swing.border.TitledBorder) {
                    ((javax.swing.border.TitledBorder)((javax.swing.JPanel) c).getBorder()).setTitleColor(Color.BLACK);
                    ((javax.swing.border.TitledBorder)((javax.swing.JPanel) c).getBorder()).setBorder(javax.swing.BorderFactory.createLineBorder(Color.GRAY));
                }
            } 
            else if (c instanceof javax.swing.JButton) {
                c.setForeground(text);
                ((javax.swing.JButton) c).setBorder(javax.swing.UIManager.getBorder("Button.border"));
            }
            else if (c instanceof javax.swing.JLabel) {
                c.setForeground(text);
            }
            else if (c instanceof javax.swing.text.JTextComponent) {
                c.setForeground(Color.BLACK);
                c.setBackground(Color.WHITE); 
                ((javax.swing.text.JTextComponent) c).setCaretColor(Color.BLACK);
                ((javax.swing.JComponent) c).setBorder(javax.swing.UIManager.getBorder("TextField.border"));
            }
           
            if (c instanceof java.awt.Container) {
                resetMauToanBo((java.awt.Container) c, bg, text);
            }
        }
    }
}