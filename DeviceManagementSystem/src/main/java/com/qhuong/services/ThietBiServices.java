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

    public List<ThietBi> getThietBi() throws SQLException {
        List<ThietBi> equipments = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM thietbi");
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
            PreparedStatement stm = conn.prepareCall("INSERT INTO thietbi(tenThietBi, ngayNhap, idTrangThai, idAdmin) VALUE(?, ?, ?, ?)");
            stm.setString(1, tenThietBi);
            stm.setDate(2, java.sql.Date.valueOf(ngayNhap));
            stm.setInt(3, idTrangThai);
            stm.setInt(4, 1);
            stm.executeUpdate();
            conn.commit();
        }
    }

    public void validateAddThietBi(String tenThietBi, LocalDate ngayNhap, int idTrangThai) throws SQLException {
        // Ràng buộc 1: Kiểm tra trường rỗng
        if (tenThietBi.trim().isEmpty() || ngayNhap == null || idTrangThai <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }

        if (tenThietBi.length() > 50) {
            throw new IllegalArgumentException("Tên thiết bị tối đa 50 ký tự");
        }

        if (containsSpecialCharacters(tenThietBi)) {
            throw new IllegalArgumentException("Tên thiết bị không được chứa ký tự đặc biệt");
        }

        // Ràng buộc 2: Ngày nhập phải là ngày hiện tại
        if (!ngayNhap.equals(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhập phải là ngày hiện tại");
        }

        // Ràng buộc 3: Kiểm tra tên thiết bị đã tồn tại
        if (isNameExist(tenThietBi.trim(), true)) {
            throw new IllegalArgumentException("Tên thiết bị này đã tồn tại");
        }

        if (idTrangThai != 2 && idTrangThai != 4) {
            throw new IllegalArgumentException("Quy định trang thái thiết bị khi thêm là: ĐANG HOẠT ĐỘNG HOẶC HỎNG HÓC");
        }
    }

    public void updateThietBi(int id, String tenThietBi, LocalDate ngayNhap, LocalDate ngayThanhLy, int idTrangThai) throws SQLException {
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

    public void validateUpdateThietBi(int id, String tenThietBi, LocalDate ngayNhap, LocalDate ngayThanhLy, int newIdTrangThai) throws SQLException {
        
        int currentStatus = getCurrentStatus(id);
    // Ràng buộc 1: Kiểm tra trường rỗng
        if (tenThietBi.trim().isEmpty() || ngayNhap == null || newIdTrangThai <= 0) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin");
        }
        
        if(newIdTrangThai == 3) 
            throw new IllegalArgumentException("Không được cập nhật thiệt bị ĐANG SỬA");

        if (currentStatus == 1) {
            throw new IllegalArgumentException("Không được cập nhật trạng thái ĐÃ THANH LÝ");
        }

        if (tenThietBi.length() > 50) {
            throw new IllegalArgumentException("Tên thiết bị tối đa 50 ký tự");
        }

        if (containsSpecialCharacters(tenThietBi)) {
            throw new IllegalArgumentException("Tên thiết bị không được chứa ký tự đặc biệt");
        }

        // Ràng buộc 4: Nếu trạng thái là "Đã thanh lý" (giả sử idTrangThai = 4), ngày thanh lý không được null
        if (newIdTrangThai == 1 && ngayThanhLy == null) {
            throw new IllegalArgumentException("Vui lòng điền ngày thanh lý khi trạng thái là 'Đã thanh lý'");
        }

        // Ràng buộc 5: Ngày thanh lý phải lớn hơn ngày nhập (nếu có ngày thanh lý)
        if (ngayThanhLy != null && ngayThanhLy.isBefore(ngayNhap)) {
            throw new IllegalArgumentException("Ngày thanh lý phải lớn hơn ngày nhập");
        }

        // Ràng buộc 3: Kiểm tra tên thiết bị đã tồn tại
        if (isNameExist(tenThietBi.trim(), false) == false) {
            throw new IllegalArgumentException("Tên thiết bị này đã tồn tại");
        }

        //Ràng buộc 9: Không thể cập nhật khi trạng thái "đang hoạt động" chuyển sang "đang sửa"
        if (currentStatus > 0) {
            if (currentStatus == 2 && newIdTrangThai == 3) {
                throw new IllegalArgumentException("Không thể cập nhật khi trạng thái 'đang hoạt động' chuyển sang 'đang sửa'");
            }
        }

        //Ràng buộc 10: Không thể cập nhật khi trạng thái "hỏng hóc" chuyển sang "đang hoạt động" 
        if (currentStatus > 0) {
            if (currentStatus == 4 && newIdTrangThai == 2) {
                throw new IllegalArgumentException("Không thể cập nhật khi trạng thái 'hỏng hóc' chuyển sang đang 'hoạt động'");
            }
        }
    }

    private boolean containsSpecialCharacters(String input) {
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
            conn.commit();
        }
    }

    public void updateStatus(int idThietbi, int idTrangThai) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("UPDATE thietbi SET idTrangThai=? WHERE id=?");
            stm.setInt(1, idTrangThai);
            stm.setInt(2, idThietbi);
            stm.executeUpdate();
            conn.commit();
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

            conn.commit();
        }
    }
}
