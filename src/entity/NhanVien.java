package entity;

import java.util.Date;

public class NhanVien {
    private String maNV;
    private String hoTen;
    private String maPB;
    private long luongCoBan;
    private float heSoLuong;
    private long tienThuong;
    private int soNgayDiTre;
    private long tienPhat;
    private String tenPB;
    private Date ngayVaoLam;
    private long phuCap;

    public NhanVien() {
    }

    public NhanVien(String maNV, String hoTen, String maPB, long luongCoBan, float heSoLuong) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.maPB = maPB;
        this.luongCoBan = luongCoBan;
        this.heSoLuong = heSoLuong;
        this.ngayVaoLam = new Date();
    }

    public NhanVien(String maNV, String hoTen, String maPB, long luongCoBan, float heSoLuong, long tienThuong, int soNgayDiTre, long tienPhat, long phuCap) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.maPB = maPB;
        this.luongCoBan = luongCoBan;
        this.heSoLuong = heSoLuong;
        this.tienThuong = tienThuong;
        this.soNgayDiTre = soNgayDiTre;
        this.tienPhat = tienPhat;
        this.phuCap = phuCap;
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getMaPB() { return maPB; }
    public void setMaPB(String maPB) { this.maPB = maPB; }

    public long getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(long luongCoBan) { this.luongCoBan = luongCoBan; }

    public float getHeSoLuong() { return heSoLuong; }
    public void setHeSoLuong(float heSoLuong) { this.heSoLuong = heSoLuong; }

    public long getTienThuong() { return tienThuong; }
    public void setTienThuong(long tienThuong) { this.tienThuong = tienThuong; }

    public int getSoNgayDiTre() { return soNgayDiTre; }
    public void setSoNgayDiTre(int soNgayDiTre) { this.soNgayDiTre = soNgayDiTre; }

    public long getTienPhat() { return tienPhat; }
    public void setTienPhat(long tienPhat) { this.tienPhat = tienPhat; }
    
    public long getPhuCap() { return phuCap; }
    public void setPhuCap(long phuCap) { this.phuCap = phuCap; }

    public String getTenPB() { return tenPB; }
    public void setTenPB(String tenPB) { this.tenPB = tenPB; }

    public Date getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(Date ngayVaoLam) { this.ngayVaoLam = ngayVaoLam; }
    
    public long getThucLinh() { 
    	long luongCung = (long) (luongCoBan * heSoLuong);
        // Công thức: Lương cứng + Phụ cấp + Thưởng - Phạt
        return luongCung + phuCap + tienThuong - tienPhat;
    }
}