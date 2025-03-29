/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.NhanVienSuaChua;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaChuaServices {
    public List<NhanVienSuaChua> getNhanVien() throws SQLException {
        List<NhanVienSuaChua> employees = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM nhanviensuachua");
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                NhanVienSuaChua n = new NhanVienSuaChua(rs.getInt("id"), rs.getString("tenNV"), 
                        rs.getDate("ngaySinh"), rs.getString("CCCD"), rs.getString("soDT"), rs.getString("diaChi"), rs.getString("email"));
                employees.add(n);
            }
        }
        return employees;
    }
    
    public int getIdEmployee(String name) throws SQLException {
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM nhanviensuachua WHERE tenNV=?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        }
        return -1;
    }
}
