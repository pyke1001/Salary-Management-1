//Tính tiền lương - Đồng
package quanlyluong;

public class MayTinhTienLuong {

    public static double tinhTongLuong(double luong1Gio, double gioChuan, 
                                       double gioTangCa, double heSoTangCa, 
                                       boolean coNghiThaiSan) {
        double troCapThaiSan = 0;
        double tongLanh = 0;
        
        // 1. Tính lương cơ bản
        double luongChuan = gioChuan * luong1Gio;
        
        // 2. Tính lương tăng ca
        double luongTangCa = gioTangCa * luong1Gio * heSoTangCa; 

        if (coNghiThaiSan) {
            troCapThaiSan = 10000000; // Nếu nghỉ thai sản thì trợ cấp = 10 triệu VNĐ, đồng thời nghỉ làm (Đi đẻ thì ai mà đi làm :V).
            luongChuan = 0;             // Nghỉ làm nên không có lương chuẩn
            luongTangCa = 0;            // Không có tăng ca
        }

        tongLanh = luongChuan + luongTangCa + troCapThaiSan;
        
        return tongLanh;
    }
}