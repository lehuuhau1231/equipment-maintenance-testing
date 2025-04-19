//
//import com.qhuong.pojo.JdbcUtils;
//import java.sql.Connection;
//import com.qhuong.services.AdminServices;
//import java.sql.CallableStatement;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import org.junit.jupiter.api.AfterEach;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mindrot.jbcrypt.BCrypt;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//
///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
///**
// *
// * @author PC
// */
//public class TestAdminServices {
//
//    private static AdminServices adminService;
//    private static Connection conn;
//
//    @BeforeEach
//    void setUpDatabase() throws SQLException {
//        // Thiết lập kết nối H2 in-memory
//        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
//        conn.setAutoCommit(false);
//        JdbcUtils.setConnection(conn);
//
//        adminService = new AdminServices();
//        try (var stmt = conn.createStatement()) {
//            //Tạo bảng admin
//            stmt.executeUpdate("DROP TABLE IF EXISTS admin;");
//            stmt.executeUpdate(
//                    "CREATE TABLE admin ("
//                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
//                    + "username VARCHAR(50), "
//                    + "password VARCHAR(100), "
//                    + "ho VARCHAR(50), "
//                    + "ten VARCHAR(50),"
//                    + "email VARCHAR(50))"
//            );
//             
//        }
//    }
//
//    @AfterEach
//    void tearDown() throws SQLException {
//        conn.close();
//
//    }
//
//    @Test
//    void testGetAdminSuccess() throws SQLException {
//        // Insert dữ liệu mẫu
//        String username = "admin1";
//        String rawPassword = "@Password123";
//        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//
//        try (PreparedStatement stm = conn.prepareStatement(
//                "INSERT INTO admin(username, password, ho, ten, email) VALUES (?, ?, ?, ?, ?)")) {
//            stm.setString(1, username);
//            stm.setString(2, hashedPassword);
//            stm.setString(3, "Nguyen");
//            stm.setString(4, "An");
//            stm.setString(5, "an@example.com");
//            stm.executeUpdate();
//        }
//
//        // Test đăng nhập đúng
//        int result = adminService.getAdmin(username, rawPassword);
//
//        assertEquals(1, result);
//
//    }
//
//    @Test
//    void testGetAdminWrongPassword() throws SQLException {
//        // Insert dữ liệu mẫu
//        String username = "admin2";
//        String rawPassword = "@Password123";
//        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//
//        try (PreparedStatement stm = conn.prepareStatement(
//                "INSERT INTO admin(username, password, ho, ten, email) VALUES (?, ?, ?, ?, ?)")) {
//            stm.setString(1, username);
//            stm.setString(2, hashedPassword);
//            stm.setString(3, "Tran");
//            stm.setString(4, "Binh");
//            stm.setString(5, "binh@example.com");
//            stm.executeUpdate();
//        }
//
//        // Test nhập sai mật khẩu
//        int result = adminService.getAdmin(username, "wrongpassword");
//
//        assertEquals(0, result);
//    }
//
//    @Test
//    void testGetAdminUserNotFound() throws SQLException {
//        // Test với username không tồn tại
//        int result = adminService.getAdmin("nonexistentuser", "@Password123");
//
//        assertEquals(-1, result);
//    }
//
//    @Test
//    void testIsUsernameExistTrue() throws SQLException {
//        try (PreparedStatement stm = conn.prepareStatement(
//                "INSERT INTO admin(username, password, ho, ten, email) VALUES (?, ?, ?, ?, ?)")) {
//            stm.setString(1, "adminExist");
//            stm.setString(2, BCrypt.hashpw("password123", BCrypt.gensalt()));
//            stm.setString(3, "Le");
//            stm.setString(4, "Minh");
//            stm.setString(5, "minh@example.com");
//            stm.executeUpdate();
//        }
//
//        boolean exists = adminService.isUsernameExist("adminExist");
//
//        assertTrue(exists);
//    }
//
//    @Test
//    void testIsUsernameExistFalse() throws SQLException {
//
//        boolean exists = adminService.isUsernameExist("nonexistentuser");
//
//        assertFalse(exists);
//    }
//
//    @Test
//    void testGetEmailSuccess() throws SQLException {
//        // Thêm user mẫu
//        try (PreparedStatement stm = conn.prepareStatement(
//                "INSERT INTO admin(username, password, ho, ten, email) VALUES (?, ?, ?, ?, ?)")) {
//            stm.setString(1, "adminEmail");
//            stm.setString(2, BCrypt.hashpw("password123", BCrypt.gensalt()));
//            stm.setString(3, "Pham");
//            stm.setString(4, "Duc");
//            stm.setString(5, "duc@example.com");
//            stm.executeUpdate();
//        }
//
//        // Test lấy email đúng
//        String email = adminService.getEmail("adminEmail");
//
//        assertEquals("duc@example.com", email);
//    }
//
//    @Test
//    void testGetEmailNotFound() throws SQLException {
//        String email = adminService.getEmail("unknownuser");
//
//        assertNull(email);
//    }
//
//    @Test
//    public void testAddAdmin_Success() throws SQLException {
//        // Arrange
//        String username = "newadmin";
//        String password = "newpassword";
//        String ho = "Do";
//        String ten = "Khanh";
//        String email = "khanh@example.com";
//
//        try (MockedStatic<JdbcUtils> mockedJdbc = Mockito.mockStatic(JdbcUtils.class)) {
//            mockedJdbc.when(JdbcUtils::getConn).thenReturn(conn);
//
//            // Act
//            adminService.addAdmin(conn, username, password, ho, ten, email);
//
//            // Assert
//            try (PreparedStatement stm = conn.prepareStatement("SELECT * FROM admin WHERE username = ?")) {
//                stm.setString(1, username);
//                ResultSet rs = stm.executeQuery();
//
//                assertTrue(rs.next());
//
//                assertEquals(username, rs.getString("username"));
//                assertEquals(ho, rs.getString("ho"));
//                assertEquals(ten, rs.getString("ten"));
//                assertEquals(email, rs.getString("email"));
//
//                String storedHashedPassword = rs.getString("password");
//                assertNotEquals(password, storedHashedPassword);
//                assertTrue(BCrypt.checkpw(password, storedHashedPassword));
//            }
//        }
//    }
//
//    @Test
//    public void testUpdatePassword_Success() throws SQLException {
//        // Arrange
//        String username = "testuser_" + System.currentTimeMillis();
//        String oldPassword = "OldPassword1!";
//        String newPassword = "NewPassword1!";
//
//        adminService.addAdmin(conn, username, oldPassword, "Ho", "Ten", "test@example.com");
//
//        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
//
//        // Không cần mock JdbcUtils nữa vì ta chủ động truyền conn
//        adminService.updatePassword(conn, username, newHashedPassword);
//
//        // Assert
//        try (PreparedStatement stm = conn.prepareStatement("SELECT password FROM admin WHERE username = ?")) {
//            stm.setString(1, username);
//            ResultSet rs = stm.executeQuery();
//
//            assertTrue(rs.next());
//
//            String storedPassword = rs.getString("password");
//            assertFalse(BCrypt.checkpw(oldPassword, storedPassword));
//            assertTrue(BCrypt.checkpw(newPassword, storedPassword));
//        }
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "'', password",
//        "username, ''"
//    })
//    public void testCheckLogin_MissingCredentials(String username, String password) {
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            adminService.checkLogin(username, password);
//        });
//
//        assertEquals("Vui lòng điền đủ thông tin!", exception.getMessage());
//    }
//
//    @Test
//    public void testCheckLogin_WrongPassword() throws SQLException {
//        String hashedPassword = BCrypt.hashpw("1234", BCrypt.gensalt());
//        try (PreparedStatement stm = conn.prepareStatement("INSERT INTO admin(username, password) VALUES (?, ?)")) {
//            stm.setString(1, "admin");
//            stm.setString(2, hashedPassword);
//            stm.executeUpdate();
//        }
//
//        Exception ex = assertThrows(IllegalArgumentException.class, ()
//                -> adminService.checkLogin("admin", "wrongpass"));
//        assertEquals("Sai mật khẩu", ex.getMessage());
//    }
//
//    @Test
//    public void testCheckLogin_WrongUsername() {
//        Exception ex = assertThrows(IllegalArgumentException.class, ()
//                -> adminService.checkLogin("not_exist", "1234"));
//        assertEquals("Sai tài khoản", ex.getMessage());
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//        "'', '', Vui lòng điền đủ thông tin!",
//        "'Password123!', '', Vui lòng điền đủ thông tin!",
//        "'', 'Password123!', Vui lòng điền đủ thông tin!",
//        "'Password123!', 'Mismatch123!', Mật khẩu xác nhận không khớp với mật khẩu mới!",
//        "'Pass1!', 'Pass1!', Độ dài mật khẩu ít nhất 8 ký tự",
//        "'password1!', 'password1!', Mật khẩu phải chứa ít nhất 1 ký tự HOA!",
//        "'PASSWORD1!', 'PASSWORD1!', Mật khẩu phải chứa ít nhất 1 ký tự THƯỜNG!",
//        "'Password!', 'Password!', Mật khẩu phải chứa ít nhất 1 ký tự SỐ!",
//        "'Password1', 'Password1', Mật khẩu phải chứa ít nhất 1 ký tự ĐẶC BIỆT!"
//    })
//    public void testInvalidPasswords(String newPassword, String confirmPassword, String expectedMessage) {
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, ()
//                -> adminService.validateNewPassword(newPassword, confirmPassword));
//        assertEquals(expectedMessage, ex.getMessage());
//    }
//
//}
