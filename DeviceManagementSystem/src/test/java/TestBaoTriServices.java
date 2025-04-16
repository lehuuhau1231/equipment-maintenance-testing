
import com.qhuong.services.BaoTriServices;
import com.qhuong.services.AdminServices;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void testGetListMaintenanceCompleted_Success() throws SQLException {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE thietbi SET idTrangThai = ? WHERE id = ?")) {
            stmt.setInt(1, 5); // Bảo trì
            stmt.setInt(2, 1);
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO baotri (ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?, ?)")) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(yesterday.atStartOfDay()));
            stmt.setInt(3, 1);
            stmt.setInt(4, 1);
            stmt.executeUpdate();
        }
        connection.commit();

        // Act
        List<Integer> result = baoTriServices.getListMaintenanceCompleted();

        // Assert
        assertEquals(1, result.size(), "Phải trả về 1 thiết bị");
        assertEquals(1, result.get(0), "ID thiết bị phải là 1");
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
}
