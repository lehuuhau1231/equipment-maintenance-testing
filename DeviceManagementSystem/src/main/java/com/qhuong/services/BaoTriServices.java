/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.BaoTri;
import com.qhuong.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lehuu
 */
public class BaoTriServices {

    public List<BaoTri> getBaoTri() throws SQLException {
        List<BaoTri> maintenance = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT b.id, b.ngayBaoTri, t.tenThietBi, nv.tenNV "
                    + "FROM baotri b "
                    + "JOIN ThietBi t ON b.idThietBi = t.id "
                    + "JOIN NhanVienSuaChua nv ON b.idNhanVien = nv.id");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                BaoTri b = new BaoTri(rs.getInt("id"), rs.getTimestamp("ngayBaoTri").toLocalDateTime(), rs.getString("tenThietBi"), rs.getString("tenNV"));
                maintenance.add(b);
            }
        }
        return maintenance;
    }
    
    public void addMaintenanceSchedule(LocalDateTime ngayBaoTri, int idThietBi, int idNhanVien) throws SQLException {
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO baotri(ngayBaoTri, idThietBi, idNhanVien) VALUE(?, ?, ?)");
            stm.setTimestamp(1, Timestamp.valueOf(ngayBaoTri));
            stm.setInt(2, idThietBi);
            stm.setInt(3, idNhanVien);
            stm.executeUpdate();
            conn.commit();
        }
    }
}
