import com.qhuong.pojo.JdbcUtils;
import com.qhuong.services.AdminServices;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServicesTest {

    @Mock
    private Connection conn;

    @Mock
    private CallableStatement stm; // Thay PreparedStatement bằng CallableStatement

    @Mock
    private ResultSet rs;

    @InjectMocks
    private AdminServices adminServices;

    @BeforeEach
    void setUp() throws SQLException {
        when(JdbcUtils.getConn()).thenReturn(conn);
        when(conn.prepareCall(anyString())).thenReturn(stm); // Mock prepareCall trả về CallableStatement
    }

    @Test
    void testGetAdmin_Success() throws SQLException {
        String username = "admin";
        String password = "password";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(hashedPassword);
        when(rs.getInt("id")).thenReturn(1);

        int result = adminServices.getAdmin(username, password);

        assertEquals(1, result);
        assertEquals(1, AdminServices.idAdmin);
        verify(stm).setString(1, username);
    }

    @Test
    void testGetAdmin_UserNotFound() throws SQLException {
        String username = "nonexistent";
        String password = "password";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        int result = adminServices.getAdmin(username, password);

        assertEquals(-1, result);
        verify(stm).setString(1, username);
    }

    @Test
    void testGetAdmin_WrongPassword() throws SQLException {
        String username = "admin";
        String password = "wrongpassword";
        String hashedPassword = BCrypt.hashpw("correctpassword", BCrypt.gensalt());

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn(hashedPassword);

        int result = adminServices.getAdmin(username, password);

        assertEquals(0, result);
        verify(stm).setString(1, username);
    }

    @Test
    void testIsUsernameExist_True() throws SQLException {
        String username = "admin";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        boolean result = adminServices.isUsernameExist(username);

        assertTrue(result);
        verify(stm).setString(1, username);
    }

    @Test
    void testIsUsernameExist_False() throws SQLException {
        String username = "nonexistent";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        boolean result = adminServices.isUsernameExist(username);

        assertFalse(result);
        verify(stm).setString(1, username);
    }

    @Test
    void testGetEmail_Success() throws SQLException {
        String username = "admin";
        String email = "admin@example.com";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("email")).thenReturn(email);

        String result = adminServices.getEmail(username);

        assertEquals(email, result);
        verify(stm).setString(1, username);
    }

    @Test
    void testGetEmail_NotFound() throws SQLException {
        String username = "nonexistent";

        when(stm.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        String result = adminServices.getEmail(username);

        assertNull(result);
        verify(stm).setString(1, username);
    }

    @Test
    void testAddAdmin_Success() throws SQLException {
        String username = "newadmin";
        String password = "password123";
        String ho = "Nguyen";
        String ten = "Van";
        String email = "newadmin@example.com";

        when(stm.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> adminServices.addAdmin(username, password, ho, ten, email));
        verify(stm).setString(1, username);
        verify(stm).setString(3, ho);
        verify(stm).setString(4, ten);
        verify(stm).setString(5, email);
    }

    @Test
    void testUpdatePassword_Success() throws SQLException {
        String username = "admin";
        String hashedPassword = BCrypt.hashpw("newpassword", BCrypt.gensalt());

        when(stm.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> adminServices.updatePassword(username, hashedPassword));
        verify(stm).setString(1, hashedPassword);
        verify(stm).setString(2, username);
    }

    @Test
    void testCheckLogin_EmptyCredentials() {
        String username = "";
        String password = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.checkLogin(username, password));

        assertEquals("Vui lòng điền đủ thông tin!", exception.getMessage());
    }

    @Test
    void testValidateNewPassword_Success() {
        String newPassword = "Abcd1234!";
        String confirmPassword = "Abcd1234!";

        assertDoesNotThrow(() -> adminServices.validateNewPassword(newPassword, confirmPassword));
    }

    @Test
    void testValidateNewPassword_Mismatch() {
        String newPassword = "Abcd1234!";
        String confirmPassword = "Abcd1234@";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword));

        assertEquals("Mật khẩu xác nhận không khớp với mật khẩu mới!", exception.getMessage());
    }

    @Test
    void testValidateNewPassword_TooShort() {
        String newPassword = "Abcd12!";
        String confirmPassword = "Abcd12!";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword));

        assertEquals("Độ dài mật khẩu ít nhất 8 ký tự", exception.getMessage());
    }

    @Test
    void testValidateNewPassword_NoUppercase() {
        String newPassword = "abcd1234!";
        String confirmPassword = "abcd1234!";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminServices.validateNewPassword(newPassword, confirmPassword));

        assertEquals("Mật khẩu phải chứa ít nhất 1 ký tự HOA!", exception.getMessage());
    }
}