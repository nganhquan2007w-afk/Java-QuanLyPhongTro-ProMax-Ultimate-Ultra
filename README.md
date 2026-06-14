# 🏢 Quản Lý Phòng Trọ Pro Max Ultimate Ultra

<p align="center">
  <img src="https://img.shields.io/badge/JAVA-11-orange?style=for-the-badge&logo=java&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/SWING-GUI-007396?style=for-the-badge&logo=java&logoColor=white" alt="Swing"/>
  <img src="https://img.shields.io/badge/MYSQL-DATABASE-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/MAVEN-BUILD-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
</p>

<p align="center">
  <i>Hệ thống phần mềm quản lý phòng trọ chuyên nghiệp dựa trên kiến trúc <b>Client-Server (Socket)</b>, kết hợp giao diện Java Swing (<b>FlatLaf</b>) hiện đại. Hệ thống mang đến giải pháp quản lý tập trung, tự động hóa mạnh mẽ và đồng bộ dữ liệu theo thời gian thực.</i>
</p>

---

## 🌟 Tính Năng Nổi Bật

- 🔌 **Kiến trúc Client-Server (Socket)**: Hỗ trợ kết nối đồng thời từ nhiều máy trạm (Client) đến một máy chủ trung tâm (Server). Dữ liệu được trao đổi qua mạng bằng định dạng JSON siêu nhẹ và bảo mật.
- 🎨 **Giao diện hiện đại & Đẹp mắt**: Tích hợp thư viện FlatLaf mang lại trải nghiệm người dùng (UI) tuyệt vời, trực quan và hỗ trợ đầy đủ Dark/Light themes.
- ⚙️ **Tự động hóa Cơ sở dữ liệu**: Lần chạy Server đầu tiên sẽ tự động kết nối MySQL, tạo Database, thiết lập cấu trúc bảng và nạp dữ liệu mẫu ban đầu.
- 📊 **Quản lý toàn diện**: 
  - Quản lý danh sách phòng trọ, sơ đồ phòng, trạng thái trống/đang thuê.
  - Quản lý thông tin khách thuê, hợp đồng chặt chẽ.
  - Quản lý và tính toán dịch vụ (điện, nước, rác, internet...).
- 🖨️ **Tính tiền & Xuất hóa đơn**: Tự động chốt số điện nước, tính tiền hàng tháng, theo dõi công nợ, xuất hóa đơn sang file PDF rõ ràng, chuyên nghiệp.
- ⚡ **Hiệu năng & Ổn định**: Sử dụng Connection Pool (HikariCP) giúp tối ưu hóa xử lý đa luồng và tăng tốc độ tương tác với cơ sở dữ liệu.

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn ngữ Core**: Java 11
- **Quản lý dự án & Build**: Maven
- **Cơ sở dữ liệu**: MySQL 8+
- **Thư viện chính**:
  - `mysql-connector-j` (8.4.0): Trình điều khiển kết nối MySQL.
  - `gson` (2.10.1): Đóng gói/giải nén dữ liệu giao tiếp Client-Server.
  - `HikariCP` (5.0.1): Quản lý Connection Pool tối ưu cho DB.
  - `itextpdf` (5.5.13.3): Kết xuất và in hóa đơn PDF.
  - `flatlaf` (3.5.2): Giao diện Look & Feel hiện đại.

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

Dự án này đã được **tự động hóa hoàn toàn cấu trúc Database**! Hệ thống sẽ tự động tạo cơ sở dữ liệu `QUANLYPHONGTRO`, tạo toàn bộ các bảng và tự động chèn tài khoản mặc định vào database khi bạn chạy Server lần đầu. Bạn KHÔNG cần phải import file `.sql` bằng tay!

### 1. Yêu cầu hệ thống & Cấu hình:
- Đã cài đặt **Java JDK 11** trở lên.
- Đang mở phần mềm **MySQL Server** trên máy (cổng mặc định `3306`).
- **CẤU HÌNH DATABASE**: Cần cập nhật thông tin kết nối MySQL (Username, Password) tại các file sau cho khớp với cấu hình máy bạn (Mặc định đang để mật khẩu là `root123`):
  1. File `server_config.properties` (Thư mục gốc)
  2. *(Tùy chọn)* File `test/InitDB.java` (Nếu bạn muốn chạy file này để nạp DB thủ công).
  > 💡 *Ví dụ nếu bạn xài XAMPP không có mật khẩu thì đổi thành để trống: `db.password=`*
  > 💡 *Nếu máy bạn đã từng chạy bị lỗi tài khoản trước đó, hãy vào MySQL xóa database cũ (chạy lệnh `DROP DATABASE QUANLYPHONGTRO;`) để phần mềm có thể tự động tạo lại database mới hoàn chỉnh với đầy đủ dữ liệu khi bạn chạy Server.*

