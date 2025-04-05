/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.NhanVienSuaThietBi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaThietBiServices {

    public List<NhanVienSuaThietBi> getNhanVienSuaThietBi(boolean fullData) throws SQLException {
        List<NhanVienSuaThietBi> repair = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT b.id, b.ngaySua, t.tenThietBi, nv.tenNV, b.chiPhi, b.moTa"
                    + " FROM nhanviensuathietbi b "
                    + "JOIN ThietBi t ON b.idThietBi = t.id "
                    + "JOIN NhanVienSuaChua nv ON b.idNhanVien = nv.id");
            ResultSet rs = stm.executeQuery();

            if (fullData == true) {
                while (rs.next()) {
                    if (rs.getString("moTa") != null && rs.getLong("chiPhi") != 0) {
                        NhanVienSuaThietBi r = new NhanVienSuaThietBi(rs.getInt("id"),
                                rs.getTimestamp("ngaySua").toLocalDateTime(),
                                rs.getString("tenThietbi"), rs.getString("tenNV"),
                                rs.getLong("chiPhi"), rs.getString("moTa"));
                        repair.add(r);
                    }
                }
            } else {
                while (rs.next()) {
                    if (rs.getString("moTa") == null && rs.getLong("chiPhi") == 0) {
                        NhanVienSuaThietBi r = new NhanVienSuaThietBi(rs.getInt("id"),
                                rs.getTimestamp("ngaySua").toLocalDateTime(),
                                rs.getString("tenThietbi"), rs.getString("tenNV"));
                        repair.add(r);
                    }
                }
            }
        }
        return repair;
    }

    public List<LocalDateTime> getListDateTime(int idNhanVien) throws SQLException {
        List<LocalDateTime> dates = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngaySua, chiPhi FROM nhanviensuathietbi WHERE idNhanVien=?");
            stm.setInt(1, idNhanVien);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                if(rs.getLong("chiPhi") == 0)
                    dates.add(rs.getTimestamp("ngaySua").toLocalDateTime());
            }
        }
        return dates;
    }

    public void updateRepairSchedule(LocalDateTime ngaySua, int idThietBi, int idNhanVien) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO nhanviensuathietbi(ngaySua, idThietbi, idNhanVien) VALUE(?, ?, ?)");
            stm.setTimestamp(1, Timestamp.valueOf(ngaySua));
            stm.setInt(2, idThietBi);
            stm.setInt(3, idNhanVien);
            stm.executeUpdate();
            conn.commit();
        }
    }

    public boolean checkIdEquipment(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT idThietBi FROM nhanviensuathietbi WHERE idThietBi=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    public void updateReceipt(int id, double chiPhi, String moTa) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE nhanviensuathietbi SET chiPhi=?, moTa=? WHERE id=?");
            stm.setDouble(1, chiPhi);
            stm.setString(2, moTa);
            stm.setInt(3, id);
            stm.executeUpdate();
            conn.commit();
        }
    }
}
