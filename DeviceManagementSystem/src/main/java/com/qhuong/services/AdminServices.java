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
    public static int idAdmin;
    
    public int getAdmin(String username,String password) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM admin WHERE username=?");
            stm.setString(1, username);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                if(BCrypt.checkpw(password, rs.getString("password")) == true) {
                    this.idAdmin = rs.getInt("id");
                    return 1;
                }
            } else
                return -1;
        }
        return 0;
    }
    
    public void addAdmin(String username, String password, String ho, String ten, String email) throws SQLException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO admin (username, password, ho, ten, email) VALUE(?, ?, ?, ?, ?)";
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setString(1, username);
            stm.setString(2, hashedPassword);
            stm.setString(3, ho);
            stm.setString(4, ten);
            stm.setString(5, email);
            stm.executeUpdate();
            conn.commit();
        }
        
    }
}
