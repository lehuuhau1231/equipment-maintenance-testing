/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.ThietBi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author lehuu
 */
public class ThietBiServices {

    public List<ThietBi> getThietBi() throws SQLException {
        List<ThietBi> equipments = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM thietbi");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ThietBi t = new ThietBi(rs.getInt("id"), rs.getString("tenThietBi"), rs.getDate("ngayNhap"), rs.getInt("idTrangThai"), rs.getDate("thanhLy"));
                equipments.add(t);
            }
        }
        return equipments;
    }

    public boolean isExist(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM thietbi WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    public void addThietBi(String tenThietBi, LocalDate ngayNhap, int idTrangThai) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO thietbi(tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUE(?, ?, ?, ?)");
            stm.setString(1, tenThietBi);
            stm.setDate(2, java.sql.Date.valueOf(ngayNhap));
            stm.setInt(3, idTrangThai);
            stm.setInt(4, AdminServices.idAdmin);
            stm.executeUpdate();
            conn.commit();
        }
    }

    public void updateThietBi(int id, String tenThietBi, LocalDate ngayNhap, LocalDate ngayThanhLy, int idTrangThai) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (ngayThanhLy == null) {
                stm = conn.prepareCall("UPDATE thietbi SET tenThietBi=?, ngayNhap=?, idTrangThai=?, idAdmin=? WHERE id=?");
                stm.setString(1, tenThietBi);
                stm.setDate(2, java.sql.Date.valueOf(ngayNhap));
                stm.setInt(3, idTrangThai);
                stm.setInt(4, AdminServices.idAdmin);
                stm.setInt(5, id);
            } else {
                stm = conn.prepareCall("UPDATE thietbi SET tenThietBi=?, thanhLy=?, ngayNhap=?, idTrangThai=?, idAdmin=? WHERE id=?");
                stm.setString(1, tenThietBi);
                stm.setDate(2, java.sql.Date.valueOf(ngayThanhLy));
                stm.setDate(3, java.sql.Date.valueOf(ngayNhap));
                stm.setInt(4, idTrangThai);
                stm.setInt(5, AdminServices.idAdmin);
                stm.setInt(6, id);
            }
            
            stm.executeUpdate();
            conn.commit();
        }
    }

    public int getIdEquipment(String name) throws SQLException {
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM thietbi WHERE tenThietBi=?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        }
        return -1;
    }
}
