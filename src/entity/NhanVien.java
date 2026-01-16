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
    private String gioiTinh;
    
    private double gioTangCa;
    private double heSoTangCa;
    private long thucLinh;
    private String lyDoThuongPhat = "";

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

    public String getLyDoThuongPhat() { return lyDoThuongPhat; }
    public void setLyDoThuongPhat(String lyDoThuongPhat) { this.lyDoThuongPhat = lyDoThuongPhat; }

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
    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
    
    public double getGioTangCa() { return gioTangCa; }
    public void setGioTangCa(double gioTangCa) { this.gioTangCa = gioTangCa; }
    public double getHeSoTangCa() { return heSoTangCa; }
    public void setHeSoTangCa(double heSoTangCa) { this.heSoTangCa = heSoTangCa; }
    
    public long getThucLinh() { return thucLinh; }
    public void setThucLinh(long thucLinh) { this.thucLinh = thucLinh; }
    
    public long getGross() { 
        long luongCung = (long) (luongCoBan * heSoLuong);
        if (thucLinh > 0) return thucLinh;
        return luongCung + phuCap + tienThuong - tienPhat;
    }
}