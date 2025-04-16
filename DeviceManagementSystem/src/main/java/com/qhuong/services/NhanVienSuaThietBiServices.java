/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.NhanVienSuaThietBi;
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
                if (rs.getLong("chiPhi") == 0) {
                    dates.add(rs.getTimestamp("ngaySua").toLocalDateTime());
                }
            }
        }
        return dates;
    }

    public List<NhanVienSuaThietBi> getListNotRepair() throws SQLException {
        List<NhanVienSuaThietBi> repairs = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngaySua, chiPhi, idThietBi, idNhanVien FROM nhanviensuathietbi");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                if (rs.getLong("chiPhi") == 0) {
                    NhanVienSuaThietBi nv = new NhanVienSuaThietBi(rs.getTimestamp("ngaySua").toLocalDateTime(), rs.getInt("idThietBi"), rs.getInt("idNhanVien"));
                    repairs.add(nv);
                }
            }
        }
        return repairs;
    }

    public int repairScheduleTimes(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT COUNT(*) FROM nhanviensuathietbi WHERE idThietBi=? AND chiPhi IS NULL AND moTa IS NULL");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void addRepairSchedule(LocalDateTime ngaySua, int idThietBi, int idNhanVien) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO nhanviensuathietbi(ngaySua, idThietbi, idNhanVien) VALUES(?, ?, ?)");
            stm.setTimestamp(1, Timestamp.valueOf(ngaySua));
            stm.setInt(2, idThietBi);
            stm.setInt(3, idNhanVien);
            stm.executeUpdate();
        }
    }

    public void validateAddRepairSchedule(LocalDateTime ngaySua, int idThietBi, int idNhanVien) throws SQLException {
        // Ràng buộc 1: Kiểm tra dữ liệu đầu vào không null
        if (ngaySua == null || idThietBi <= 0 || idNhanVien <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }

        if (repairScheduleTimes(idThietBi) == 1) {
            throw new IllegalArgumentException("Trong một thời điểm thiết bị chỉ được lập lịch sửa 1 lần");
        }

        // Ràng buộc 2: Kiểm tra trùng giờ làm việc của nhân viên
        LocalDate repairDate = ngaySua.toLocalDate();
        LocalTime repairTime = ngaySua.toLocalTime();
        List<LocalDateTime> employeeSchedule = getListDateTime(idNhanVien);
        boolean isTimeConflict = employeeSchedule.stream()
                .anyMatch(t -> t.toLocalDate().equals(repairDate) && t.toLocalTime().equals(repairTime));
        if (isTimeConflict) {
            throw new IllegalArgumentException("Lỗi! Nhân viên làm trùng giờ");
        }

        // Ràng buộc 3: Kiểm tra khối lượng công việc của nhân viên (tối đa 3 công việc/ngày)
        long dailyWorkload = OverWorkload(idNhanVien, ngaySua);
        System.out.println(dailyWorkload);
        if (dailyWorkload >= 3) {
            throw new IllegalArgumentException("Nhân viên chỉ được làm tối đa 3 công việc 1 ngày");
        }

        // Ràng buộc 4: Kiểm tra ngày sửa chữa trong khoảng 0-3 ngày từ hiện tại
        LocalDate now = LocalDate.now();
        if (!(repairDate.isAfter(now.minusDays(1)) && repairDate.isBefore(now.plusDays(4)))) {
            throw new IllegalArgumentException("Ngày sửa phải nằm trong 3 ngày kể từ ngày hiện tại");
        }
    }
    
    public int OverWorkload(int idNhanVien, LocalDateTime ngaySuaChua) throws SQLException {
        LocalDate date = ngaySuaChua.toLocalDate();
        int count = 0;
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT COUNT(*) FROM nhanviensuathietbi WHERE idNhanVien=? AND DATE(ngaySua) = ? AND chiPhi IS NULL");
            stm.setInt(1, idNhanVien);
            stm.setDate(2, Date.valueOf(date));
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                count += rs.getInt(1);
            }
            PreparedStatement stm2 = conn.prepareCall("SELECT COUNT(*) FROM baotri WHERE idNhanVien=? AND DATE(ngayBaoTri) = ?");
            stm2.setInt(1, idNhanVien);
            stm2.setDate(2, Date.valueOf(date));
            ResultSet rs2 = stm2.executeQuery();
            if (rs2.next()) {
                count += rs2.getInt(1);
            }
        }
        return count;
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

    public void updateReceipt(int id, long chiPhi, String moTa) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE nhanviensuathietbi SET chiPhi=?, moTa=? WHERE id=?");
            stm.setLong(1, chiPhi);
            stm.setString(2, moTa);
            stm.setInt(3, id);
            stm.executeUpdate();
        }
    }

    public void validateUpdateReceipt(int id, String chiPhi, String moTa) {
        if (chiPhi.trim().isEmpty() || moTa.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }

        if (moTa.length() > 250) {
            throw new IllegalArgumentException("Mô tả tối đa 250 ký tự");
        }

        String specialCharactersPattern = "^[\\p{L}\\p{N}\\s]+$";
        if (!moTa.matches(specialCharactersPattern)) {
            throw new IllegalArgumentException("Mô tả không được chứa ký tự đặc biệt");
        }

        if (!chiPhi.matches("\\d+")) {
            throw new IllegalArgumentException("Chi phí chỉ được nhập số nguyên dương");
        }
        try {
            long value = Long.parseLong(chiPhi);
            if (value < 10000) {
                throw new IllegalArgumentException("Chi phí từ 10.000 trở lên");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Lỗi! Số quá lớn");
        }
    }

    public NhanVienSuaThietBi getRepairScheduleNew() throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT b.ngaySua, t.tenThietBi, nv.tenNV, b.chiPhi, b.moTa"
                    + " FROM nhanviensuathietbi b "
                    + "JOIN ThietBi t ON b.idThietBi = t.id "
                    + "JOIN NhanVienSuaChua nv ON b.idNhanVien = nv.id");

            ResultSet rs = stm.executeQuery();
            if (rs.getString("moTa") != null && rs.getLong("chiPhi") != 0) {
                NhanVienSuaThietBi r = new NhanVienSuaThietBi(rs.getInt("id"),
                        rs.getTimestamp("ngaySua").toLocalDateTime(),
                        rs.getString("tenThietbi"), rs.getString("tenNV"),
                        rs.getLong("chiPhi"), rs.getString("moTa"));
                return r;
            }

        }
        return null;
    }
}
