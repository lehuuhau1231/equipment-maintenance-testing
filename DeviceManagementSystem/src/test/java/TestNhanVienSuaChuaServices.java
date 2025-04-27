
import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.NhanVienSuaChua;
import com.qhuong.services.AdminServices;
import com.qhuong.services.NhanVienSuaChuaServices;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author PC
 */
public class TestNhanVienSuaChuaServices {

    private static Connection conn;
    private static NhanVienSuaChuaServices nhanVien = new NhanVienSuaChuaServices();

    @BeforeEach
    void setUpDatabase() throws SQLException {
        // Thiết lập kết nối H2 in-memory
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        conn.setAutoCommit(false);
        JdbcUtils.setConnection(conn);

        nhanVien = new NhanVienSuaChuaServices();
        AdminServices.idAdmin = 1;
        try (var stmt = conn.createStatement()) {
            //Tạo bảng admin
            stmt.executeUpdate("DROP TABLE IF EXISTS nhanviensuachua;");
            stmt.executeUpdate("DROP TABLE IF EXISTS admin;");
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
                    "INSERT INTO nhanviensuachua (tenNV, ngaySinh, CCCD, soDT,diachi, email, idadmin) VALUES "
                    + "('Lê Hữu Hậu', '2004-01-01', '123456789012', '0123456789','TP.HCM', 'lehuuhau1231@gmail.com', 1)"
            );
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        conn.close();

    }

    @Test
    public void testGetNhanVien() throws SQLException {
        List<NhanVienSuaChua> list = nhanVien.getNhanVien();

        assertEquals(1, list.size());

        NhanVienSuaChua nv1 = list.get(0);
        assertEquals("Lê Hữu Hậu", nv1.getTenNV());
        assertEquals(LocalDate.of(2004, 1, 1), nv1.getNgaySinh());
        assertEquals("123456789012", nv1.getCCCD());
        assertEquals("0123456789", nv1.getSoDT());
        assertEquals("lehuuhau1231@gmail.com", nv1.getEmail());
    }

    @Test
    public void testGetIdEmployeeWithValidName() throws SQLException {
        int id = nhanVien.getIdEmployee("Lê Hữu Hậu");
        assertEquals(1, id);
    }

    @Test
    public void testGetIdEmployeeWithInvalidName() throws SQLException {
        int id = nhanVien.getIdEmployee("Nguyễn Văn Không Tồn Tại");
        assertEquals(-1, id);
    }

    @Test
    public void testGetEmailWithValidId() throws SQLException {
        // ID của nhân viên đầu tiên là 1
        String email = nhanVien.getEmail(1);
        assertEquals("lehuuhau1231@gmail.com", email);
    }

    @Test
    public void testGetEmailWithInvalidId() throws SQLException {
        String email = nhanVien.getEmail(999); // ID không tồn tại
        assertEquals(null, email);
    }

    @Test
    public void testGetEmailWithZeroId() throws SQLException {
        String email = nhanVien.getEmail(0); // ID không hợp lệ
        assertNull(email);
    }

    @Test
    void testAddEmployee_Success() throws SQLException {
        nhanVien.addEmployee(conn, // truyền conn test
                "Phạm Văn Cường",
                LocalDate.of(2000, 3, 25),
                "123123123123",
                "0901122334",
                "Nha Trang",
                "phamcuong@example.com"
        );

        conn.commit(); // nhớ commit nếu autoCommit đã false

        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM nhanviensuachua WHERE tenNV = ?")) {
            stmt.setString(1, "Phạm Văn Cường");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Phạm Văn Cường", rs.getString("tenNV"));
            assertEquals("phamcuong@example.com", rs.getString("email"));
            assertEquals("0901122334", rs.getString("soDT"));
            assertEquals("123123123123", rs.getString("CCCD"));
            assertEquals("Nha Trang", rs.getString("diaChi"));
            assertEquals(1, rs.getInt("idAdmin"));
        }
    }

    @Test
    public void testUpdateEmployeeWithConnection() throws SQLException {
        NhanVienSuaChua nv = new NhanVienSuaChua(
                1,
                "Tran Van B",
                LocalDate.of(1995, 5, 15),
                "987654321345",
                "0987654321",
                "TP HCM",
                "b@gmail.com"
        );
        nhanVien.updateEmployeeWithConnection(conn, nv);

        try (PreparedStatement stm = conn.prepareStatement("SELECT * FROM nhanviensuachua WHERE id = 1"); ResultSet rs = stm.executeQuery()) {

            assertTrue(rs.next());
            assertEquals("Tran Van B", rs.getString("tenNV"));
            assertEquals("1995-05-15", rs.getString("ngaySinh"));
            assertEquals("987654321345", rs.getString("CCCD"));
            assertEquals("0987654321", rs.getString("soDT"));
            assertEquals("TP HCM", rs.getString("diaChi"));
            assertEquals("b@gmail.com", rs.getString("email"));
            assertEquals(1, rs.getInt("idadmin"));
        }
    }

    @ParameterizedTest
    @CsvSource({
        "'',1990-01-01,123456789012,0123456789,Hanoi,a@gmail.com,Vui lòng điền đầy đủ thông tin!",
        "Tên@Lỗi,1990-01-01,123456789012,0123456789,Hanoi,a@gmail.com,Tên nhân viên không được chứa ký tự đặc biệt",
        "Nguyen Van A,1990-01-01,1234567890,0123456789,Hanoi,a@gmail.com,CCCD 12 số",
        "Nguyen Van A,1990-01-01,123456789012,01234abcd9,Hanoi,a@gmail.com,Số điện thoại chỉ được chứa ký tự số",
        "Nguyen Van A,2010-01-01,123456789012,0123456789,Hanoi,a@gmail.com,Nhân viên không đủ 18 tuổi",
        "Nguyen Van A,1990-01-01,123456789012,0123456,Hanoi,a@gmail.com,Số điện thoại 10 số",
        "Nguyen Van A,1990-01-01,123456789012,0123456789,Hanoi,invalid-email,Email không hợp lệ. Vui lòng nhập lại email với định dạng hợp lệ (ví dụ: example@domain.com)."
    })
    public void testInvalidEmployeeInput(
            String tenNV,
            String ngaySinhStr,
            String cccd,
            String soDT,
            String diaChi,
            String email,
            String expectedMessage
    ) {
        LocalDate ngaySinh = ngaySinhStr == null || ngaySinhStr.isEmpty()
                ? null
                : LocalDate.parse(ngaySinhStr);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> nhanVien.validateEmployeeInput(tenNV, ngaySinh, cccd, soDT, diaChi, email));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
        "'Nguyen Van A', false",
        "'Nguyen@Van', true",
        "'12345', false",
        "'', true"
    })
    void testContainsSpecialCharacters(String input, boolean expected) {
        assertEquals(expected, new NhanVienSuaChuaServices().containsSpecialCharacters(input));
    }

    @ParameterizedTest
    @CsvSource({
        "'123456', false",
        "'123abc', true",
        "'', true"
    })
    void testIsNumber(String input, boolean expected) {
        assertEquals(expected, new NhanVienSuaChuaServices().isNumber(input));
    }

}
