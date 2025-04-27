package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.ThietBi;

import java.sql.CallableStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;


public class TestThietBiServices {

    private static ThietBiServices thietBiServices;
    private static Connection connection;

    @InjectMocks
    private ThietBiServices services;

    @Mock
    private Connection mockConnection;

    @Mock
    private CallableStatement mockCallableStatement;

    @BeforeEach
    void setUpDatabase() throws SQLException {
        // Thiết lập kết nối H2 in-memory
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcUtils.setConnection(connection);

        thietBiServices = new ThietBiServices();
        AdminServices.idAdmin = 1; // Giả lập idAdmin

        // Tạo bảng thử nghiệm
        try (var stmt = connection.createStatement()) {
            // Tạo bảng trangthai
            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuathietbi, baotri, thietbi, trangthai;");
            stmt.executeUpdate(
                    "CREATE TABLE trangthai ("
                    + "id INT PRIMARY KEY, "
                    + "tenTrangThai VARCHAR(50))"
            );
            stmt.executeUpdate(
                    "INSERT INTO trangthai (id, tenTrangThai) VALUES "
                    + "(1, 'Đã thanh lý'), "
                    + "(2, 'Đang hoạt động'), "
                    + "(3, 'Đang sửa'), "
                    + "(4, 'Hỏng hóc'), "
                    + "(5, 'Bảo trì')"
            );

            // Tạo bảng thietbi
            stmt.executeUpdate(
                    "CREATE TABLE thietbi ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "tenThietBi VARCHAR(50), "
                    + "ngayNhap DATE, "
                    + "idTrangThai INT, "
                    + "thanhLy DATE, "
                    + "thongBao VARCHAR(255), "
                    + "idAdmin INT)"
            );

            // Tạo bảng baotri và nhanviensuathietbi
            stmt.executeUpdate(
                    "CREATE TABLE nhanviensuathietbi ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "idThietBi INT, "
                    + "chiPhi DECIMAL, "
                    + "moTa VARCHAR(255))"
            );
            stmt.executeUpdate(
                    "CREATE TABLE baotri ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "idThietBi INT, "
                    + "ngayBaoTri DATETIME, "
                    + "idNhanVien INT, "
                    + "FOREIGN KEY (idNhanVien) REFERENCES nhanviensuathietbi(id))"
            );
            stmt.executeUpdate("INSERT INTO nhanviensuathietbi (idThietBi, chiPhi, moTa) VALUES (1, 500000, 'Sửa mực')");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testGetThietBi_EmptyKeyword() throws SQLException {
        // Arrange: Thêm dữ liệu
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        // Act
        List<ThietBi> result = thietBiServices.getThietBi("");

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 thiết bị");
        ThietBi tb = result.get(0);
        System.out.println(tb.getNgayNhap());
        assertEquals("Laptop", tb.getTenThietBi());
        assertEquals(LocalDate.of(2023, 10, 1), tb.getNgayNhap().toLocalDate());
        assertEquals(2, tb.getIdTrangThai());
        assertNull(tb.getNgayThanhLy());
        assertNull(tb.getThongBao());
    }

    @Test
    void testGetThietBi_WithKeyword() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
            stmt.setString(1, "Desktop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-02"));
            stmt.setInt(3, 3);
            stmt.executeUpdate();
        }
        // Act
        List<ThietBi> result = thietBiServices.getThietBi("Lapt");

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 thiết bị");
        assertEquals("Laptop", result.get(0).getTenThietBi());
    }

    @Test
    void testGetImportDateEquipment() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        // Act
        List<ThietBi> result = thietBiServices.getImportDateEquipment();

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 thiết bị");
        ThietBi tb = result.get(0);
        assertEquals(LocalDate.of(2023, 10, 1), tb.getNgayNhap().toLocalDate());
    }

    @Test
    void testGetImportDateById_Found() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        // Act
        LocalDate result = thietBiServices.getImportDateById(1);

        // Assert
        assertEquals(LocalDate.of(2023, 10, 1), result, "Ngày nhập phải khớp");
    }

    @Test
    void testGetImportDateById_NotFound() throws SQLException {
        // Kiểm tra trước
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM thietbi WHERE id = ?")) {
            stmt.setInt(1, 999);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(0, rs.getInt(1), "Không có thiết bị với ID=999");
        }

        // Act
        LocalDate result = thietBiServices.getImportDateById(999);

        // Assert
        assertNull(result, "Phải trả về null khi không tìm thấy");
    }

    @Test
    void testIsExist_True() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        // Act
        boolean result = thietBiServices.isExist(1);

        // Assert
        assertTrue(result, "Phải trả về true khi thiết bị tồn tại");
    }

    @Test
    void testIsExist_False() throws SQLException {
        // Kiểm tra trước
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM thietbi WHERE id = ?")) {
            stmt.setInt(1, 999);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(0, rs.getInt(1), "Không có thiết bị với ID=999");
        }

        // Act
        boolean result = thietBiServices.isExist(999);

        // Assert
        assertFalse(result, "Phải trả về false khi thiết bị không tồn tại");
    }

    @Test
    void testIsNameExist_AddAction_True() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        // Act
        boolean result = thietBiServices.isNameExist("Laptop", true);

        // Assert
        assertTrue(result, "Phải trả về true khi tên tồn tại trong chế độ thêm");
    }

    @Test
    void testIsNameExist_UpdateAction_False() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-02"));
            stmt.executeUpdate();
        }

        // Kiểm tra trước
        try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM thietbi WHERE tenThietBi = ?")) {
            stmt.setString(1, "Laptop");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            assertEquals(2, rs.getInt(1), "Phải có 2 thiết bị với tên Laptop");
        }

        // Act
        boolean result = thietBiServices.isNameExist("Laptop", false);

        // Assert
        assertFalse(result, "Phải trả về false khi tên trùng quá 1 lần trong chế độ cập nhật");
    }

    @Test
    void testAddThietBi_Success() throws SQLException {
        // Act
        thietBiServices.addThietBi("Laptop", LocalDate.now(), 2);
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            JdbcUtils.setConnection(connection);
        }
        // Kiểm tra sau
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM thietbi WHERE tenThietBi = ?")) {
            stmt.setString(1, "Laptop");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có thiết bị vừa thêm");
            assertEquals("Laptop", rs.getString("tenThietBi"));
            assertEquals(2, rs.getInt("idTrangThai"));
            assertEquals(1, rs.getInt("idAdmin"));
        }
    }

    @Test
    void testValidateAddThietBi_EmptyFields() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateAddThietBi("", null, 0));
        assertEquals("Vui lòng điền đầy đủ thông tin", thrown.getMessage());
    }

    @Test
    void testValidateAddThietBi_NameTooLong() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateAddThietBi("a".repeat(51), LocalDate.now(), 2));
        assertEquals("Tên thiết bị tối đa 50 ký tự", thrown.getMessage());
    }

    @Test
    void testValidateAddThietBi_SpecialCharacters() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateAddThietBi("Laptop#", LocalDate.now(), 2));
        assertEquals("Tên thiết bị không được chứa ký tự đặc biệt", thrown.getMessage());
    }

    @Test
    void testAddThietBi_InvalidNgayNhap() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateAddThietBi("Laptop", yesterday, 2));
        assertEquals("Ngày nhập phải là ngày hiện tại", thrown.getMessage());
    }

    @Test
    void testAddThietBi_InvalidTrangThai() {
        // Arrange
        int invalidTrangThai = 1; // "Đã thanh lý"

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateAddThietBi("Laptop", LocalDate.now(), invalidTrangThai));
        assertEquals("Quy định trạng thái thiết bị khi thêm là: ĐANG HOẠT ĐỘNG HOẶC HỎNG HÓC", thrown.getMessage());
    }


    @Test
    public void testUpdateThietBi_WithThanhLy_ShouldDeleteSchedule() throws SQLException {
        // 1. Tạo lịch bảo trì cho thiết bị id = 1
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareStatement(
                    "INSERT INTO baotri (idThietBi, ngayBaoTri, idNhanVien) VALUES (?, ?, ?)"
            );
            stm.setInt(1, 1); // Thiết bị ID 1 (đã tồn tại)
            stm.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stm.setInt(3, 1); // idNhanVien (giả sử nhân viên id = 1 cũng đã có)
            stm.executeUpdate();
        }

        // 2. Gọi hàm update thiết bị, thanh lý luôn
        LocalDate ngayThanhLy = LocalDate.now().plusDays(1);
        thietBiServices.updateThietBi(1, "Laptop mini", ngayThanhLy, 1);

        // 3. Kiểm tra thiết bị đã cập nhật đúng trạng thái và ngày thanh lý
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(
                    "SELECT idTrangThai, thanhLy FROM thietbi WHERE id = ?"
            );
            stm.setInt(1, 1);
            ResultSet rs = stm.executeQuery();
            assertTrue(rs.next());
            assertEquals(1, rs.getInt("idTrangThai"));
            assertEquals(ngayThanhLy, rs.getDate("thanhLy").toLocalDate());
        }

        // 4. Kiểm tra lịch bảo trì đã bị xóa hết
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(
                    "SELECT COUNT(*) FROM baotri WHERE idThietBi = ?"
            );
            stm.setInt(1, 1);
            ResultSet rs = stm.executeQuery();
            rs.next();
            assertEquals(0, rs.getInt(1));
        }
    }

    @Test
    void testValidateUpdateThietBi_EmptyFields() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateUpdateThietBi(1, "", null, 0));
        assertEquals("Vui lòng điền đầy đủ thông tin", thrown.getMessage());
    }

    @Test
    void testValidateUpdateThietBi_InvalidStatus() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2); // Đang hoạt động
            stmt.setInt(4, 1);
            stmt.executeUpdate();
            connection.commit();
        }

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateUpdateThietBi(1, "Laptop Updated", LocalDate.now(), 3)); // Đang sửa
        assertEquals("Không được cập nhật thiết bị ĐANG SỬA", thrown.getMessage());
    }

    @Test
    void testValidateUpdateThietBi_ThanhLyWithoutDate() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2); // Đang hoạt động
            stmt.setInt(4, 1);
            stmt.executeUpdate();
            connection.commit();
        }

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateUpdateThietBi(1, "Laptop Updated", null, 1)); // Đã thanh lý
        assertEquals("Vui lòng điền ngày thanh lý khi trạng thái là 'Đã thanh lý'", thrown.getMessage());
    }

    @Test
    void testCheckEmptyFields_EmptyName() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkEmptyFields("", 2));
        assertEquals("Vui lòng điền đầy đủ thông tin", thrown.getMessage());
    }

    @Test
    void testCheckEmptyFields_WhitespaceName() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkEmptyFields("  ", 2));
        assertEquals("Vui lòng điền đầy đủ thông tin", thrown.getMessage());
    }

    @Test
    void testCheckEmptyFields_InvalidStatus() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkEmptyFields("Laptop", 0));
        assertEquals("Vui lòng điền đầy đủ thông tin", thrown.getMessage());
    }

    @Test
    void testCheckStatusUpdateRules_ThanhLyStatus() throws SQLException {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkStatusUpdateRules(1, 2)); // Từ Đã thanh lý sang Đang hoạt động
        assertEquals("Không được cập nhật thiết bị ĐÃ THANH LÝ", thrown.getMessage());
    }

    @Test
    void testCheckStatusUpdateRules_BaoTriStatus() throws SQLException {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkStatusUpdateRules(5, 2)); // Từ Bảo trì sang Đang hoạt động
        assertEquals("Không được cập nhật thiết bị ĐANG BẢO TRÌ", thrown.getMessage());
    }

    @Test
    void testCheckStatusUpdateRules_HongHocToHoatDong() throws SQLException {

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkStatusUpdateRules(4, 2));

        assertEquals("Không thể cập nhật khi trạng thái 'hỏng hóc' chuyển sang 'đang hoạt động'", thrown.getMessage());
    }

    @Test
    void testCheckStatusUpdateRules_HoatDongToDangSua() throws SQLException {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkStatusUpdateRules(2, 3)); // Từ Đang hoạt động sang Đang sửa
        assertEquals("Không được cập nhật thiết bị ĐANG SỬA", thrown.getMessage());
    }

    @Test
    void testCheckStatusUpdateRules_ValidTransition() throws SQLException {
        // Act & Assert
        assertDoesNotThrow(() -> thietBiServices.checkStatusUpdateRules(2, 4)); // Từ Đang hoạt động sang Hỏng hóc
    }

    @Test
    void testCheckThanhLyDateRules_ThanhLyNoDate() throws SQLException {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkThanhLyDateRules(1, 1, null)); // Đã thanh lý, ngày null
        assertEquals("Vui lòng điền ngày thanh lý khi trạng thái là 'Đã thanh lý'", thrown.getMessage());
    }

    @Test
    void testCheckThanhLyDateRules_PastDate() throws SQLException {
        // Arrange
        LocalDate pastDate = LocalDate.now().minusDays(2);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.checkThanhLyDateRules(1, 1, pastDate)); // Đã thanh lý, ngày quá khứ
        assertEquals("Ngày thanh lý phải từ ngày hiện tại trở đi", thrown.getMessage());
    }

    @Test
    void testCheckThanhLyDateRules_ValidDate() throws SQLException {
        // Arrange
        LocalDate today = LocalDate.now();

        // Act & Assert
        assertDoesNotThrow(() -> thietBiServices.checkThanhLyDateRules(1, 1, today)); // Đã thanh lý, ngày hợp lệ
    }

    @Test
    void testContainsSpecialCharacters_True() {
        // Act
        boolean result = thietBiServices.containsSpecialCharacters("Laptop#");

        // Assert
        assertTrue(result, "Phải phát hiện ký tự đặc biệt");
    }

    @Test
    void testContainsSpecialCharacters_False() {
        // Act
        boolean result = thietBiServices.containsSpecialCharacters("Laptop 123");

        // Assert
        assertFalse(result, "Không có ký tự đặc biệt");
    }

    @Test
    void testGetCurrentStatus_Found() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }

        // Kiểm tra trước
        try (PreparedStatement stmt = connection.prepareStatement("SELECT idTrangThai FROM thietbi WHERE id = ?")) {
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có thiết bị với ID=1");
            assertEquals(2, rs.getInt("idTrangThai"));
        }

        // Act
        int result = thietBiServices.getCurrentStatus(1);

        // Assert
        assertEquals(2, result, "Trạng thái phải khớp");
    }

    @Test
    void testGetIdEquipment_Found() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }

        // Kiểm tra trước
        try (PreparedStatement stmt = connection.prepareStatement("SELECT id FROM thietbi WHERE tenThietBi = ?")) {
            stmt.setString(1, "Laptop");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có thiết bị với tên Laptop");
        }

        // Act
        int result = thietBiServices.getIdEquipment("Laptop");

        // Assert
        assertEquals(1, result, "ID phải khớp");
    }

    @Test
    void testGetNameById_Found() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }

        // Kiểm tra trước
        try (PreparedStatement stmt = connection.prepareStatement("SELECT tenThietBi FROM thietbi WHERE id = ?")) {
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có thiết bị với ID=1");
        }

        // Act
        String result = thietBiServices.getNameById(1);

        // Assert
        assertEquals("Laptop", result, "Tên phải khớp");
    }

    @Test
    void testAddNotification_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }    // Act
        thietBiServices.addNotification(1, "Test notification");
        if (connection != null || !connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            JdbcUtils.setConnection(connection);
        }
        // Kiểm tra sau
        try (PreparedStatement stmt = connection.prepareStatement("SELECT thongBao FROM thietbi WHERE id = ?")) {
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có thiết bị sau khi cập nhật");
            assertEquals("Test notification", rs.getString("thongBao"), "Thông báo phải khớp");
        }
    }

    @Test
    void testUpdateStatus_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "Laptop");
            stmt.setDate(2, java.sql.Date.valueOf("2023-10-01"));
            stmt.setInt(3, 2);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        // Act
        thietBiServices.updateStatus(1, 3);
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            JdbcUtils.setConnection(connection);
        }
        // Kiểm tra sau
        try (PreparedStatement stmt = connection.prepareStatement("SELECT idTrangThai FROM thietbi WHERE id = ?")) {
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có thiết bị sau khi cập nhật");
            assertEquals(3, rs.getInt("idTrangThai"), "Trạng thái phải được cập nhật thành 3");
        }
    }


    @Test
    void testValidateSearch_TooLong() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateSearch("a".repeat(51)));
        assertEquals("Tối đa 50 ký tự", thrown.getMessage());
    }

    @Test
    void testValidateSearch_SpecialCharacters() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> thietBiServices.validateSearch("Search#"));
        assertEquals("Không được chứa ký tự đặc biệt", thrown.getMessage());
    }
}
