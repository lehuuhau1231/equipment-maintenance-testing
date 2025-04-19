//
//import com.qhuong.pojo.JdbcUtils;
//import com.qhuong.pojo.NhanVienSuaThietBi;
//import com.qhuong.services.NhanVienSuaThietBiServices;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import com.qhuong.services.AdminServices;
//import java.sql.Timestamp;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.AfterEach;
//
//import static org.junit.jupiter.api.Assertions.*;
//import org.mockito.Mockito;
//
//public class TestNhanVienSuaThietBiServices {
//
//    private static NhanVienSuaThietBiServices services;
//    private static Connection connection;
//
//    @BeforeEach
//    void setUpDatabase() throws SQLException {
//        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
//        JdbcUtils.setConnection(connection);
//
//        services = new NhanVienSuaThietBiServices();
//        AdminServices.idAdmin = 1;
//
//        try (var stmt = connection.createStatement()) {
//            // Xóa các bảng nếu đã tồn tại
//            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuathietbi");
//            stmt.executeUpdate("DROP TABLE IF EXISTS baotri");
//            stmt.executeUpdate("DROP TABLE IF EXISTS thietbi");
//            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuachua");
//            stmt.executeUpdate("DROP TABLE IF EXISTS admin");
//            stmt.executeUpdate("DROP TABLE IF EXISTS trangthai");
//
//            // Tạo bảng trangthai
//            stmt.executeUpdate(
//                    "CREATE TABLE trangthai ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "tenTrangThai VARCHAR(50) NOT NULL)"
//            );
//            stmt.executeUpdate(
//                    "INSERT INTO trangthai (id, tenTrangThai) VALUES "
//                    + "(2, 'Đang hoạt động')"
//            );
//
//            // Tạo bảng admin
//            stmt.executeUpdate(
//                    "CREATE TABLE admin ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "username VARCHAR(50) NOT NULL, "
//                    + "password VARCHAR(100) NOT NULL, "
//                    + "ho VARCHAR(20), "
//                    + "ten VARCHAR(20), "
//                    + "email VARCHAR(50), "
//                    + "UNIQUE (username), "
//                    + "UNIQUE (email))"
//            );
//            stmt.executeUpdate(
//                    "INSERT INTO admin (id, username, password, ho, ten, email) VALUES "
//                    + "(1, 'admin', 'Lehuuhau1231@', 'Lê', 'Hậu', 'lehuuhau1231@gmail.com')"
//            );
//
//            // Tạo bảng thietbi
//            stmt.executeUpdate(
//                    "CREATE TABLE thietbi ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "tenThietBi VARCHAR(50) NOT NULL, "
//                    + "thanhLy DATE, "
//                    + "ngayNhap DATE NOT NULL, "
//                    + "thongBao VARCHAR(50), "
//                    + "idTrangThai INT NOT NULL, "
//                    + "idadmin INT NOT NULL, "
//                    + "FOREIGN KEY (idTrangThai) REFERENCES trangthai(id), "
//                    + "FOREIGN KEY (idadmin) REFERENCES admin(id))"
//            );
//            stmt.executeUpdate(
//                    "INSERT INTO thietbi (tenThietBi, ngayNhap, idTrangThai, idadmin) VALUES "
//                    + "('Laptop', '2025-1-01', 2, 1)"
//            );
//            // Tạo bảng nhanviensuachua
//            stmt.executeUpdate(
//                    "CREATE TABLE nhanviensuachua ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "tenNV VARCHAR(50) NOT NULL, "
//                    + "ngaySinh DATE NOT NULL, "
//                    + "CCCD CHAR(12) NOT NULL, "
//                    + "soDT CHAR(10) NOT NULL, "
//                    + "diaChi VARCHAR(250), "
//                    + "email VARCHAR(50) NOT NULL, "
//                    + "idadmin INT NOT NULL, "
//                    + "UNIQUE (email), "
//                    + "UNIQUE (CCCD), "
//                    + "UNIQUE (soDT), "
//                    + "FOREIGN KEY (idadmin) REFERENCES admin(id))"
//            );
//            stmt.executeUpdate(
//                    "INSERT INTO nhanviensuachua (tenNV, ngaySinh, CCCD, soDT, email, idadmin) VALUES "
//                    + "('Lê Hữu Hậu', '2004-01-01', '123456789012', '0123456789', 'lehuuhau1231@gmail.com', 1)"
//            );
//
//            // Tạo bảng nhanviensuathietbi
//            stmt.executeUpdate(
//                    "CREATE TABLE nhanviensuathietbi ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "ngaySua DATETIME NOT NULL, "
//                    + "idThietBi INT NOT NULL, "
//                    + "idNhanVien INT NOT NULL, "
//                    + "chiPhi BIGINT, "
//                    + "moTa VARCHAR(250), "
//                    + "FOREIGN KEY (idThietBi) REFERENCES thietbi(id), "
//                    + "FOREIGN KEY (idNhanVien) REFERENCES nhanviensuachua(id))"
//            );
//
//            stmt.executeUpdate(
//                    "CREATE TABLE baotri ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "ngayLapLich DATETIME DEFAULT CURRENT_TIMESTAMP, "
//                    + "ngayBaoTri DATETIME NOT NULL, "
//                    + "idThietBi INT NOT NULL, "
//                    + "idNhanVien INT NOT NULL, "
//                    + "FOREIGN KEY (idThietBi) REFERENCES thietbi(id), "
//                    + "FOREIGN KEY (idNhanVien) REFERENCES nhanviensuachua(id))"
//            );
//        }
//    }
//
//    @AfterEach
//    void tearDown() throws SQLException {
//        connection.close();
//    }
//
//    @Test
//    void testGetNhanVienSuaThietBi_FullData_Success() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 500000);
//            stmt.setString(5, "Thay pin");
//            stmt.executeUpdate();
//        }
//
//        // Act
//        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(true);
//
//        // Assert
//        assertEquals(1, result.size(), "Phải trả về 1 bản ghi sửa chữa");
//        NhanVienSuaThietBi repair = result.get(0);
//        assertEquals(1, repair.getId());
//        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), repair.getNgaySua());
//        assertEquals("Laptop", repair.getTenThietBi());
//        assertEquals("Lê Hữu Hậu", repair.getTenNV());
//        assertEquals(500000, repair.getChiPhi());
//        assertEquals("Thay pin", repair.getMoTa());
//    }
//
//    @Test
//    void testGetNhanVienSuaThietBi_FullData_NoMatches() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 0);
//            stmt.setNull(5, java.sql.Types.VARCHAR);
//            stmt.executeUpdate();
//        }
//
//        // Act
//        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(true);
//
//        // Assert
//        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bản ghi hợp lệ");
//    }
//
//    @Test
//    void testGetNhanVienSuaThietBi_NotFullData_Success() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2025-01-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 0);
//            stmt.setNull(5, java.sql.Types.VARCHAR);
//            stmt.executeUpdate();
//        }
//
//        // Act
//        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(false);
//
//        // Assert
//        assertEquals(1, result.size(), "Phải trả về 1 bản ghi sửa chữa");
//        NhanVienSuaThietBi repair = result.get(0);
//        assertEquals(1, repair.getId());
//        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), repair.getNgaySua());
//        assertEquals("Laptop", repair.getTenThietBi());
//        assertEquals("Lê Hữu Hậu", repair.getTenNV());
//    }
//
//    @Test
//    void testGetNhanVienSuaThietBi_NotFullData_NoMatches() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-10-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 500000);
//            stmt.setString(5, "Thay pin");
//            stmt.executeUpdate();
//        }
//
//        // Act
//        List<NhanVienSuaThietBi> result = services.getNhanVienSuaThietBi(false);
//
//        // Assert
//        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bản ghi hợp lệ");
//    }
//
//    @Test
//    void testCheckTimeConflict_NoConflicts() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 1, 10, 0);
//        int idNhanVien = 1;
//
//        // Act
//        int result = services.checkTimeConflict(idNhanVien, ngaySua);
//
//        // Assert
//        assertEquals(0, result, "Phải trả về 0 khi không có xung đột thời gian");
//    }
//
//    @Test
//    void testCheckTimeConflict_BaoTriConflict() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2023, 10, 1, 10, 0);
//        int idNhanVien = 1;
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO baotri (ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?)")) {
//            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, idNhanVien);
//            stmt.executeUpdate();
//            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
//            stmt.executeUpdate();
//        }
//
//        // Act
//        int result = services.checkTimeConflict(idNhanVien, ngaySua);
//
//        // Assert
//        assertEquals(2, result, "Phải trả về 2 xung đột từ bảng baotri");
//    }
//
//    @Test
//    void testCheckTimeConflict_NhanVienSuaThietBiConflict() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 1, 10, 0);
//        int idNhanVien = 1;
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, idNhanVien);
//            stmt.setNull(4, java.sql.Types.BIGINT);
//            stmt.executeUpdate();
//        }
//
//        // Act
//        int result = services.checkTimeConflict(idNhanVien, ngaySua);
//
//        // Assert
//        assertEquals(1, result, "Phải trả về 1 xung đột từ bảng nhanviensuathietbi");
//    }
//
//    @Test
//    void testCheckTimeConflict_BothTablesConflict() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 1, 10, 0);
//        int idNhanVien = 1;
//        try (PreparedStatement stmtBaoTri = connection.prepareStatement(
//                "INSERT INTO baotri (ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?)")) {
//            stmtBaoTri.setTimestamp(1, Timestamp.valueOf(ngaySua));
//            stmtBaoTri.setInt(2, 1);
//            stmtBaoTri.setInt(3, idNhanVien);
//            stmtBaoTri.executeUpdate();
//        }
//        try (PreparedStatement stmtSua = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
//            stmtSua.setTimestamp(1, Timestamp.valueOf(ngaySua));
//            stmtSua.setInt(2, 1);
//            stmtSua.setInt(3, idNhanVien);
//            stmtSua.setNull(4, java.sql.Types.BIGINT);
//            stmtSua.executeUpdate();
//        }
//
//        // Act
//        int result = services.checkTimeConflict(idNhanVien, ngaySua);
//
//        // Assert
//        assertEquals(2, result, "Phải trả về 2 xung đột từ cả hai bảng");
//    }
//
//    @Test
//    void testCheckTimeConflict_DifferentTimeNoConflict() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 1, 10, 0);
//        LocalDateTime conflictTime = LocalDateTime.of(2025, 1, 1, 11, 0);
//        int idNhanVien = 1;
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO baotri (ngayBaoTri, idThietBi, idNhanVien) VALUES (?, ?, ?)")) {
//            stmt.setTimestamp(1, Timestamp.valueOf(conflictTime));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, idNhanVien);
//            stmt.executeUpdate();
//        }
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, Timestamp.valueOf(conflictTime));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, idNhanVien);
//            stmt.setNull(4, java.sql.Types.BIGINT);
//            stmt.executeUpdate();
//        }
//
//        // Act
//        int result = services.checkTimeConflict(idNhanVien, ngaySua);
//
//        // Assert
//        assertEquals(0, result, "Phải trả về 0 khi thời gian không trùng");
//    }
//
//    @Test
//    void testGetListNotRepair_Success() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2025-1-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 0);
//            stmt.executeUpdate();
//        }
//
//        // Act
//        List<NhanVienSuaThietBi> result = services.getListNotRepair();
//
//        // Assert
//        assertEquals(1, result.size(), "Phải trả về 1 bản ghi chưa sửa");
//        NhanVienSuaThietBi repair = result.get(0);
//        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), repair.getNgaySua());
//        assertEquals(1, repair.getIdThietBi());
//        assertEquals(1, repair.getIdNhanVien());
//    }
//
//    @Test
//    void testGetListNotRepair_NoMatches() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi) VALUES (?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2025-1-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 500000);
//            stmt.executeUpdate();
//        }
//
//        // Act
//        List<NhanVienSuaThietBi> result = services.getListNotRepair();
//
//        // Assert
//        assertTrue(result.isEmpty(), "Phải trả về danh sách rỗng khi không có bản ghi chưa sửa");
//    }
//
//    @Test
//    void testRepairScheduleTimes_Success() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2025-1-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setNull(4, java.sql.Types.BIGINT);
//            stmt.setNull(5, java.sql.Types.VARCHAR);
//            stmt.executeUpdate();
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2023-1-02 10:00:00"));
//            stmt.executeUpdate();
//        }
//
//        // Act
//        int result = services.repairScheduleTimes(1);
//
//        // Assert
//        assertEquals(2, result, "Phải trả về 2 lần lên lịch sửa chữa");
//    }
//
//    @Test
//    void testRepairScheduleTimes_NoMatches() throws SQLException {
//        // Arrange
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, java.sql.Timestamp.valueOf("2025-1-01 10:00:00"));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setLong(4, 500000);
//            stmt.setString(5, "Thay pin");
//            stmt.executeUpdate();
//        }
//
//        // Act
//        int result = services.repairScheduleTimes(1);
//
//        // Assert
//        assertEquals(0, result, "Phải trả về 0 khi không có lịch sửa chữa");
//    }
//
//    @Test
//    void testAddRepairSchedule_Success() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 1, 10, 0);
//
//        // Act
//        services.addRepairSchedule(ngaySua, 1, 1);
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
//            JdbcUtils.setConnection(connection);
//        }
//        // Assert
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "SELECT ngaySua, idThietBi, idNhanVien FROM nhanviensuathietbi WHERE id = 1")) {
//            ResultSet rs = stmt.executeQuery();
//            assertTrue(rs.next(), "Phải có bản ghi lịch sửa chữa");
//            assertEquals(Timestamp.valueOf(ngaySua), rs.getTimestamp("ngaySua"));
//            assertEquals(1, rs.getInt("idThietBi"));
//            assertEquals(1, rs.getInt("idNhanVien"));
//        }
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_Success() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.now();
//
//        // Act & Assert
//        assertDoesNotThrow(() -> services.validateAddRepairSchedule(ngaySua, 1, 1),
//                "Không nên ném ngoại lệ khi dữ liệu hợp lệ");
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_NullNgaySua() {
//        // Arrange
//        LocalDateTime ngaySua = null;
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
//                "Phải ném ngoại lệ khi ngaySua là null");
//        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_InvalidIdThietBi() {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 2, 10, 0);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateAddRepairSchedule(ngaySua, 0, 1),
//                "Phải ném ngoại lệ khi idThietBi <= 0");
//        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_InvalidIdNhanVien() {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 2, 10, 0);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateAddRepairSchedule(ngaySua, 1, 0),
//                "Phải ném ngoại lệ khi idNhanVien <= 0");
//        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_ExistingSchedule() throws SQLException {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 2, 10, 0);
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) VALUES (?, ?, ?, ?, ?)")) {
//            stmt.setTimestamp(1, Timestamp.valueOf(ngaySua));
//            stmt.setInt(2, 1);
//            stmt.setInt(3, 1);
//            stmt.setNull(4, java.sql.Types.BIGINT);
//            stmt.setNull(5, java.sql.Types.VARCHAR);
//            stmt.executeUpdate();
//        }
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
//                "Phải ném ngoại lệ khi thiết bị đã có lịch sửa chữa");
//        assertEquals("Trong một thời điểm thiết bị chỉ được lập lịch sửa 1 lần", exception.getMessage());
//    }
//
//    @Test
//    public void testValidateAddRepairSchedule_TimeConflict_ThrowsException() throws SQLException {
//        // Tạo mock của class
//        NhanVienSuaThietBiServices servicesMock = Mockito.spy(NhanVienSuaThietBiServices.class);
//
//        // Tạo thời gian hợp lệ (trong khoảng 3 ngày từ hiện tại)
//        LocalDateTime now = LocalDateTime.now().plusDays(1);
//
//        // Mock các hàm phụ để tránh ảnh hưởng
//        Mockito.doReturn(0).when(servicesMock).repairScheduleTimes(Mockito.anyInt());
//        Mockito.doReturn(1).when(servicesMock).checkTimeConflict(Mockito.anyInt(), Mockito.any());
//
//        // Kiểm tra xem có ném đúng exception không
//        assertThrows(IllegalArgumentException.class, () -> {
//            servicesMock.validateAddRepairSchedule(now, 1, 1);
//        });
//    }
//
//    @Test
//    public void testValidateAddRepairSchedule_OverWorkload() throws SQLException {
//        NhanVienSuaThietBiServices servicesMock = Mockito.spy(NhanVienSuaThietBiServices.class);
//
//        LocalDateTime now = LocalDateTime.now().plusDays(1);
//
//        // Mock các hàm phụ để không ảnh hưởng logic khác
//        Mockito.doReturn(0).when(servicesMock).repairScheduleTimes(Mockito.anyInt());
//        Mockito.doReturn(0).when(servicesMock).checkTimeConflict(Mockito.anyInt(), Mockito.any());
//        Mockito.doReturn(3).when(servicesMock).OverWorkload(Mockito.anyInt(), Mockito.any());
//
//        // Kiểm tra xem exception có được ném ra không
//        assertThrows(IllegalArgumentException.class, () -> {
//            servicesMock.validateAddRepairSchedule(now, 1, 1);
//        });
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_InvalidDate_BeforeRange() {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 9, 29, 10, 0);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
//                "Phải ném ngoại lệ khi ngày sửa ngoài khoảng 0-3 ngày");
//        assertEquals("Ngày sửa phải nằm trong 3 ngày kể từ ngày hiện tại", exception.getMessage());
//    }
//
//    @Test
//    void testValidateAddRepairSchedule_InvalidDate_AfterRange() {
//        // Arrange
//        LocalDateTime ngaySua = LocalDateTime.of(2025, 1, 5, 10, 0);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateAddRepairSchedule(ngaySua, 1, 1),
//                "Phải ném ngoại lệ khi ngày sửa ngoài khoảng 0-3 ngày");
//        assertEquals("Ngày sửa phải nằm trong 3 ngày kể từ ngày hiện tại", exception.getMessage());
//    }
//    
//    @Test
//    void testCheckIdEquipment_Exists() throws SQLException {
//        connection.createStatement().execute(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) " +
//                        "VALUES ('2025-04-17 10:00:00', 1, 1, NULL, NULL)");
//        // Act
//        boolean result = services.checkIdEquipment(1);
//
//        // Assert
//        assertTrue(result);
//    }
//
//    @Test
//    void testCheckIdEquipment_NotExists() throws SQLException {
//        // Act
//        boolean result = services.checkIdEquipment(999);
//
//        // Assert
//        assertFalse(result);
//    }
//
//    @Test
//    void testUpdateReceipt_Success() throws SQLException {
//        connection.createStatement().execute(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) " +
//                        "VALUES ('2025-04-17 10:00:00', 1, 1, NULL, NULL)");
//        // Act
//        services.updateReceipt(1, 100000, "Sửa máy in nâng cấp");
//        if (connection == null || connection.isClosed()) {
//            connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
//            JdbcUtils.setConnection(connection);
//        }
//        // Assert: Kiểm tra dữ liệu đã được cập nhật
//        try (var stmt = connection.prepareStatement("SELECT chiPhi, moTa FROM nhanviensuathietbi WHERE id = ?")) {
//            stmt.setInt(1, 1);
//            var rs = stmt.executeQuery();
//            assertTrue(rs.next());
//            assertEquals(100000, rs.getLong("chiPhi"));
//            assertEquals("Sửa máy in nâng cấp", rs.getString("moTa"));
//        }
//    }
//
//    @Test
//    void testValidateUpdateReceipt_ValidInput() {
//        // Act & Assert
//        assertDoesNotThrow(() -> services.validateUpdateReceipt(1, "50000", "Sửa máy in"));
//    }
//
//    @Test
//    void testValidateUpdateReceipt_EmptyChiPhi() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "", "Sửa máy in"));
//        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
//    }
//
//    @Test
//    void testValidateUpdateReceipt_EmptyMoTa() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "50000", ""));
//        assertEquals("Vui lòng điền đầy đủ thông tin", exception.getMessage());
//    }
//
//    @Test
//    void testValidateUpdateReceipt_MoTaTooLong() {
//        // Arrange
//        String longMoTa = "a".repeat(251);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "50000", longMoTa));
//        assertEquals("Mô tả tối đa 250 ký tự", exception.getMessage());
//    }
//
//    @Test
//    void testValidateUpdateReceipt_MoTaSpecialCharacters() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "50000", "Sửa máy @in"));
//        assertEquals("Mô tả không được chứa ký tự đặc biệt", exception.getMessage());
//    }
//
//    @Test
//    void testValidateUpdateReceipt_InvalidChiPhi() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "abc", "Sửa máy in"));
//        assertEquals("Chi phí chỉ được nhập số nguyên dương", exception.getMessage());
//    }
//
//    @Test
//    void testValidateUpdateReceipt_ChiPhiTooLow() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "5000", "Sửa máy in"));
//        assertEquals("Chi phí từ 10.000 trở lên", exception.getMessage());
//    }
//
//    @Test
//    void testValidateUpdateReceipt_ChiPhiTooLarge() {
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                () -> services.validateUpdateReceipt(1, "999999999999999999999", "Sửa máy in"));
//        assertEquals("Lỗi! Số quá lớn", exception.getMessage());
//    }
//
//    @Test
//    void testGetRepairScheduleNew_Success() throws SQLException {
//        connection.createStatement().execute(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) " +
//                        "VALUES ('2025-04-17 10:00:00', 1, 1, 50000, 'Sửa máy in')");
//        // Act
//        NhanVienSuaThietBi result = services.getRepairScheduleNew();
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.getId());
//        assertEquals(LocalDateTime.of(2025, 4, 17, 10, 0), result.getNgaySua());
//        assertEquals("Laptop", result.getTenThietBi());
//        assertEquals("Lê Hữu Hậu", result.getTenNV());
//        assertEquals(50000, result.getChiPhi());
//        assertEquals("Sửa máy in", result.getMoTa());
//    }
//
//    @Test
//    void testGetRepairScheduleNew_NullMoTa() throws SQLException {
//        // Arrange: Cập nhật bản ghi để moTa là null
//        connection.createStatement().execute(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) " +
//                        "VALUES ('2025-04-17 10:00:00', 1, 1, 100000, NULL)");
//
//        // Act
//        NhanVienSuaThietBi result = services.getRepairScheduleNew();
//
//        // Assert
//        assertNull(result);
//    }
//
//    @Test
//    void testGetRepairScheduleNew_NullChiPhi() throws SQLException {
//        // Arrange: Cập nhật bản ghi để chiPhi là 0
//        connection.createStatement().execute(
//                "INSERT INTO nhanviensuathietbi (ngaySua, idThietBi, idNhanVien, chiPhi, moTa) " +
//                        "VALUES ('2025-04-17 10:00:00', 1, 1, NULL, 'Sửa máy in')");
//
//        // Act
//        NhanVienSuaThietBi result = services.getRepairScheduleNew();
//
//        // Assert
//        assertNull(result);
//    }
//}
