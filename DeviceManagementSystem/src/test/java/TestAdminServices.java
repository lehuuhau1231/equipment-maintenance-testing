import com.qhuong.pojo.JdbcUtils;
import com.qhuong.services.AdminServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServices Test")
class AdminServicesTest {

    @Mock
    private Connection conn;

    @Mock
    private CallableStatement stm; // Sửa từ PreparedStatement thành CallableStatement

    @Mock
    private ResultSet rs;

    @InjectMocks
    private AdminServices adminServices;

    @BeforeEach
    void setUp() throws SQLException {
        when(JdbcUtils.getConn()).thenReturn(conn);
        when(conn.prepareCall(anyString())).thenReturn(stm);
    }

    @Test
    @DisplayName("getAdmin returns 1 when credentials are correct")
    void testGetAdmin_Success() throws SQLException {
        String username = "admin";
        String password = "password";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        int adminId = 1;

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(hashedPassword);
        when(rs.getInt("id")).thenReturn(adminId);

        int result = adminServices.getAdmin(username, password);

        assertEquals(1, result, "Should return 1 for valid credentials");
        assertEquals(adminId, AdminServices.idAdmin, "idAdmin should be set to admin ID");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("getAdmin returns -1 when username does not exist")
    void testGetAdmin_UserNotFound() throws SQLException {
        String username = "nonexistent";
        String password = "password";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        int result = adminServices.getAdmin(username, password);

        assertEquals(-1, result, "Should return -1 for non-existent username");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("getAdmin returns 0 when password is incorrect")
    void testGetAdmin_WrongPassword() throws SQLException {
        String username = "admin";
        String password = "wrongpassword";
        String hashedPassword = BCrypt.hashpw("correctpassword", BCrypt.gensalt());

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(hashedPassword);

        int result = adminServices.getAdmin(username, password);

        assertEquals(0, result, "Should return 0 for incorrect password");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("isUsernameExist returns true when username exists")
    void testIsUsernameExist_True() throws SQLException {
        String username = "admin";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        boolean result = adminServices.isUsernameExist(username);

        assertTrue(result, "Should return true if username exists");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("isUsernameExist returns false when username does not exist")
    void testIsUsernameExist_False() throws SQLException {
        String username = "nonexistent";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        boolean result = adminServices.isUsernameExist(username);

        assertFalse(result, "Should return false if username does not exist");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("getEmail returns email when username exists")
    void testGetEmail_Success() throws SQLException {
        String username = "admin";
        String email = "admin@example.com";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("email")).thenReturn(email);

        String result = adminServices.getEmail(username);

        assertEquals(email, result, "Should return the correct email");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("getEmail returns null when username does not exist")
    void testGetEmail_NotFound() throws SQLException {
        String username = "nonexistent";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        String result = adminServices.getEmail(username);

        assertNull(result, "Should return null if username does not exist");
        verify(stm).setString(1, username);
        verify(stm).executeQuery();
    }

    @Test
    @DisplayName("addAdmin executes successfully")
    void testAddAdmin_Success() throws SQLException {
        String username = "newadmin";
        String password = "password123";
        String ho = "Nguyen";
        String ten = "Van";
        String email = "newadmin@example.com";

        when(stm.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> adminServices.addAdmin(username, password, ho, ten, email),
                "addAdmin should not throw an exception");
        verify(stm).setString(1, username);
        verify(stm).setString(3, ho);
        verify(stm).setString(4, ten);
        verify(stm).setString(5, email);
        verify(stm).executeUpdate();
    }

    @Test
    @DisplayName("updatePassword executes successfully")
    void testUpdatePassword_Success() throws SQLException {
        String username = "admin";
        String hashedPassword = BCrypt.hashpw("newpassword", BCrypt.gensalt());

        when(stm.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> adminServices.updatePassword(username, hashedPassword),
                "updatePassword should not throw an exception");
        verify(stm).setString(1, hashedPassword);
        verify(stm).setString(2, username);
        verify(stm).executeUpdate();
    }

    @Test
    @DisplayName("checkLogin throws exception for empty credentials")
    void testCheckLogin_EmptyCredentials() {
        String username = "";
        String password = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.checkLogin(username, password),
                "Should throw exception for empty credentials");

        assertEquals("Vui lòng điền đủ thông tin!", exception.getMessage());
    }

    @Test
    @DisplayName("checkLogin throws exception for non-existent username")
    void testCheckLogin_UserNotFound() throws SQLException {
        String username = "nonexistent";
        String password = "password";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.checkLogin(username, password),
                "Should throw exception for non-existent username");

        assertEquals("Sai tài khoản", exception.getMessage());
        verify(stm, times(2)).setString(1, username);
        verify(stm, times(2)).executeQuery();
    }

    @Test
    @DisplayName("checkLogin throws exception for incorrect password")
    void testCheckLogin_WrongPassword() throws SQLException {
        String username = "admin";
        String password = "wrongpassword";
        String hashedPassword = BCrypt.hashpw("correctpassword", BCrypt.gensalt());

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(hashedPassword);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.checkLogin(username, password),
                "Should throw exception for incorrect password");

        assertEquals("Sai mật khẩu", exception.getMessage());
        verify(stm, times(2)).setString(1, username);
        verify(stm, times(2)).executeQuery();
    }

    @Test
    @DisplayName("validateNewPassword succeeds for valid password")
    void testValidateNewPassword_Success() {
        String newPassword = "Abcd1234!";
        String confirmPassword = "Abcd1234!";

        assertDoesNotThrow(() -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should not throw exception for valid password");
    }

    @Test
    @DisplayName("validateNewPassword throws exception for mismatched passwords")
    void testValidateNewPassword_Mismatch() {
        String newPassword = "Abcd1234!";
        String confirmPassword = "Abcd1234@";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for mismatched passwords");

        assertEquals("Mật khẩu xác nhận không khớp với mật khẩu mới!", exception.getMessage());
    }

    @Test
    @DisplayName("validateNewPassword throws exception for password too short")
    void testValidateNewPassword_TooShort() {
        String newPassword = "Abcd12!";
        String confirmPassword = "Abcd12!";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for password too short");

        assertEquals("Độ dài mật khẩu ít nhất 8 ký tự", exception.getMessage());
    }

    @Test
    @DisplayName("validateNewPassword throws exception for missing uppercase")
    void testValidateNewPassword_NoUppercase() {
        String newPassword = "abcd1234!";
        String confirmPassword = "abcd1234!";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for missing uppercase");

        assertEquals("Mật khẩu phải chứa ít nhất 1 ký tự HOA!", exception.getMessage());
    }

    @Test
    @DisplayName("validateNewPassword throws exception for missing lowercase")
    void testValidateNewPassword_NoLowercase() {
        String newPassword = "ABCD1234!";
        String confirmPassword = "ABCD1234!";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for missing lowercase");

        assertEquals("Mật khẩu phải chứa ít nhất 1 ký tự THƯỜNG!", exception.getMessage());
    }

    @Test
    @DisplayName("validateNewPassword throws exception for missing digit")
    void testValidateNewPassword_NoDigit() {
        String newPassword = "AbcdEfgh!";
        String confirmPassword = "AbcdEfgh!";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for missing digit");

        assertEquals("Mật khẩu phải chứa ít nhất 1 ký tự SỐ!", exception.getMessage());
    }

    @Test
    @DisplayName("validateNewPassword throws exception for missing special character")
    void testValidateNewPassword_NoSpecialChar() {
        String newPassword = "Abcd1234";
        String confirmPassword = "Abcd1234";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for missing special character");

        assertEquals("Mật khẩu phải chứa ít nhất 1 ký tự ĐẶC BIỆT!", exception.getMessage());
    }

    @Test
    @DisplayName("validateNewPassword throws exception for null or empty password")
    void testValidateNewPassword_NullOrEmpty() {
        String newPassword = "";
        String confirmPassword = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword),
                "Should throw exception for null or empty password");

        assertEquals("Vui lòng điền đủ thông tin!", exception.getMessage());
    }
}