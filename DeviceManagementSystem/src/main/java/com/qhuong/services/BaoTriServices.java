/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.BaoTri;
import com.qhuong.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public List<Integer> listEquipmentForMaintenance() throws SQLException {
        List<Integer> equipment = new ArrayList<>();
        LocalDate today = LocalDate.now();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT idThietBi FROM baotri WHERE ngayBaoTri >= ? AND ngayBaoTri < ?");
            stm.setDate(1, Date.valueOf(today));
            stm.setDate(2, Date.valueOf(today.plusDays(1)));
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                equipment.add(rs.getInt("idThietBi"));
            }
            return equipment;
        }
    }

    public List<Integer> getListMaintenanceCompleted() throws SQLException {
        List<Integer> equipment = new ArrayList<>();
        LocalDate today = LocalDate.now();
        TrangThaiServices statusService = new TrangThaiServices();
        int idTrangThai = statusService.getIdStatus("Bảo trì");
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT b.idThietBi FROM baotri b JOIN thietbi t ON b.idThietBi = t.id WHERE t.idTrangThai=? AND b.ngayBaoTri < ?");
            stm.setInt(1, idTrangThai);
            stm.setDate(2, Date.valueOf(today));
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                equipment.add(rs.getInt("idThietBi"));
            }
            return equipment;
        }
    }

    public List<BaoTri> getListMaintenanceToSendEmail() throws SQLException {
        List<BaoTri> maintenance = new ArrayList<>();
        LocalDate nextDate = LocalDate.now().plusDays(1);
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id, ngayBaoTri, idThietBi, idNhanVien FROM baotri WHERE ngayBaoTri >= ? AND ngayBaoTri < ? AND sentEmail = FALSE");
            stm.setDate(1, Date.valueOf(nextDate));
            stm.setDate(2, Date.valueOf(nextDate.plusDays(1)));
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                BaoTri nv = new BaoTri(rs.getInt("id"), rs.getTimestamp("ngayBaoTri").toLocalDateTime(), rs.getInt("idThietBi"), rs.getInt("idNhanVien"));
                maintenance.add(nv);
            }
        }
        return maintenance;
    }

    public void updateFieldSentEmailStatus(int id) throws SQLException {

        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE baotri SET sentEmail = TRUE WHERE id=?");
            stm.setInt(1, id);
            stm.executeUpdate();
        }
    }

    public List<LocalDate> getLocalDate(int idThietBi) throws SQLException {
        List<LocalDate> date = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayBaoTri FROM baotri WHERE idThietBi=? ORDER BY ngayBaoTri");
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
        }
    }

    public void validateAddMaintenanceSchedule(LocalDateTime ngayLapLich, LocalDateTime ngayBaoTri, int idThietBi, int idNhanVien) throws SQLException {
        // Ràng buộc 1: Kiểm tra dữ liệu đầu vào không null
        if (ngayLapLich == null || ngayBaoTri == null || idThietBi <= 0 || idNhanVien <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }

        if (getMaintenanceTimes(idThietBi) > 2) {
            throw new IllegalArgumentException("Đủ số lần bảo trì! Một thiết bị chỉ được bảo trì 2 lần");
        }

        // Ràng buộc 2: Kiểm tra ngày bảo trì trong khoảng 3-6 tháng
        ThietBiServices equipmentService = new ThietBiServices();
        LocalDateTime lastMaintenance = getMaintenanceDate(idThietBi);
        LocalDate baseDate;
        if (lastMaintenance != null) {
            baseDate = lastMaintenance.toLocalDate();
        } else {
            baseDate = equipmentService.getImportDateById(idThietBi);
        }
        LocalDate maintenanceDate = ngayBaoTri.toLocalDate();
        if (!(maintenanceDate.isAfter(baseDate.plusMonths(3).minusDays(1)) && maintenanceDate.isBefore(baseDate.plusMonths(6).plusDays(1)))) {
            throw new IllegalArgumentException("Ngày bảo trì phải trong khoảng 3 đến 6 tháng kể từ " + (lastMaintenance != null ? "ngày bảo trì thứ nhất" : "ngày nhập"));
        }

        // Ràng buộc 3: Kiểm tra khối lượng công việc của nhân viên (tối đa 3 công việc/ngày)
        long dailyWorkload = OverWorkload(idNhanVien, ngayBaoTri);
        System.out.println(dailyWorkload);
        if (dailyWorkload >= 3) {
            throw new IllegalArgumentException("Nhân viên chỉ được làm tối đa 3 công việc trong 1 ngày");
        }

        // Ràng buộc 4: Kiểm tra trùng giờ làm việc của nhân viên
        LocalTime maintenanceTime = ngayBaoTri.toLocalTime();
        List<LocalDateTime> employeeSchedule = getListDateTime(idNhanVien);
        boolean isTimeConflict = employeeSchedule.stream().anyMatch(t -> t.toLocalDate().equals(maintenanceDate) && t.toLocalTime().equals(maintenanceTime));
        if (isTimeConflict) {
            throw new IllegalArgumentException("Nhân viên đã có lịch trùng giờ tại thời điểm này");
        }
    }

    public int getMaintenanceTimes(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT COUNT(*) FROM baotri WHERE idThietBi=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
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

    public int OverWorkload(int idNhanVien, LocalDateTime ngayBaoTri) throws SQLException {
        LocalDate date = ngayBaoTri.toLocalDate();
        int count = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT COUNT(*) FROM baotri WHERE idNhanVien=? AND DATE(ngayBaoTri) = ?");
            stm.setInt(1, idNhanVien);
            stm.setDate(2, Date.valueOf(date));
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                count += rs.getInt(1);
            }
            PreparedStatement stm2 = conn.prepareCall("SELECT COUNT(*) FROM nhanviensuathietbi WHERE idNhanVien=? AND DATE(ngaySua) = ? AND chiPhi IS NULL");
            stm2.setInt(1, idNhanVien);
            stm2.setDate(2, Date.valueOf(date));
            ResultSet rs2 = stm2.executeQuery();
            if (rs2.next()) {
                count += rs2.getInt(1);
            }
        }
        return count;
    }

    public LocalDateTime getScheduleDate(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayLapLich FROM baotri WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("ngayLapLich").toLocalDateTime();
            }
        }
        return null;
    }

    public LocalDateTime getMaintenanceDateById(int id) throws SQLException {
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

    public List<LocalDateTime> getListMaintenanceDateNotId(int id) throws SQLException {
        List<LocalDateTime> dates = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayBaoTri FROM baotri WHERE id<>?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                dates.add(rs.getTimestamp("ngayBaoTri").toLocalDateTime());
            }
        }
        return dates;
    }

    public List<BaoTri> getUnmaintenanceEquipment() throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            List<BaoTri> maintenance = new ArrayList<>();
            PreparedStatement stm = conn.prepareCall("SELECT t.id, b.ngayBaoTri, t.ngayNhap"
                    + " FROM baotri b LEFT JOIN thietbi t ON b.idThietBi = t.id GROUP BY t.id, b.ngayBaoTri, t.ngayNhap HAVING COUNT(b.idThietBi) < 2");
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                BaoTri b = new BaoTri(rs.getTimestamp("ngayBaoTri").toLocalDateTime(), rs.getTimestamp("ngayNhap").toLocalDateTime(), rs.getInt("id"));
                maintenance.add(b);
            }
            return maintenance;
        }
    }

    public void updateScheduleMaintenance(int id, int idNhanVien) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE baotri SET idNhanVien=? WHERE id=?");
            stm.setInt(1, idNhanVien);
            stm.setInt(2, id);
            stm.executeUpdate();
        }
    }

    public void validateUpdateScheduleMaintenance(int id, int idNhanVien) throws SQLException {
        // Ràng buộc 1: Kiểm tra dữ liệu đầu vào không null
        if (id <= 0 || idNhanVien <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }

        if (!isScheduleExist(id)) {
            throw new IllegalArgumentException("Lịch bảo trì không tồn tại");
        }

        LocalDateTime ngayBaoTri = getMaintenanceDateById(id);
        checkLastTwoDaysUpdate(ngayBaoTri.toLocalDate());

        // Ràng buộc 4: Kiểm tra trùng giờ làm việc của nhân viên
        List<LocalDateTime> employeeSchedule = getListDateTime(idNhanVien);
        LocalDate maintenanceDate = ngayBaoTri.toLocalDate();
        LocalTime maintenanceTime = ngayBaoTri.toLocalTime();
        boolean isTimeConflict = employeeSchedule.stream().anyMatch(t -> t.toLocalDate().equals(maintenanceDate) && t.toLocalTime().equals(maintenanceTime));
        if (isTimeConflict) {
            throw new IllegalArgumentException("Nhân viên đã có lịch trùng giờ tại thời điểm này");
        }
    }

    public void checkLastTwoDaysUpdate(LocalDate ngayBaoTri) {
        if (ngayBaoTri == null) {
            throw new IllegalArgumentException("Vui lòng nhập ngày bảo trì");
        }

        if (!LocalDate.now().isBefore(ngayBaoTri.minusDays(2))) {
            throw new IllegalArgumentException("Không được cập nhật lịch trong 2 ngày cuối");
        }
    }

    public boolean isScheduleExist(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM baotri WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            return rs.next();
        }
    }
}
