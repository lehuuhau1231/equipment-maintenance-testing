/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.Admin;
import com.qhuong.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author lehuu
 */
public class AdminServices {
    public boolean getAdmin(String username,String password) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM admin WHERE username=? AND password=?");
            stm.setString(1, username);
            stm.setString(2, password);
            System.out.println("Test");
            System.out.println(stm);
        }
        
        return true;
    }
}
