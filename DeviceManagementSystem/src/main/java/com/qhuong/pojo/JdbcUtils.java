/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author lehuu
 */
public class JdbcUtils {
//    static {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//        } catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//    }
//    
//    public static Connection getConn() throws SQLException {
//        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/equipmentdb", "root", "123456");
//        conn.setAutoCommit(false);
//        return conn;
//    }

    private static Connection testConnection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConn() throws SQLException {
        // Nếu đang trong môi trường test, dùng connection test
        if (testConnection != null && !testConnection.isClosed()) {
            System.out.println("Moi truong test");
            return testConnection;
        }

        // Môi trường production dùng MySQL
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/thietbi", "root", "1234");
        return conn;
    }

    public static void setConnection(Connection conn) {
        testConnection = conn;
    }
}
