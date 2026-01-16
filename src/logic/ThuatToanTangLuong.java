package logic;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class ThuatToanTangLuong {

    public static double tinhPhuCapThamNien(Date ngayVaoLam) {
        if (ngayVaoLam == null) return 0;
        
        LocalDate start;
        if (ngayVaoLam instanceof java.sql.Date) {
            start = ((java.sql.Date) ngayVaoLam).toLocalDate();
        } else {
            start = ngayVaoLam.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        int soNam = Period.between(start, LocalDate.now()).getYears();

        if (soNam >= 5) {
            return 0.05 + ((soNam - 5) * 0.01); // 5 năm = 5%, mỗi năm sau +1%
        }
        return 0; // Dưới 5 năm chưa có
    }

    public static float deXuatHeSoMoi(java.util.Date ngayVaoLam, float heSoHienTai) {
    float heSoKhoiDiem = 2.34f; 
    
    if (ngayVaoLam == null) return heSoKhoiDiem;

    LocalDate start;
    
    if (ngayVaoLam instanceof java.sql.Date) {
        start = ((java.sql.Date) ngayVaoLam).toLocalDate();
    } else {
        start = ngayVaoLam.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    int soNam = Period.between(start, LocalDate.now()).getYears();
    
    int soLanTang = soNam / 3; 
    if (soLanTang > 9) soLanTang = 9; 

    float heSoGocUocLuong = heSoHienTai - (soLanTang * 0.33f);

    if (heSoGocUocLuong < 1.95f) {
        heSoKhoiDiem = 1.86f; 
    } else if (heSoGocUocLuong < 2.2f) {
        heSoKhoiDiem = 2.10f; 
    } else {
        heSoKhoiDiem = 2.34f; 
    }

    float heSoMoi = heSoKhoiDiem + (soLanTang * 0.33f);
    
    return (float) (Math.round(heSoMoi * 100.0) / 100.0);
}

    public static double tinhLuongTheoKPI(double luongCu, String xepLoaiKPI) {
        double phanTramTang = 0;
        switch (xepLoaiKPI.toUpperCase()) { 
            case "A": phanTramTang = 0.20; break;
            case "B": phanTramTang = 0.10; break;
            case "C": phanTramTang = 0.05; break;
            default: phanTramTang = 0;
        }
        return luongCu * (1 + phanTramTang);
    }
}