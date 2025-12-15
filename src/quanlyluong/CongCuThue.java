//Tính thuế - Tùng
package quanlyluong;

public class CongCuThue {

    // Hàm tính tiền bảo hiểm (10.5% lương)
    public static double tinhBaoHiem(double tongLuong) {
        return tongLuong * 0.105; 
    }

    // Hàm tính thuế TNCN (Theo 7 bậc lũy tiến chuẩn VN)
    public static double tinhThueTNCN(double tongLuong, int soNguoiPhuThuoc) {
        // 1. Trừ tiền bảo hiểm trước
        double tienBaoHiem = tinhBaoHiem(tongLuong);
        
        // 2. Tính các khoản giảm trừ
        double giamTruBanThan = 11000000;
        double giamTruPhuThuoc = soNguoiPhuThuoc * 4400000;
        
        // 3. Tính thu nhập chịu thuế
        double thuNhapTinhThue = tongLuong - tienBaoHiem - giamTruBanThan - giamTruPhuThuoc;
        
        if (thuNhapTinhThue <= 0) return 0; // Không phải đóng thuế

        // 4. Tính thuế theo bậc thang
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