package logic;

public class MayTinhLuong {
    

    public static double tinhTongLuong(double luong1Gio, double gioChuan, double gioTangCa, double heSoTangCa, boolean coNghiThaiSan) {
        double troCapThaiSan = 0;
        double luongChuan = gioChuan * luong1Gio;
        double luongTangCa = gioTangCa * luong1Gio * heSoTangCa;

        if (coNghiThaiSan) {
            troCapThaiSan = 10000000; // Mức cố định thai sản
            luongChuan = 0;
            luongTangCa = 0;
        }
        return luongChuan + luongTangCa + troCapThaiSan;
    }

    public static double tinhBaoHiem(double tongLuong) {
        return tongLuong * 0.105; // 10.5% Bảo hiểm
    }

    public static double tinhThueTNCN(double tongLuong, int soNguoiPhuThuoc) {
        double tienBaoHiem = tinhBaoHiem(tongLuong);
        double giamTruBanThan = 11000000;
        double giamTruPhuThuoc = soNguoiPhuThuoc * 4400000;
        double thuNhapTinhThue = tongLuong - tienBaoHiem - giamTruBanThan - giamTruPhuThuoc;

        if (thuNhapTinhThue <= 0) return 0;

        // Bậc thuế lũy tiến
        if (thuNhapTinhThue <= 5000000) return thuNhapTinhThue * 0.05;
        if (thuNhapTinhThue <= 10000000) return (thuNhapTinhThue * 0.1) - 250000;
        if (thuNhapTinhThue <= 18000000) return (thuNhapTinhThue * 0.15) - 750000;
        if (thuNhapTinhThue <= 32000000) return (thuNhapTinhThue * 0.2) - 1650000;
        if (thuNhapTinhThue <= 52000000) return (thuNhapTinhThue * 0.25) - 3250000;
        if (thuNhapTinhThue <= 80000000) return (thuNhapTinhThue * 0.3) - 5850000;
        
        return (thuNhapTinhThue * 0.35) - 9850000;
    }	

    public static long tinhThucLinhFinal(long luongCoBan, float heSoLuong, double gioTangCa, double heSoTangCa, 
                                         long tienThuong, long tienPhat, int soNguoiPhuThuoc, 
                                         java.util.Date ngayVaoLam) {
        
        // Lương Cứng
        long luongCung = (long) (luongCoBan * heSoLuong);
        
        // Tính Tiền Thâm Niên (Gọi sang file ThuatToanTangLuong)
        double phanTramThamNien = logic.ThuatToanTangLuong.tinhPhuCapThamNien(ngayVaoLam);
        long tienThamNien = (long) (luongCung * phanTramThamNien);
        
        // Tính lương 1 giờ (để tính OT)
        double luong1Gio = (double)luongCung / 26 / 8;
        
        // Tổng Gross = Lương cứng + Thâm niên + OT
        double tongLuongGross = luongCung + tienThamNien + (gioTangCa * luong1Gio * heSoTangCa);
        
        // Các khoản trừ (Gọi hàm bên trên)
        double baoHiem = tinhBaoHiem(tongLuongGross);
        double thue = tinhThueTNCN(tongLuongGross, soNguoiPhuThuoc);
        
        // Phụ cấp cố định (Ăn + Xăng)
        long phuCapCung = 730000 + 300000; 

        // Chốt Thực Lĩnh
        double thucLinh = (tongLuongGross - baoHiem - thue) + phuCapCung + tienThuong - tienPhat;
        
        return (long) Math.round(thucLinh);
    }
}