package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.BaoTri;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author lehuu
 */
public class TestBaoTriServices {

    private static BaoTriServices baoTriServices;
    private static Connection connection;

    @BeforeEach
    void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        connection.setAutoCommit(false);
        JdbcUtils.setConnection(connection);

        // Khởi tạo dịch vụ (giả sử là BaoTriServices hoặc ThietBiServices)
        baoTriServices = new BaoTriServices(); // Hoặc thietBiServices = new ThietBiServices();
        AdminServices.idAdmin = 1;

        try (var stmt = connection.createStatement()) {
            // Xóa các bảng nếu đã tồn tại
            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuathietbi");
            stmt.executeUpdate("DROP TABLE IF EXISTS baotri");
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
                    + "(1, 'Đã thanh lý'), "
                    + "(2, 'Đang hoạt động'), "
                    + "(3, 'Đang sửa'), "
                    + "(4, 'Hỏng hóc'), "
                    + "(5, 'Bảo trì')"
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
            // Tạo bảng baotri
            stmt.executeUpdate(
                    "CREATE TABLE baotri ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "ngayLapLich DATETIME NOT NULL, "
                    + "ngayBaoTri DATETIME NOT NULL, "
                    + "sentEmail BOOLEAN DEFAULT FALSE, "
                    + "idThietBi INT NOT NULL, "
                    + "idNhanVien INT NOT NULL, "
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
    void testGetBaoTri_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<BaoTri> result = baoTriServices.getBaoTri();

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 lịch bảo trì");
        BaoTri baoTri = result.get(0);
        assertEquals(1, baoTri.getId());
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), baoTri.getNgayLapLich());
        assertEquals(LocalDateTime.of(2023, 10, 2, 10, 0), baoTri.getNgayBaoTri());
        assertEquals("Laptop", baoTri.getTenThietBi());
        assertEquals("Lê Hữu Hậu", baoTri.getTenNV());
    }

    @Test
    void testGetBaoTri_Empty() throws SQLException {
        // Act
        List<BaoTri> result = baoTriServices.getBaoTri();

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có lịch bảo trì");
    }

    @Test
    void testListEquipmentForMaintenance_Success() throws SQLException {
        // Arrange
        LocalDate today = LocalDate.now();
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(today.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<Integer> result = baoTriServices.listEquipmentForMaintenance();

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 thiết bị");
        assertEquals(1, result.get(0), "ID thiết bị phải là 1");
    }

    @Test
    void testListEquipmentForMaintenance_NoMatches() throws SQLException {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(tomorrow.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<Integer> result = baoTriServices.listEquipmentForMaintenance();

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có lịch bảo trì hôm nay");
    }

    @Test
    void testGetListMaintenanceCompleted_NoMatches() throws SQLException {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE thietbi SET idTrangThai = ? WHERE id = ?")) {
            stmt.setInt(1, 5); // Bảo trì
            stmt.setInt(2, 1);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(tomorrow.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<Integer> result = baoTriServices.getListMaintenanceCompleted();

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bảo trì hoàn thành");
    }

    @Test
    void testGetListMaintenanceToSendEmail_Success() throws SQLException {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(tomorrow.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<BaoTri> result = baoTriServices.getListMaintenanceToSendEmail();

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 lịch bảo trì");
        BaoTri baoTri = result.get(0);
        assertEquals(1, baoTri.getId());
        assertEquals(tomorrow.atStartOfDay(), baoTri.getNgayBaoTri());
        assertEquals(1, baoTri.getIdThietBi());
        assertEquals(1, baoTri.getIdNhanVien());
    }

    @Test
    void testGetListMaintenanceToSendEmail_AlreadySent() throws SQLException {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(tomorrow.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.setBoolean(5, true);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<BaoTri> result = baoTriServices.getListMaintenanceToSendEmail();

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi email đã được gửi");
    }

    @Test
    void testUpdateFieldSentEmailStatus_Success() throws SQLException {
        // Arrange
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        }

        baoTriServices.updateFieldSentEmailStatus(1);

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
            connection.setAutoCommit(false);
            JdbcUtils.setConnection(connection);
        }

        try (PreparedStatement stmt = connection.prepareStatement("SELECT sentEmail FROM baotri WHERE id = ?")) {
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                assertTrue(rs.getBoolean("sentEmail"), "Trạng thái sentEmail phải là TRUE");
            }
        }
    }

    @Test
    void testGetLocalDate_Success() throws SQLException {
        // Arrange
        LocalDate date1 = LocalDate.of(2023, 10, 1);
        LocalDate date2 = LocalDate.of(2023, 10, 2);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(date1.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(date2.atStartOfDay()));
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<LocalDate> result = baoTriServices.getLocalDate(1);

        // Assert
        assertEquals(2, result.size(), "Phải trả về 2 ngày bảo trì");
        assertEquals(date1, result.get(0));
        assertEquals(date2, result.get(1));
    }

    @Test
    void testGetLocalDate_NoDates() throws SQLException {
        // Act
        List<LocalDate> result = baoTriServices.getLocalDate(1);

        // Assert
        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có ngày bảo trì");
    }

    @Test
    void testAddMaintenanceSchedule() throws SQLException {
        // Arrange
        try (Connection conn = JdbcUtils.getConn()) {
            LocalDateTime ngayLapLich = LocalDateTime.of(2023, 10, 1, 10, 0);
            LocalDateTime ngayBaoTri = LocalDateTime.of(2023, 10, 2, 10, 0);
            int idThietBi = 1;
            int idNhanVien = 1;

            baoTriServices.addMaintenanceSchedule(conn, ngayLapLich, ngayBaoTri, idThietBi, idNhanVien);

            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM baotri WHERE idThietBi = ?")) {
                stmt.setInt(1, idThietBi);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);

                assertEquals(1, count, "Lịch bảo trì không được thêm vào cơ sở dữ liệu.");
            }
        }
    }

    @Test
    void testValidateAddMaintenanceSchedule_NullInputs() throws SQLException {
        // Kiểm tra với dữ liệu đầu vào null
        assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.validateAddMaintenanceSchedule(null, null, 0, 0);
        }, "Vui lòng điền đầy đủ thông tin");
    }

    @Test
    void testValidateAddMaintenanceSchedule_ExceedMaxMaintenanceCount() throws SQLException {
        // Cài đặt dữ liệu giả lập với số lần bảo trì vượt quá 2
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();

            // Thêm lần bảo trì thứ hai
            stmt.setTimestamp(1, Timestamp.valueOf("2023-10-02 12:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf("2023-10-03 10:00:00"));
            stmt.executeUpdate();
        }

        // Test khi vượt quá số lần bảo trì
        assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.validateAddMaintenanceSchedule(
                    LocalDateTime.of(2023, 10, 1, 10, 0),
                    LocalDateTime.of(2023, 10, 2, 10, 0),
                    1,
                    1
            );
        }, "Đủ số lần bảo trì! Một thiết bị chỉ được bảo trì 2 lần");
    }

    @Test
    void testValidateAddMaintenanceSchedule_InvalidMaintenanceDate() throws SQLException {
        // Cài đặt dữ liệu giả lập với ngày bảo trì không hợp lệ
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf("2023-01-01 10:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf("2023-01-02 10:00:00"));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        }

        // Test khi ngày bảo trì không trong khoảng 3 đến 6 tháng kể từ ngày nhập
        assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.validateAddMaintenanceSchedule(
                    LocalDateTime.of(2023, 6, 1, 10, 0),
                    LocalDateTime.of(2023, 9, 1, 10, 0),
                    1,
                    1
            );
        }, "Ngày bảo trì phải trong khoảng 3 đến 6 tháng kể từ ngày nhập");
    }

    @Test
    void testValidateAddMaintenanceSchedule_ExceedEmployeeWorkload() throws SQLException {
        // Giả lập OverWorkload trả về giá trị > 3
        assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.validateAddMaintenanceSchedule(
                    LocalDateTime.of(2023, 10, 1, 10, 0),
                    LocalDateTime.of(2023, 10, 2, 10, 0),
                    1,
                    1
            );
        }, "Nhân viên chỉ được làm tối đa 3 công việc trong 1 ngày");
    }

    @Test
    void testValidateAddMaintenanceSchedule_TimeConflict() throws SQLException {
        // Giả lập checkTimeConflict trả về > 0 (trùng giờ làm việc)
        assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.validateAddMaintenanceSchedule(
                    LocalDateTime.of(2023, 10, 1, 10, 0),
                    LocalDateTime.of(2023, 10, 2, 10, 0),
                    1,
                    1
            );
        }, "Nhân viên đã có lịch trùng giờ tại thời điểm này");
    }

    @Test
    void testGetMaintenanceTimes_Success() throws SQLException {
        // Cài đặt dữ liệu giả lập
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.setInt(3, 1);  // Thiết bị có id = 1
            stmt.setInt(4, 1);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();

            // Thêm bản ghi thứ 2 cho thiết bị có id = 1
            stmt.setTimestamp(1, Timestamp.valueOf("2023-10-02 10:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf("2023-10-03 10:00:00"));
            stmt.executeUpdate();
        }

        // Gọi phương thức getMaintenanceTimes và kiểm tra kết quả
        int maintenanceTimes = baoTriServices.getMaintenanceTimes(1);
        assertEquals(2, maintenanceTimes, "Số lần bảo trì cho thiết bị có id 1 phải là 2");
    }

    @Test
    void testGetMaintenanceTimes_EmptyDatabase() throws SQLException {
        // Test khi cơ sở dữ liệu không có bản ghi nào
        int maintenanceTimes = baoTriServices.getMaintenanceTimes(1);
        assertEquals(0, maintenanceTimes, "Số lần bảo trì cho thiết bị có id 1 phải là 0 khi không có bản ghi nào");
    }

    @Test
    void testGetMaintenanceDate_Success() throws SQLException {
        // Insert bản ghi cho thiết bị có id = 1
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf("2023-10-02 10:00:00")); // Có giây
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        }

        // Gọi method
        LocalDateTime maintenanceDate = baoTriServices.getMaintenanceDate(1);

        // So sánh bằng LocalDateTime trực tiếp
        assertEquals(LocalDateTime.of(2023, 10, 2, 10, 0, 0), maintenanceDate, "Ngày bảo trì không chính xác");
    }

    @Test
    void testCheckTimeConflict_ReturnsCorrectCount() throws SQLException {
        // Arrange
        LocalDateTime ngayBaoTri = LocalDateTime.of(2023, 10, 2, 10, 0);
        int idNhanVien = 1;

        // Chèn bản ghi vào bảng baotri
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(2, Timestamp.valueOf(ngayBaoTri));
            stmt.setInt(3, 1);
            stmt.setInt(4, idNhanVien);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        }

        // Chèn bản ghi vào bảng nhanviensuathietbi
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (idNhanVien, idThietBi, ngaySua) VALUES (?, ?, ?)")) {
            stmt.setInt(1, idNhanVien);
            stmt.setInt(2, 1);
            stmt.setTimestamp(3, Timestamp.valueOf(ngayBaoTri));
            stmt.executeUpdate();
        }

        // Act
        int conflictCount = baoTriServices.checkTimeConflict(idNhanVien, ngayBaoTri, 0);

        // Assert
        assertEquals(2, conflictCount, "Số lượng xung đột thời gian phải là 2");
    }

    @Test
    void testOverWorkload_ReturnsCorrectCount() throws SQLException {
        // Arrange
        LocalDateTime testDate = LocalDateTime.of(2023, 10, 2, 10, 0);
        int idNhanVien = 1;

        // Chèn công việc vào bảng baotri
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien, sentEmail) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setTimestamp(1, Timestamp.valueOf("2023-10-01 08:00:00"));
            stmt.setTimestamp(2, Timestamp.valueOf(testDate));
            stmt.setInt(3, 1);
            stmt.setInt(4, idNhanVien);
            stmt.setBoolean(5, false);
            stmt.executeUpdate();
        }

        // Chèn công việc vào bảng nhanviensuathietbi
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO nhanviensuathietbi (idNhanVien, idThietBi, ngaySua) VALUES (?, ?, ?)")) {
            stmt.setInt(1, idNhanVien);
            stmt.setInt(2, 1);
            stmt.setTimestamp(3, Timestamp.valueOf(testDate));
            stmt.executeUpdate();
        }

        // Act
        int workload = baoTriServices.OverWorkload(idNhanVien, testDate);

        // Assert
        assertEquals(2, workload, "Nhân viên có 2 công việc trong ngày");
    }

    @Test
    void testGetScheduleDate_Success() throws SQLException {
        // Arrange - chèn một bản ghi baotri giả
        LocalDateTime expectedScheduleDate = LocalDateTime.of(2023, 10, 1, 10, 0);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (id, ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setInt(1, 1);
            stmt.setTimestamp(2, Timestamp.valueOf(expectedScheduleDate)); // ngayLapLich
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(2023, 10, 2, 10, 0))); // ngayBaoTri
            stmt.setInt(4, 1); // idThietBi
            stmt.setInt(5, 1); // idNhanVien
            stmt.executeUpdate();
        }

        // Act
        LocalDateTime actualScheduleDate = baoTriServices.getScheduleDate(1);

        // Assert
        assertNotNull(actualScheduleDate, "Không được trả về null");
        assertEquals(expectedScheduleDate, actualScheduleDate, "Ngày lập lịch không khớp");
    }

   @Test
    public void testCheckLastTwoDaysUpdate_nullNgayBaoTri() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.checkLastTwoDaysUpdate(null);
        });
        assertEquals("Vui lòng nhập ngày bảo trì", exception.getMessage());
    }

    @Test
    public void testCheckLastTwoDaysUpdate_afterNgayBaoTri() {
        LocalDate ngayBaoTri = LocalDate.now().minusDays(1); // Ngày bảo trì đã qua
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.checkLastTwoDaysUpdate(ngayBaoTri);
        });
        assertEquals("Không thể cập nhật vì đã qua ngày bảo trì", exception.getMessage());
    }

    @Test
    public void testCheckLastTwoDaysUpdate_inLastTwoDaysBeforeNgayBaoTri() {
        LocalDate ngayBaoTri = LocalDate.now().plusDays(1); // Ngày bảo trì là ngày mai
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            baoTriServices.checkLastTwoDaysUpdate(ngayBaoTri);
        });
        assertEquals("Không được cập nhật lịch trong 2 ngày cuối", exception.getMessage());
    }

   
   
}
