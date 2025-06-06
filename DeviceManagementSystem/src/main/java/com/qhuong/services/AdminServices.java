/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author lehuu
 */
public class AdminServices {

    public static int idAdmin = 1;

//    public int getAdmin(String username, String password) throws SQLException {
//        try (Connection conn = JdbcUtils.getConn()) {
//            PreparedStatement stm = conn.prepareCall("SELECT * FROM admin WHERE username=?");
//            stm.setString(1, username);
//            ResultSet rs = stm.executeQuery();
//            if (rs.next()) {
//                if (password.equals("") == false && BCrypt.checkpw(password, rs.getString("password")) == true) {
//                    this.idAdmin = rs.getInt("id");
//                    return 1;
//                }
//            } else {
//                return -1;
//            }
//        }
//        return 0;
//    }
    public int getAdmin(String username, String password) throws SQLException {
    try (Connection conn = JdbcUtils.getConn()) {
        PreparedStatement stm = conn.prepareCall("SELECT * FROM admin WHERE username=?");
        stm.setString(1, username);
        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            String hashed = rs.getString("password");
            // Kiểm tra mật khẩu nếu không rỗng và hash hợp lệ
            if (!password.isEmpty()) {
                try {
                    if (BCrypt.checkpw(password, hashed)) {
                        this.idAdmin = rs.getInt("id");
                        return 1;
                    }
                } catch (IllegalArgumentException e) {
                    // Trường hợp hash bị sai định dạng (ví dụ bị cắt khi lưu DB)
                    System.err.println("Lỗi hash không hợp lệ: " + hashed);
                    return 0;
                }
            }
            return 0; // Sai mật khẩu
        } else {
            return -1; // Không tìm thấy username
        }
    }
}


    public boolean isUsernameExist(String username) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM admin WHERE username=?");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }

    public String getEmail(String username) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT email FROM admin WHERE username=?");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        }
        return null;
    }

//    public void addAdmin(String username, String password, String ho, String ten, String email) throws SQLException {
//        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
//        String sql = "INSERT INTO admin (username, password, ho, ten, email) VALUES (?, ?, ?, ?, ?)";
//        try (Connection conn = JdbcUtils.getConn()) {
//            PreparedStatement stm = conn.prepareCall(sql);
//            stm.setString(1, username);
//            stm.setString(2, hashedPassword);
//            stm.setString(3, ho);
//            stm.setString(4, ten);
//            stm.setString(5, email);
//            stm.executeUpdate();
//        }
//    }
    public void addAdmin(String username, String password, String ho, String ten, String email) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            addAdmin(conn, username, password, ho, ten, email);
        }
    }

    public void addAdmin(Connection conn, String username, String password, String ho, String ten, String email) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO admin (username, password, ho, ten, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = conn.prepareCall(sql)) {
            stm.setString(1, username);
            stm.setString(2, hashedPassword);
            stm.setString(3, ho);
            stm.setString(4, ten);
            stm.setString(5, email);
            stm.executeUpdate();
        }
    }

//    public void updatePassword(String username, String hashedPassword) throws SQLException {
//        String sql = "UPDATE admin SET password=? WHERE username=?";
//        try (Connection conn = JdbcUtils.getConn()) {
//            PreparedStatement stm = conn.prepareCall(sql);
//            stm.setString(1, hashedPassword);
//            stm.setString(2, username);
//            stm.executeUpdate();
//        }
//    }
    // Bổ sung thêm 1 hàm updatePassword có truyền conn
    public void updatePassword(Connection conn, String username, String hashedPassword) throws SQLException {
        String sql = "UPDATE admin SET password=? WHERE username=?";
        try (PreparedStatement stm = conn.prepareCall(sql)) {
            stm.setString(1, hashedPassword);
            stm.setString(2, username);
            stm.executeUpdate();
        }
    }

// Hàm cũ vẫn giữ để chạy thật
    public void updatePassword(String username, String hashedPassword) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            updatePassword(conn, username, hashedPassword);
        }
    }

    public void checkLogin(String username, String password) throws SQLException {
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng điền đủ thông tin!");
        }
        if (getAdmin(username, password) == -1) {
            throw new IllegalArgumentException("Sai tài khoản");
        } else if (getAdmin(username, password) == 0) {
            throw new IllegalArgumentException("Sai mật khẩu");
        }
    }

    public void validateNewPassword(String newPassword, String confirmPassword) throws IllegalArgumentException {
        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng điền đủ thông tin!");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp với mật khẩu mới!");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Độ dài mật khẩu ít nhất 8 ký tự");
        }

        if (!newPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 ký tự HOA!");
        }

        if (!newPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 ký tự THƯỜNG!");
        }

        if (!newPassword.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 ký tự SỐ!");
        }

        if (!newPassword.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("Mật khẩu phải chứa ít nhất 1 ký tự ĐẶC BIỆT!");
        }
    }
}
