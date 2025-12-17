package logic;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class XuLyTangLuong {

    // CÁCH 1: Tăng theo thâm niên (Dành cho người làm lâu năm)
    // Logic: Làm > 1 năm tăng 5%, > 3 năm tăng 10%, > 5 năm tăng 15%
    public static double tinhLuongTheoThamNien(double luongCu, Date ngayVaoLam) {
        // Chuyển Date sang LocalDate để tính toán cho dễ
        LocalDate start = ngayVaoLam.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        
        // Tính khoảng cách thời gian (Năm)
        int soNamLamViec = Period.between(start, now).getYears();
        
        double phanTramTang = 0;
        
        if (soNamLamViec >= 5) {
            phanTramTang = 0.15; // 15%
        } else if (soNamLamViec >= 3) {
            phanTramTang = 0.10; // 10%
        } else if (soNamLamViec >= 1) {
            phanTramTang = 0.05; // 5%
        }
        
        // Nếu dưới 1 năm thì không tăng (phanTramTang = 0)
        return luongCu * (1 + phanTramTang);
    }

    // CÁCH 2: Tăng theo KPI (Hiệu suất làm việc)
    // Logic: A (Xuất sắc) = 20%, B (Giỏi) = 10%, C (Khá) = 5%
    public static double tinhLuongTheoKPI(double luongCu, String xepLoaiKPI) {
        double phanTramTang = 0;
        
        switch (xepLoaiKPI) {
            case "A": // Xuất sắc
                phanTramTang = 0.20; 
                break;
            case "B": // Giỏi
                phanTramTang = 0.10; 
                break;
            case "C": // Khá
                phanTramTang = 0.05; 
                break;
            default: // D hoặc F thì nhịn
                phanTramTang = 0;
        }
        
        return luongCu * (1 + phanTramTang);
    }
}