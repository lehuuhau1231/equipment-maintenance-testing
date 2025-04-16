package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.NhanVienSuaThietBi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

public class TestNhanVienSuaThietBiServices {

    private static NhanVienSuaThietBiServices services;
    private static Connection connection;

    @BeforeEach
    void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        JdbcUtils.setConnection(connection);
        
        services = new NhanVienSuaThietBiServices();
        AdminServices.idAdmin = 1;

        try (var stmt = connection.createStatement()) {
            // Xóa các bảng nếu đã tồn tại
            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuathietbi");
            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuachua");
            stmt.executeUpdate("DROP TABLE IF EXISTS thietbi");
            stmt.executeUpdate("DROP TABLE IF EXISTS admin");
            stmt.executeUpdate("DROP TABLE IF EXISTS trangthai");

            // Tạo bảng trangthai
            stmt.executeUpdate(
                    "CREATE TABLE trangthai ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "tenTrangThai VARCHAR(50) NOT NULL)"
            );
            stmt.executeUpdate(
                    "INSERT INTO trangthai (id, tenTrangThai) VALUES "
                    + "(2, 'Đang hoạt động')"
            );

            // Tạo bảng admin
            stmt.executeUpdate(
                    "CREATE TABLE admin ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "username VARCHAR(50) NOT NULL, "
                    + "password VARCHAR(100) NOT NULL, "
                    + "ho VARCHAR(20), "
                    + "ten VARCHAR(20), "
                    + "email VARCHAR(50), "
                    + "UNIQUE (username), "
                    + "UNIQUE (email))"
            );
            stmt.executeUpdate(
                    "INSERT INTO admin (id, username, password, ho, ten, email) VALUES "
                    + "(1, 'admin', 'Lehuuhau1231@', 'Lê', 'Hậu', 'lehuuhau1231@gmail.com')"
            );

            // Tạo bảng thietbi
            stmt.executeUpdate(
                    "CREATE TABLE thietbi ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "tenThietBi VARCHAR(50) NOT NULL, "
                    + "thanhLy DATE, "
                    + "ngayNhap DATE NOT NULL, "
                    + "thongBao VARCHAR(50), "
                    + "idTrangThai INT NOT NULL, "
                    + "idadmin INT NOT NULL, "
                    + "FOREIGN KEY (idTrangThai) REFERENCES trangthai(id), "
                    + "FOREIGN KEY (idadmin) REFERENCES admin(id))"
            );
            stmt.executeUpdate(
                    "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idadmin) VALUES "
                    + "('Laptop', '2023-10-01', 2, 1)"
            );

            // Tạo bảng nhanviensuachua
            stmt.executeUpdate(
                    "CREATE TABLE nhanviensuachua ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "tenNV VARCHAR(50) NOT NULL, "
                    + "ngaySinh DATE NOT NULL, "
                    + "CCCD CHAR(12) NOT NULL, "
                    + "soDT CHAR(10) NOT NULL, "
                    + "diaChi VARCHAR(250), "
                    + "email VARCHAR(50) NOT NULL, "
                    + "idadmin INT NOT NULL, "
                    + "UNIQUE (email), "
                    + "UNIQUE (CCCD), "
                    + "UNIQUE (soDT), "
                    + "FOREIGN KEY (idadmin) REFERENCES admin(id))"
            );
            stmt.executeUpdate(
                    "INSERT INTO nhanviensuachua (tenNV, ngaySinh, CCCD, soDT, email, idadmin) VALUES "
                    + "('Lê Hữu Hậu', '2004-01-01', '123456789012', '0123456789', 'lehuuhau1231@gmail.com', 1)"
            );

