package logic;
																		// Tính Lương - Đồng, Tùng
public class MayTinhLuong {
																		
    public static double tinhTongLuong(double luong1Gio, double gioChuan, double gioTangCa, double heSoTangCa, boolean coNghiThaiSan) {	// Tổng lương - Đồng
        double troCapThaiSan = 0;
        double luongChuan = gioChuan * luong1Gio;
        double luongTangCa = gioTangCa * luong1Gio * heSoTangCa;

        if (coNghiThaiSan) {
            troCapThaiSan = 10000000;
            luongChuan = 0;
            luongTangCa = 0;
        }

        return luongChuan + luongTangCa + troCapThaiSan;
    }

    public static double tinhBaoHiem(double tongLuong) {							// Bảo hiểm - Đồng
        return tongLuong * 0.105;
    }

    public static double tinhThueTNCN(double tongLuong, int soNguoiPhuThuoc) {		// Thuế - Tùng
        double tienBaoHiem = tinhBaoHiem(tongLuong);
        double giamTruBanThan = 11000000;
        double giamTruPhuThuoc = soNguoiPhuThuoc * 4400000;
        double thuNhapTinhThue = tongLuong - tienBaoHiem - giamTruBanThan - giamTruPhuThuoc;

        if (thuNhapTinhThue <= 0) return 0;

        double thuePhaiDong = 0;
        if (thuNhapTinhThue <= 5000000) {
            thuePhaiDong = thuNhapTinhThue * 0.05;
        } else if (thuNhapTinhThue <= 10000000) {
            thuePhaiDong = (thuNhapTinhThue * 0.1) - 250000;
        } else if (thuNhapTinhThue <= 18000000) {
            thuePhaiDong = (thuNhapTinhThue * 0.15) - 750000;
        } else if (thuNhapTinhThue <= 32000000) {
            thuePhaiDong = (thuNhapTinhThue * 0.2) - 1650000;
        } else if (thuNhapTinhThue <= 52000000) {
            thuePhaiDong = (thuNhapTinhThue * 0.25) - 3250000;
        } else if (thuNhapTinhThue <= 80000000) {
            thuePhaiDong = (thuNhapTinhThue * 0.3) - 5850000;
        } else {
            thuePhaiDong = (thuNhapTinhThue * 0.35) - 9850000;
        }

        return thuePhaiDong;
    }
}