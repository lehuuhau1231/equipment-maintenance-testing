
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lehuu
 */
public class JdbcUtils {
    private static Connection testConn;

    // Set connection cho test
    public static void setConnForTest(Connection conn) {
        testConn = conn;
    }

    // Lấy connection, nếu trong test sẽ trả về connection đã set
    public static Connection getConn() throws SQLException {
        if (testConn != null)
            return testConn;
        // Nếu không trong test thì dùng connection bình thường
        return DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
    }
}
