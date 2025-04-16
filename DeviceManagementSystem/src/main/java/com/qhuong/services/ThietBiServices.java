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
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author lehuu
 */
public class ThietBiServices {

    public List<ThietBi> getThietBi(String kw) throws SQLException {
        List<ThietBi> equipments = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (kw.trim().isEmpty()) {
                stm = conn.prepareCall("SELECT * FROM thietbi");
            } else {
                stm = conn.prepareCall("SELECT * FROM thietbi WHERE tenThietBi LIKE CONCAT('%', ?, '%')");
                stm.setString(1, kw);
            }
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ThietBi t = new ThietBi(rs.getInt("id"), rs.getString("tenThietBi"),
                        rs.getDate("ngayNhap"), rs.getInt("idTrangThai"), rs.getDate("thanhLy"), rs.getString("thongBao"));
                equipments.add(t);
            }
        }
        return equipments;
    }

    public List<ThietBi> getImportDateEquipment() throws SQLException {
        List<ThietBi> equipments = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id, ngayNhap FROM thietbi");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                ThietBi t = new ThietBi(rs.getInt("id"), rs.getDate("ngayNhap"));
                equipments.add(t);
            }
        }
        return equipments;
    }

    public LocalDate getImportDateById(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT ngayNhap FROM thietbi WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("ngayNhap").toLocalDateTime().toLocalDate();
            }
        }
        return null;
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

    public boolean isNameExist(String tenThietBi, boolean addEquipmentAction) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT COUNT(*) FROM thietbi WHERE tenThietbi=?");
            stm.setString(1, tenThietBi);
            ResultSet rs = stm.executeQuery();
            if (addEquipmentAction) {
                rs.next();
                return rs.getInt(1) == 1;
            } else {
                rs.next();
                return rs.getInt(1) <= 1;
            }
        }
    }

    public void addThietBi(String tenThietBi, LocalDate ngayNhap, int idTrangThai) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO thietbi(tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUES(?, ?, ?, ?)");
            stm.setString(1, tenThietBi);
            stm.setDate(2, java.sql.Date.valueOf(ngayNhap));
            stm.setInt(3, idTrangThai);
            stm.setInt(4, AdminServices.idAdmin);
            stm.executeUpdate();
        }
    }

    public void validateAddThietBi(String tenThietBi, LocalDate ngayNhap, int idTrangThai) throws SQLException {
        TrangThaiServices statusServices = new TrangThaiServices();
        int idTrangThaiHoatDong = statusServices.getIdStatus("Đang hoạt động");
        int idTrangThaiHongHoc = statusServices.getIdStatus("Hỏng hóc");
        // Ràng buộc 1: Kiểm tra trường rỗng
        if (tenThietBi.trim().isEmpty() || ngayNhap == null || idTrangThai <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }

        checkNameRules(tenThietBi);

        // Ràng buộc 2: Ngày nhập phải là ngày hiện tại
        if (!ngayNhap.equals(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhập phải là ngày hiện tại");
        }

        // Ràng buộc 3: Kiểm tra tên thiết bị đã tồn tại
        if (isNameExist(tenThietBi.trim(), true)) {
            throw new IllegalArgumentException("Tên thiết bị này đã tồn tại");
        }

        if (idTrangThai != idTrangThaiHoatDong && idTrangThai != idTrangThaiHongHoc) {
            throw new IllegalArgumentException("Quy định trạng thái thiết bị khi thêm là: ĐANG HOẠT ĐỘNG HOẶC HỎNG HÓC");
        }
    }

    public void updateThietBi(int id, String tenThietBi, LocalDate ngayThanhLy, int idTrangThai) throws SQLException {
        //Ràng buộc 6: những thiết bị có trạng thái "đang hoạt động" đã được lập lịch bảo trì trước đó mà chuyển sang "đã thanh lý" thì phải HỦY lịch bảo trì
        int currentStatus = getCurrentStatus(id);
        if (currentStatus > 0) {
            if (idTrangThai == 1) {
                int idThietBi = getIdEquipment(tenThietBi);
                deleteAllSchedule(idThietBi);
            }
        }
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm;
            if (ngayThanhLy == null) {
                stm = conn.prepareCall("UPDATE thietbi SET tenThietBi=?, idTrangThai=?, idAdmin=? WHERE id=?");
                stm.setString(1, tenThietBi);
                stm.setInt(2, idTrangThai);
                stm.setInt(3, AdminServices.idAdmin);
                stm.setInt(4, id);
            } else {
                stm = conn.prepareCall("UPDATE thietbi SET tenThietBi=?, thanhLy=?, idTrangThai=?, idAdmin=? WHERE id=?");
                stm.setString(1, tenThietBi);
                stm.setDate(2, java.sql.Date.valueOf(ngayThanhLy));
                stm.setInt(3, idTrangThai);
                stm.setInt(4, AdminServices.idAdmin);
                stm.setInt(5, id);
            }

            stm.executeUpdate();
        }
    }

    public void validateUpdateThietBi(int id, String tenThietBi, LocalDate ngayThanhLy, int newIdTrangThai) throws SQLException {
        int currentStatus = getCurrentStatus(id);

        checkEmptyFields(tenThietBi, newIdTrangThai);
        checkNameRules(tenThietBi);
        checkStatusUpdateRules(currentStatus, newIdTrangThai);
        checkThanhLyDateRules(id, newIdTrangThai, ngayThanhLy);
        checkNameExists(tenThietBi.trim());
    }

