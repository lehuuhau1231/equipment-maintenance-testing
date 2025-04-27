
import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.TrangThai;
import com.qhuong.services.AdminServices;

import com.qhuong.services.TrangThaiServices;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author PC
 */
public class TestTrangThaiServices {

    private static Connection conn;
    private static TrangThaiServices trangThai = new TrangThaiServices();

    @BeforeEach
    void setUpDatabase() throws SQLException {
        // Thiết lập kết nối H2 in-memory
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        conn.setAutoCommit(false);
        JdbcUtils.setConnection(conn);

        trangThai = new TrangThaiServices();
        AdminServices.idAdmin = 1;
        try (var stmt = conn.createStatement()) {
            //Tạo bảng admin
            stmt.executeUpdate("DROP TABLE IF EXISTS trangthai;");
            stmt.executeUpdate(
                    "CREATE TABLE trangthai ("
                    + "id INT PRIMARY KEY, "
                    + "tenTrangThai VARCHAR(50))"
            );
            stmt.executeUpdate(
                    "INSERT INTO trangthai (id, tenTrangThai) VALUES "
                    + "(1, 'Đã thanh lý'), "
                    + "(2, 'Đang hoạt động'), "
                    + "(3, 'Hỏng hóc'), "
                    + "(4, 'Đang sửa'), "
                    + "(5, 'Bảo trì')"
            );
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        conn.close();
    }
     @Test
    public void testGetTrangThai_Full_IncludeDamage() throws Exception {
        List<TrangThai> result = trangThai.getTrangThai(true, true);
        assertNotNull(result);
        assertTrue(result.stream().noneMatch(t -> t.getId() == 3 || t.getId() == 5 || t.getId() == 2));
    }

    @Test
    public void testGetTrangThai_Full_ExcludeDamage() throws Exception {
        List<TrangThai> result = trangThai.getTrangThai(true, false);
        assertNotNull(result);
        assertTrue(result.stream().noneMatch(t -> t.getId() == 3 || t.getId() == 5));
    }

    @Test
    public void testGetTrangThai_NotFull() throws Exception {
        List<TrangThai> result = trangThai.getTrangThai(false, false); // damage param is ignored
        assertNotNull(result);
        assertTrue(result.stream().noneMatch(t -> t.getId() == 1 || t.getId() == 3 || t.getId() == 5));
    }
   @Test
    public void testGetTrangThai_DaThanhLy() throws Exception {
        String ketQua = trangThai.getTrangThaiDaThanhLy();
        assertNotNull(ketQua);
        assertEquals("Đã thanh lý", ketQua);
    }
    
     @Test
    public void layDanhSachTrangThaiDangMap_TuDB() throws Exception {
        Map<Integer, String> ketQua = trangThai.getStatusMap();

        // Kiểm tra không null và có ít nhất 1 dòng (nếu DB có dữ liệu)
        assertNotNull(ketQua);
        assertFalse(ketQua.isEmpty());

        
        assertTrue(ketQua.containsKey(1));
        assertEquals("Đã thanh lý", ketQua.get(1));
    }
    
    @Test
    public void testGetIdStatus_TonTaiTrongDB() throws Exception {
        int ketQua = trangThai.getIdStatus("Đang hoạt động");  
        assertTrue(ketQua > 0); 
    }

    @Test
    public void testGetIdStatus_KhongTonTai() throws Exception {
        int ketQua = trangThai.getIdStatus("Không tồn tại");
        assertEquals(-1, ketQua);
    }
}