            // Tạo bảng nhanviensuathietbi
            stmt.executeUpdate(
                    "CREATE TABLE nhanviensuathietbi ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "ngaySua DATETIME NOT NULL, "
                    + "idThietBi INT NOT NULL, "
                    + "idNhanVien INT NOT NULL, "
                    + "chiPhi BIGINT, "
                    + "moTa VARCHAR(250), "
                    + "FOREIGN KEY (idThietBi) REFERENCES thietbi(id), "
                    + "FOREIGN KEY (idNhanVien) REFERENCES nhanviensuachua(id))"
            );
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testGetNhanVienSuaThietBi_FullData_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 500000);
            stmt.setString(5, "Thay pin");
            stmt.executeUpdate();
        }

        // Act
        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(true);

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 bản ghi sửa chữa");
        NhanVienSuaThietBi repair = result.get(0);
        assertEquals(1, repair.getId());
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), repair.getNgaySua());
        assertEquals("Laptop", repair.getTenThietBi());
        assertEquals("Lê Hữu Hậu", repair.getTenNV());
        assertEquals(500000, repair.getChiPhi());
        assertEquals("Thay pin", repair.getMoTa());
    }

    @Test
    void testGetNhanVienSuaThietBi_FullData_NoMatches() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 0);
            stmt.setNull(5, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
        }

        // Act
        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(true);

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bản ghi hợp lệ");
    }

    @Test
    void testGetNhanVienSuaThietBi_NotFullData_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 0);
            stmt.setNull(5, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
        }

        // Act
        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(false);

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 bản ghi sửa chữa");
        NhanVienSuaThietBi repair = result.get(0);
        assertEquals(1, repair.getId());
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), repair.getNgaySua());
        assertEquals("Laptop", repair.getTenThietBi());
        assertEquals("Lê Hữu Hậu", repair.getTenNV());
    }

    @Test
    void testGetNhanVienSuaThietBi_NotFullData_NoMatches() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 500000);
            stmt.setString(5, "Thay pin");
            stmt.executeUpdate();
        }

        // Act
        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(false);

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bản ghi hợp lệ");
    }

    @Test
    void testGetListDateTime_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 0);
            stmt.executeUpdate();
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.executeUpdate();
        }

        // Act
        List<LocalDateTime> result = services.getListDateTime(1);

        // Assert
        assertEquals(2, result.size(), "Phải trả về 2 ngày sửa");
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), result.get(0));
        assertEquals(LocalDateTime.of(2023, 10, 2, 10, 0), result.get(1));
    }

    @Test
    void testGetListDateTime_NoMatches() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 500000);
            stmt.executeUpdate();
        }

        // Act
        List<LocalDateTime> result = services.getListDateTime(1);

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có ngày hợp lệ");
    }

    @Test
    void testGetListNotRepair_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 0);
            stmt.executeUpdate();
        }

        // Act
        List<NhanVienSuaThietBi> result = services.getListNotRepair();

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 bản ghi chưa sửa");
        NhanVienSuaThietBi repair = result.get(0);
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), repair.getNgaySua());
        assertEquals(1, repair.getIdThietBi());
        assertEquals(1, repair.getIdNhanVien());
    }

    @Test
    void testGetListNotRepair_NoMatches() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 500000);
            stmt.executeUpdate();
        }

        // Act
        List<NhanVienSuaThietBi> result = services.getListNotRepair();

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bản ghi chưa sửa");
    }

    @Test
    void testRepairScheduleTimes_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setNull(4, java.sql.Types.BIGINT);
            stmt.setNull(5, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.executeUpdate();
        }

        // Act
        int result = services.repairScheduleTimes(1);

        // Assert
        assertEquals(2, result, "Phải trả về 2 lần lên lịch sửa chữa");
    }

    @Test
    void testRepairScheduleTimes_NoMatches() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setLong(4, 500000);
            stmt.setString(5, "Thay pin");
            stmt.executeUpdate();
        }

        // Act
        int result = services.repairScheduleTimes(1);

        // Assert
        assertEquals(0, result, "Phải trả về 0 khi không có lịch sửa chữa");
    }

    @Test
    void testAddRepairSchedule_Success() throws SQLException {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.of(2023, 10, 1, 10, 0);

        // Act
        services.addRepairSchedule(ngaySua, 1, 1);
        if (connection != null || !connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            connection.setAutoCommit(false);
            JdbcUtils.setConnection(connection);
        }
        // Assert
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT ngaySua, idThietBi, idNhanVien FROM nhanviensuathietbi WHERE id = 1")) {
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), "Phải có bản ghi lịch sửa chữa");
            assertEquals(Timestamp.valueOf(ngaySua), rs.getTimestamp("ngaySua"));
            assertEquals(1, rs.getInt("idThietBi"));
            assertEquals(1, rs.getInt("idNhanVien"));
        }
    }

    @Test
    void testValidateAddRepairSchedule_Success() throws SQLException {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.now();

        // Act & Assert
        assertDoesNotThrow(() -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Không nên ném ngoại lệ khi dữ liệu hợp lệ");
    }

    @Test
    void testValidateAddRepairSchedule_NullNgaySua() {
        // Arrange
        LocalDateTime ngaySua = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Phải ném ngoại lệ khi ngaySua là null");
        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_InvalidIdThietBi() {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.of(2023, 10, 2, 10, 0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 0, 1),
                "Phải ném ngoại lệ khi idThietBi <= 0");
        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_InvalidIdNhanVien() {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.of(2023, 10, 2, 10, 0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 0),
                "Phải ném ngoại lệ khi idNhanVien <= 0");
        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_ExistingSchedule() throws SQLException {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.of(2023, 10, 2, 10, 0);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setNull(4, java.sql.Types.BIGINT);
            stmt.setNull(5, java.sql.Types.VARCHAR);
            stmt.executeUpdate();
        }

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Phải ném ngoại lệ khi thiết bị đã có lịch sửa chữa");
        assertEquals("Trong một thời điểm thiết bị chỉ được lập lịch sửa 1 lần", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_TimeConflict() throws SQLException {
        // Arrange
        LocalDateTime ngaySua = LocalDate.now().atTime(9, 0);

        try (
                // Thêm thiết bị
                PreparedStatement insertThietBi = connection.prepareStatement(
                        "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idadmin) VALUES ('Ipad', '2023-10-01', 2, 1)"); PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien) VALUES (?, ?, ?)")) {
            // Thực hiện insert thiết bị
            insertThietBi.executeUpdate();

            // Thêm 2 lịch sửa vào cùng thời điểm để tạo xung đột
            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.executeUpdate();

            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
            stmt.setInt(2, 2);
            stmt.executeUpdate();
        }

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Phải ném ngoại lệ khi nhân viên trùng giờ");

        assertEquals("Lỗi! Nhân viên làm trùng giờ", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_OverWorkload() throws SQLException {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.now();
        LocalDate date = LocalDate.now();
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien) VALUES (?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf(date.atTime(9, 0)));
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.executeUpdate();
            stmt.setTimestamp(1, Timestamp.valueOf(date.atTime(10, 0)));
            stmt.executeUpdate();
            stmt.setTimestamp(1, Timestamp.valueOf(date.atTime(11, 0)));
            stmt.executeUpdate();
            stmt.setTimestamp(1, Timestamp.valueOf(date.atTime(12, 0)));
            stmt.executeUpdate();
        }

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Phải ném ngoại lệ khi nhân viên làm quá 3 công việc/ngày");
        assertEquals("Nhân viên chỉ được làm tối đa 3 công việc 1 ngày", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_InvalidDate_BeforeRange() {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.of(2023, 9, 29, 10, 0); // Trước 2023-10-01

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Phải ném ngoại lệ khi ngày sửa ngoài khoảng 0-3 ngày");
        assertEquals("Ngày sửa phải nằm trong 3 ngày kể từ ngày hiện tại", exception.getMessage());
    }

    @Test
    void testValidateAddRepairSchedule_InvalidDate_AfterRange() {
        // Arrange
        LocalDateTime ngaySua = LocalDateTime.of(2023, 10, 5, 10, 0); // Sau 2023-10-04

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
                "Phải ném ngoại lệ khi ngày sửa ngoài khoảng 0-3 ngày");
        assertEquals("Ngày sửa phải nằm trong 3 ngày kể từ ngày hiện tại", exception.getMessage());
    }
}
