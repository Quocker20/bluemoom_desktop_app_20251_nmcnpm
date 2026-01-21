
# 🏢 BlueMoon - Apartment Management System

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVC-0052CC?style=flat-square)
![Workflow](https://img.shields.io/badge/Workflow-AI%20Assisted-00A67E?style=flat-square)

> **Hệ thống quản lý chung cư Desktop toàn diện, được xây dựng với kiến trúc Java Core vững chắc kết hợp quy trình phát triển giao diện hiện đại hỗ trợ bởi AI.**

## 📖 Giới thiệu (The Story)

**BlueMoon Management** giải quyết bài toán quản lý dữ liệu cư dân và tài chính phức tạp tại các chung cư vừa và nhỏ, thay thế hoàn toàn quy trình sổ sách thủ công.

Dự án này là minh chứng cho **Quy trình phát triển phần mềm lai (Hybrid Development Workflow)** mà tôi đang theo đuổi:
1.  **Backend (Hand-coded):** Viết tay 100% để đảm bảo tuân thủ nghiêm ngặt các nguyên lý thiết kế phần mềm, bảo mật và toàn vẹn dữ liệu.
2.  **Frontend (AI-Accelerated):** Tận dụng Generative AI để vượt qua rào cản của công nghệ cũ (Java Swing), tập trung thời gian vào trải nghiệm người dùng (UX) và logic nghiệp vụ.

---

## 📸 Hình ảnh Demo

| Tổng quan (Dashboard) | Báo cáo & Thống kê |
| :---: | :---: |
| ![Dashboard](src/main/resources/images/dashboard.png) | ![Report](src/main/images/report.png) |

---

## 🛠️ Công nghệ & Kiến trúc

### ⚙️ Backend: Java Core & Design Patterns
Tôi xây dựng Backend hoàn toàn thủ công (No Framework) để thể hiện sự am hiểu sâu sắc về nền tảng ngôn ngữ:
* **Mô hình:** **MVC** (Model - View - Controller) phân tách rõ ràng luồng dữ liệu.
* **Data Access:** Sử dụng mẫu **DAO (Data Access Object)** và **Singleton** để quản lý kết nối JDBC hiệu quả.
* **Database:** MySQL được chuẩn hóa (Normalization) để tối ưu truy vấn và ràng buộc dữ liệu.
* **Libraries:**
    * `mysql-connector-j`: Kết nối cơ sở dữ liệu.
    * `apache-poi`: Xuất báo cáo ra file Excel chuyên nghiệp.

### 🎨 Frontend: AI-Driven Workflow
Để hiện đại hóa giao diện Java Swing:
1.  **Design:** Thiết kế Prototype và luồng người dùng trên **Figma**.
2.  **Generation:** Sử dụng **Google Gemini** để sinh mã nguồn giao diện (View) chi tiết.
3.  **Integration (Vai trò của tôi):**
    * Tái cấu trúc code AI sinh ra thành các Components tái sử dụng (`RoundedPanel`, `ColoredButton`).
    * Xử lý sự kiện (Event Handling) và đấu nối dữ liệu từ Backend.
    * Debug và tinh chỉnh Pixel-perfect.

---

## 🚀 Chức năng Chính

### 1. Quản lý Cư dân Chuyên sâu
* Quản lý **Hộ khẩu** và **Nhân khẩu** (theo dõi quan hệ, CCCD, ngày sinh).
* Ghi nhận biến động cư trú: **Tạm trú**, **Tạm vắng**, **Khai tử** (Có kiểm tra logic ngày tháng).
* Tự động cập nhật trạng thái phòng (Trống/Có người ở) dựa trên dữ liệu hộ khẩu.

### 2. Tài chính & Thu phí (Tự động hóa)
* **Cấu hình linh hoạt:** Thiết lập đơn giá phí quản lý (theo m²), phí gửi xe (Ô tô/Xe máy), và các khoản đóng góp tự nguyện.
* **Tự động tính toán:** Batch processing tạo công nợ hàng tháng cho hàng trăm hộ dân chỉ với 1 click.
* **Thanh toán:** Ghi nhận lịch sử đóng tiền, hỗ trợ thanh toán từng phần.

### 3. Tiện ích & Báo cáo
* Quản lý bãi xe: Theo dõi phương tiện, biển số xe của từng hộ.
* **Dashboard:** Biểu đồ trực quan về doanh thu và cơ cấu dân số (Vẽ thủ công bằng Graphics2D).
* **Xuất Excel:** Trích xuất danh sách Tạm trú/Tạm vắng phục vụ báo cáo hành chính. 

---

## 📂 Cấu trúc Source Code

```text
src/main/java/com/bluemoon/app
├── controller/          # Xử lý Logic nghiệp vụ (Billing, Resident, Statistic...)
├── dao/                 # Lớp truy cập dữ liệu (JDBC, SQL Queries)
├── model/               # Các POJO Mapping với bảng CSDL
├── view/                # Giao diện người dùng (Swing Panels & Dialogs)
│   ├── payment/         # Giao diện Thu phí
│   ├── resident/        # Giao diện Cư dân
│   ├── statistic/       # Giao diện Báo cáo (Charts)
│   ├── system/          # Login, MainFrame
│   └── vehicle/         # Quản lý xe
└── util/                # DatabaseConnector, Security, Constants

```

---

## ⚙️ Hướng dẫn Cài đặt

### Yêu cầu

* **Java JDK:** 11 trở lên.
* **MySQL Server:** 8.0 trở lên.
* **IDE:** IntelliJ IDEA, Eclipse hoặc VS Code.

### Các bước triển khai

**Bước 1: Clone Repository**

```bash
git clone [https://github.com/Quocker20/bluemoom_desktop_app_20251_nmcnpm.git](https://github.com/Quocker20/bluemoom_desktop_app_20251_nmcnpm.git)

```

**Bước 2: Cấu hình Database**

1. Mở MySQL Workbench (hoặc tool tương tự).
2. Tạo database mới tên `bluemoon_db`.
3. Import file script tại: `src/main/resources/database/bluemoon_schema.sql` (File này đã bao gồm cấu trúc bảng và dữ liệu mẫu).

**Bước 3: Cấu hình Kết nối**
Mở file `src/main/java/com/bluemoon/app/util/DatabaseConnector.java` và cập nhật thông tin MySQL của bạn:

```java
private static final String URL = "jdbc:mysql://localhost:3306/bluemoon_db";
private static final String USER = "root"; // User MySQL của bạn
private static final String PASS = "your_password"; // Mật khẩu MySQL của bạn

```

**Bước 4: Chạy ứng dụng**

* Chạy file: `src/main/java/com/bluemoon/app/view/system/LoginFrame.java` (hoặc `App.java` nếu có).
* **Tài khoản Admin mặc định:**
* Username: `admin`
* Password: `123456` (Mật khẩu này khớp với hash trong DB mẫu).



---

## 📬 Liên hệ

Nếu bạn quan tâm đến dự án hoặc muốn trao đổi về quy trình kết hợp **Software Architecture** với **AI Coding**, hãy liên hệ với tôi:

* **Tác giả:** Vũ Quốc Anh
* **Email:** quocanh20705@gmail.com
* **GitHub:** [Quocker20](https://www.google.com/search?q=https://github.com/Quocker20)

---

*© 2024 Vu Quoc Anh. All Rights Reserved.*

```

```
```