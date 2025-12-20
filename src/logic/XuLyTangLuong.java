package logic;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class XuLyTangLuong {

    public static double tinhLuongTheoThamNien(double luongCu, Date ngayVaoLam) {
        if (ngayVaoLam == null) return luongCu;

        LocalDate start = ngayVaoLam.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        int soNamLamViec = Period.between(start, now).getYears();
        
        double phanTramTang = 0;
        
        if (soNamLamViec >= 5) {
            phanTramTang = 0.15;
        } else if (soNamLamViec >= 3) {
            phanTramTang = 0.10;
        } else if (soNamLamViec >= 1) {
            phanTramTang = 0.05;
        }
        
        return luongCu * (1 + phanTramTang);
    }

    public static double tinhLuongTheoKPI(double luongCu, String xepLoaiKPI) {
        double phanTramTang = 0;
        
        switch (xepLoaiKPI.toUpperCase()) { // toUpperCase để chấp nhận cả 'a' và 'A'
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