// --- Các phương thức con bên dưới:
    public void checkEmptyFields(String tenThietBi, int newIdTrangThai) {
        if (tenThietBi.trim().isEmpty() || newIdTrangThai <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }
    }

    public void checkNameRules(String tenThietBi) {
        if (tenThietBi.length() > 50) {
            throw new IllegalArgumentException("Tên thiết bị tối đa 50 ký tự");
        }

        if (containsSpecialCharacters(tenThietBi)) {
            throw new IllegalArgumentException("Tên thiết bị không được chứa ký tự đặc biệt");
        }
    }

    public void checkStatusUpdateRules(int currentStatus, int newIdTrangThai) throws SQLException {
        TrangThaiServices statusServices = new TrangThaiServices();
        int idDangSua = statusServices.getIdStatus("Đang sửa");
        int idBaoTri = statusServices.getIdStatus("Bảo trì");
        int idThanhLy = statusServices.getIdStatus("Đã thanh lý");
        int idHoatDong = statusServices.getIdStatus("Đang hoạt động");
        int idHongHoc = statusServices.getIdStatus("Hỏng hóc");

        if (newIdTrangThai == idDangSua) {
            throw new IllegalArgumentException("Không được cập nhật thiết bị ĐANG SỬA");
        }

        if (currentStatus == idThanhLy) {
            throw new IllegalArgumentException("Không được cập nhật thiết bị ĐÃ THANH LÝ");
        }

        if (currentStatus == idBaoTri) {
            throw new IllegalArgumentException("Không được cập nhật thiết bị ĐANG BẢO TRÌ");
        }

        // Đang hoạt động → Đang sửa
        if (currentStatus == idHoatDong && newIdTrangThai == idDangSua) {
            throw new IllegalArgumentException("Không thể cập nhật khi trạng thái 'đang hoạt động' chuyển sang 'đang sửa'");
        }

        // Hỏng hóc → Đang hoạt động
        if (currentStatus == idHongHoc && newIdTrangThai == idHoatDong) {
            throw new IllegalArgumentException("Không thể cập nhật khi trạng thái 'hỏng hóc' chuyển sang 'đang hoạt động'");
        }
    }

    public void checkThanhLyDateRules(int id, int newIdTrangThai, LocalDate ngayThanhLy) throws SQLException {
        TrangThaiServices statusServices = new TrangThaiServices();
        int idThanhLy = statusServices.getIdStatus("Đã thanh lý");

        if (newIdTrangThai == idThanhLy && ngayThanhLy == null) {
            throw new IllegalArgumentException("Vui lòng điền ngày thanh lý khi trạng thái là 'Đã thanh lý'");
        }

        if (ngayThanhLy != null && !ngayThanhLy.isAfter(LocalDate.now().minusDays(1))) {
            throw new IllegalArgumentException("Ngày thanh lý phải từ ngày hiện tại trở đi");
        }
    }

    public void checkNameExists(String tenThietBi) throws SQLException {
        if (!isNameExist(tenThietBi, false)) {
            throw new IllegalArgumentException("Tên thiết bị này đã tồn tại");
        }
    }
    //=============================

    public boolean containsSpecialCharacters(String input) {
        // Regex kiểm tra ký tự đặc biệt: bất kỳ ký tự nào không phải chữ cái, số hoặc khoảng trắng
        String specialCharactersPattern = "[^\\p{L}\\p{N}\\s]";
        return Pattern.compile(specialCharactersPattern).matcher(input).find();
    }

    public int getCurrentStatus(int id) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT idTrangThai FROM thietbi WHERE id=?");
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("idTrangThai");
            }
        }
        return -1;
    }

    public int getIdEquipment(String name) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM thietbi WHERE tenThietBi=?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public String getNameById(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT tenThietBi FROM thietbi WHERE id=?");
            stm.setInt(1, idThietBi);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("tenThietBi");
            }
        }
        return null;
    }

    public void addNotification(int idThietBi, String info) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE thietBi SET thongBao=? WHERE id=?");
            stm.setString(1, info);
            stm.setInt(2, idThietBi);
            stm.executeUpdate();
        }
    }

    public void updateStatus(int idThietbi, int idTrangThai) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE thietbi SET idTrangThai=? WHERE id=?");
            stm.setInt(1, idTrangThai);
            stm.setInt(2, idThietbi);
            stm.executeUpdate();
        }
    }

    public void deleteAllSchedule(int idThietBi) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stmBaoTri = conn.prepareCall("DELETE FROM baotri WHERE idThietBi=?");
            stmBaoTri.setInt(1, idThietBi);
            stmBaoTri.executeUpdate();

            PreparedStatement stmSuaChua = conn.prepareCall("DELETE FROM nhanviensuathietbi WHERE idThietBi=? AND chiPhi IS NULL AND moTa IS NULL");
            stmSuaChua.setInt(1, idThietBi);
            stmSuaChua.executeUpdate();
        }
    }

    public void validateSearch(String kw) {
        if (kw.length() > 50) {
            throw new IllegalArgumentException("Tối đa 50 ký tự");
        }

        if (containsSpecialCharacters(kw)) {
            throw new IllegalArgumentException("Không được chứa ký tự đặc biệt");
        }
    }
}
