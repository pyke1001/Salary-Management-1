# 💰 KONAMI PAYROLL - SALARY MANAGEMENT SYSTEM
> **Phiên bản:** v1.3.0  
> **Mục tiêu:** Quản lý Lương, Thưởng & Chấm công 🚀

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Eclipse](https://img.shields.io/badge/Eclipse-IDE-2C2255?style=for-the-badge&logo=eclipse&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-GUI-blue?style=for-the-badge)
![SQL Server](https://img.shields.io/badge/SQL_Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)

---

## 📖 Giới thiệu (Introduction)

**Konami Payroll** là phần mềm tập trung giải quyết bài toán cốt lõi của doanh nghiệp: **Tính toán tiền lương chính xác và nhanh chóng**.

Thay vì các tính năng nhân sự phức tạp, hệ thống tập trung tối ưu hóa quy trình tính lương (Payroll Processing), quản lý các khoản thu nhập biến đổi (Tăng ca, Thưởng nóng) và khấu trừ (Phạt, Bảo hiểm) một cách minh bạch nhất.

---

## 🌟 Tính năng Chính (Core Features)

### 1. 🧮 Tính Lương Tự Động (Automated Payroll)
* **Công thức chuẩn:** Tự động tính `Thực Lĩnh = (Lương cứng x Hệ số) + Phụ cấp + Tăng ca + Thưởng - Phạt - Bảo hiểm`.
* **Xử lý Tăng ca (OT):** Tính toán chi tiết giờ làm thêm (Ngày thường x1.5, Cuối tuần x2.0, Lễ Tết x3.0).
* **Live Preview:** Xem trước mức thực lĩnh ngay trên bảng danh sách mà không cần chốt sổ.

### 2. 🎁 Quản lý Thu nhập & Khấu trừ (Earnings & Deductions)
* **Thưởng linh hoạt:** Hỗ trợ thưởng nóng cho Cá nhân, theo Phòng ban hoặc Toàn công ty (kèm lý do chi tiết).
* **Kỷ luật & Phạt:** Ghi nhận ngày đi trễ và tự động trừ lương theo quy định.
* **Phúc lợi:** Tự động hiển thị mức độ đãi ngộ (Bảo hiểm, Ngày phép) dựa trên thâm niên làm việc.

### 3. 👥 Quản lý Hồ sơ Cơ bản (Basic Employee Records)
* Lưu trữ thông tin nhân viên cần thiết cho việc trả lương (Mã NV, Hệ số lương, Phòng ban, Ngày vào làm).
* Tra cứu danh bạ và lịch sử thay đổi lương/chức vụ.

### 4. 📊 Báo cáo & Tiện ích (Reports & Utilities)
* **Phiếu lương (Payslip):** Xuất phiếu lương chi tiết và gửi qua Hộp thư nội bộ.
* **Lưu trữ Lịch sử:** Chức năng "Chốt Tháng" giúp lưu lại bảng lương đã phát để đối soát về sau.
* **Thống kê:** Biểu đồ phân bổ quỹ lương giữa các phòng ban.
* **Kết nối LAN:** Hỗ trợ mô hình Client-Server để Admin và Nhân viên cùng truy cập.

---

## 📸 Hình ảnh Demo (Screenshots)

### 🖥️ Bảng Tính Lương Trung tâm
![Dashboard](screenshots/dashboard.png)
*Giao diện tập trung vào các con số tài chính và các nút nghiệp vụ lương.*

### 💵 Chi tiết Phiếu Lương Cá Nhân
![Payslip](screenshots/profile.png)
*Minh bạch hóa từng khoản thu nhập và khấu trừ của nhân viên.*

### 🏆 Danh sách Khen Thưởng & Kỷ luật
![Rewards](screenshots/rewards.png)
*Quản lý danh sách thưởng/phạt rõ ràng, hỗ trợ chỉnh sửa lý do nhanh.*

---

## 🛠️ Cài đặt & Sử dụng

### Yêu cầu
* **Java:** JDK 8 trở lên.
* **Database:** SQL Server (Khuyến nghị) hoặc SQLite.

### Hướng dẫn nhanh
1.  Chạy script SQL để tạo Database.
2.  Cấu hình file `database.config`.
3.  Chạy file `.jar` hoặc chạy từ IDE.

### 🔐 Tài khoản Demo
| Role | User | Pass | Quyền hạn |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `123456` | Toàn quyền tính lương, sửa đổi hệ số, chốt sổ |
| **User** | `NV001` | `123` | Xem phiếu lương cá nhân, xem lịch sử |

---

## 🤝 Đóng góp
Dự án được xây dựng với mục đích học tập môn **Lập trình Java Nâng cao**.
Mọi ý kiến đóng góp xin vui lòng tạo Issue hoặc Pull Request.

---
*Developed by Pyke1001.*
