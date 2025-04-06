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
            PreparedStatement stm = conn.prepareCall("SELECT b.id, b.ngayLapLich, b.ngayBaoTri, t.tenThietBi, nv.tenNV "
                    + "FROM baotri b "
                    + "JOIN ThietBi t ON b.idThietBi = t.id "
                    + "JOIN NhanVienSuaChua nv ON b.idNhanVien = nv.id");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                BaoTri b = new BaoTri(rs.getInt("id"), rs.getTimestamp("ngayLapLich").toLocalDateTime(), rs.getTimestamp("ngayBaoTri").toLocalDateTime(), rs.getString("tenThietBi"), rs.getString("tenNV"));
                maintenance.add(b);
            }
        }
        return maintenance;
    }

    public List<BaoTri> getMaintenanceDate() throws SQLException {
        List<BaoTri> maintenances = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT b.ngayBaoTri, t.tenThietBi FROM baotri b JOIN Thietbi t ON b.idThietBi = t.id");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                BaoTri b = new BaoTri(rs.getTimestamp("ngayBaoTri").toLocalDateTime(), rs.getString("tenThietBi"));
                maintenances.add(b);
            }
            return maintenances;
        }
    }

    public List<LocalDate> getLocalDate(int idThietBi) throws SQLException {
        List<LocalDate> date = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayBaoTri FROM baotri WHERE idThietBi=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                LocalDate ngayBaoTri = rs.getDate("ngayBaoTri").toLocalDate();
                date.add(ngayBaoTri);
            }
            return date;
        }
    }

    public void addMaintenanceSchedule(LocalDateTime ngayLapLich, LocalDateTime ngayBaoTri, int idThietBi, int idNhanVien) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO baotri(ngayLapLich, ngayBaoTri, idThietBi, idNhanVien) VALUE(?, ?, ?, ?)");
            stm.setTimestamp(1, Timestamp.valueOf(ngayLapLich));
            stm.setTimestamp(2, Timestamp.valueOf(ngayBaoTri));
            stm.setInt(3, idThietBi);
            stm.setInt(4, idNhanVien);
            stm.executeUpdate();
            conn.commit();
        }
    }

    public List<LocalDateTime> getListDateTime(int idNhanVien) throws SQLException {
        List<LocalDateTime> dates = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayBaoTri FROM baotri WHERE idNhanVien=?");
            stm.setInt(1, idNhanVien);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                dates.add(rs.getTimestamp("ngayBaoTri").toLocalDateTime());
            }
        }
        return dates;
    }

    public int getMaintenanceCount(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT COUNT(*) FROM baotri WHERE idThietBi=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public LocalDateTime getScheduleDate(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayLapLich FROM baotri WHERE idThietBi=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("ngayLapLich").toLocalDateTime();
            }
        }
        return null;
    }

    public LocalDateTime getMaintenanceDate(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayBaoTri FROM baotri WHERE idThietBi=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("ngayBaoTri").toLocalDateTime();
            }
        }
        return null;
    }

    public LocalDateTime getMaintenanceDateOfId(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayBaoTri FROM baotri WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("ngayBaoTri").toLocalDateTime();
            }
        }
        return null;
    }

    public void updateScheduleMaintenance(int id, LocalDateTime ngayBaoTri, int idThietBi, int idNhanVien) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE baotri SET ngayBaoTri=?, idThietBi=?, idNhanVien=? WHERE id=?");
            stm.setTimestamp(1, Timestamp.valueOf(ngayBaoTri));
            stm.setInt(2, idThietBi);
            stm.setInt(3, idNhanVien);
            stm.setInt(4, id);
            stm.executeUpdate();
            conn.commit();
        }
    }
}
