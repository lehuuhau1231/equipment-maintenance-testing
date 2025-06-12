🛠️ Đề tài: Quản lý, Bảo trì Thiết bị
🎯 Mục tiêu
Xây dựng hệ thống quản lý và bảo trì thiết bị sử dụng JavaFX trên môi trường NetBeans, đồng thời thiết kế các test case đảm bảo hệ thống hoạt động đúng theo các yêu cầu và ràng buộc nghiệp vụ.

📚 Chức năng chính
1. Theo dõi tình trạng thiết bị
Không cho phép cập nhật thiết bị đã thanh lý.
Hiển thị đúng trạng thái: Đang hoạt động, Hỏng hóc, Đang sửa.
Gửi thông báo khi thiết bị cần bảo trì định kỳ.
2. Lập kế hoạch bảo trì
Không cho phép lập lịch trùng giờ.
Chỉ cho phép lập lịch cho thiết bị đang hoạt động.
Gửi email nhắc kỹ thuật viên trước 24h.
3. Quản lý lịch sử sửa chữa
Ghi đầy đủ thời gian, người thực hiện mỗi lần sửa chữa.
Không cho phép sửa đổi lịch sử sau khi lưu.
Tính chính xác chi phí sửa chữa.

🔐 Ràng buộc nghiệp vụ cần kiểm thử
#	Ràng buộc
1. Bảo trì lần 1 phải cách ngày nhập thiết bị từ 3–6 tháng. Lần 2 cách lần 1 từ 3–6 tháng.
2. Ngày sửa chữa không được lệch quá 3 ngày so với lịch sửa.
3. Chỉ được cập nhật lịch bảo trì trước ngày bảo trì ít nhất 2 ngày.
4. Khi chuyển thiết bị sang “đã thanh lý”, phải hủy toàn bộ lịch bảo trì/sửa chữa.
5. Mỗi thiết bị chỉ được lập tối đa 2 lần bảo trì.
6. Không nhập ký tự đặc biệt vào các ô nhập.
7. Không cho phép cập nhật trạng thái từ “đang hoạt động” → “đang sửa” trực tiếp.
8. Không được chuyển từ “hỏng hóc” → “đang hoạt động” trực tiếp.
9. Chỉ được chuyển trạng thái từ “bảo trì” hoặc “đang sửa” → “đang hoạt động” sau ít nhất 1 ngày.

🧪 Test Case
Các test case được viết nhằm:
Đảm bảo đúng logic ràng buộc nghiệp vụ.
Đảm bảo hoạt động chính xác của các chức năng.

🧰 Công nghệ sử dụng
Ngôn ngữ: Java
UI: JavaFX
IDE: NetBeans
CSDL: MySQL
Unit test: JUnit