### 2. Khởi động phần mềm (Chạy theo thứ tự sau):
> Khuyến khích sử dụng **IntelliJ IDEA**, **Eclipse** hoặc **VS Code**. Hãy đợi Maven tải xong toàn bộ các thư viện (dependencies) trước khi chạy.

**▶️ Bước 1: Khởi động Server (Máy chủ)**
- Tìm và chạy file class `SocketServer` tại đường dẫn: `src/server/server/SocketServer.java`.
- *Ngay khi chạy, hãy để ý console log. Hệ thống sẽ kết nối MySQL, tự động tạo database `QUANLYPHONGTRO`, thiết lập cấu trúc bảng, và nạp 2 tài khoản mặc định.*

**▶️ Bước 2: Khởi động Client (Máy trạm)**
- Chạy class `LoginFrame` tại đường dẫn: `src/client/view/LoginFrame.java` (Nơi khởi tạo giao diện đăng nhập).
- Đăng nhập thử với tài khoản: `admin` / `admin123`.
- Bạn có thể chạy nhiều Client cùng lúc để test tính năng đồng bộ.

---

## 🌐 Hướng Dẫn Chạy Trên Nhiều Máy Trạm (Mạng LAN)

Hệ thống được thiết kế để một máy làm Server (Chủ), và nhiều máy khác làm Client (Trạm) cùng kết nối vào để làm việc.

**Tình huống 1: Chạy Server và Client trên CÙNG 1 MÁY**
- Mặc định, file `client_config.properties` đã cấu hình `SERVER_IP=localhost`. Bạn không cần chỉnh sửa gì thêm.

**Tình huống 2: Chạy Client ở MÁY KHÁC kết nối vào Máy Server (cùng chung mạng WiFi / LAN)**
1. **Trên Máy Server**: 
   - Mở Command Prompt (cmd) và gõ lệnh `ipconfig`.
   - Tìm dòng **IPv4 Address** (Ví dụ: `192.168.1.15`). Đây là IP của máy chủ.
   - (Lưu ý: Bạn nên tắt Tường lửa - Windows Defender Firewall hoặc tạo rule cho phép cổng `5000` đi qua).
2. **Trên Máy Client**:
   - Mở file `client_config.properties` nằm ở thư mục gốc của dự án.
   - Sửa dòng `SERVER_IP` thành địa chỉ IPv4 của máy Server vừa lấy được:
     ```properties
     SERVER_IP=192.168.1.15
     SERVER_PORT=5000
     ```
   - Chạy Client và đăng nhập bình thường!

---

## 🔐 Tài Khoản Đăng Nhập Mặc Định

| Vai Trò | Tên đăng nhập | Mật khẩu | Ghi chú |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` | Quản trị viên, toàn quyền hệ thống. |
| **User** | `user` | `user123` | Người dùng (nhân viên/khách). |

> 🔑 **Lưu ý:** Tài khoản User (khách thuê) sẽ được **tạo tự động** khi admin thêm khách thuê mới hoặc người dùng mới vào hệ thống (mật khẩu mặc định khởi tạo luôn là: `123456`).

---

## 📁 Cấu Trúc Mã Nguồn

```text
QuanLyPhongTroProMaxUltimate/
├── assets/                     # Chứa các file tĩnh như font, icon, hình ảnh tài nguyên.
├── sql/                        # Chứa file script QUANLIPHONGTRO.sql (Backup DB thủ công nếu cần).
├── src/                        # Thư mục mã nguồn Java chính.
│   ├── client/                 # Mã nguồn giao diện (Views) và logic xử lý của người dùng cuối.
│   ├── server/                 # Logic máy chủ, DAO xử lý DB, và Socket Server quản lý kết nối.
│   ├── common/                 # Các class dùng chung: Models (Entities), Enum, Utils.
│   └── Image/                  # Hình ảnh sử dụng riêng trong các Frame/Panel.
├── test/                       # Thư mục chứa các Unit test (nếu có).
├── pom.xml                     # Cấu hình Maven, chứa danh sách thư viện (Dependencies).
├── client_config.properties    # Cấu hình IP/Port kết nối tới Server cho Client.
└── server_config.properties    # Cấu hình thông tin DB, Port cho Server.
```
