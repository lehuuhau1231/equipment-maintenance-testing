/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.TrangThai;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lehuu
 */
public class TrangThaiServices {

    public List<TrangThai> getTrangThai(boolean getFull, boolean getDamageStatus) throws SQLException {
        List<TrangThai> status = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (getFull == true) {
                stm = conn.prepareCall("SELECT * FROM trangthai WHERE id NOT IN(3,5" + (getDamageStatus ? ",2)": ")"));
            } else {
                stm = conn.prepareCall("SELECT * FROM trangthai WHERE id NOT IN(1,3,5)");
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                TrangThai t = new TrangThai(rs.getInt("id"), rs.getString("tenTrangThai"));
                status.add(t);
            }
        }
        return status;
    }

    public String getTrangThaiDaThanhLy() throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT tenTrangThai FROM trangthai WHERE tenTrangThai=?");
            stm.setString(1, "Đã thanh lý");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("tenTrangThai");
            }
        }
        return null;
    }

    public Map<Integer, String> getStatusMap() throws SQLException {
        Map<Integer, String> status = new HashMap<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM trangthai");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                status.put(rs.getInt("id"), rs.getString("tenTrangThai"));
            }
        }
        return status;
    }

    public int getIdStatus(String name) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM trangthai WHERE tenTrangThai=?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
}
