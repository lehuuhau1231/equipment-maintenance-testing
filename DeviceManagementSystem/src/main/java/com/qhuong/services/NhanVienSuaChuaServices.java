/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.services;

import com.qhuong.pojo.JdbcUtils;
import com.qhuong.pojo.NhanVienSuaChua;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaChuaServices {

    public List<NhanVienSuaChua> getNhanVien() throws SQLException {
        List<NhanVienSuaChua> employees = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM nhanviensuachua");
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                NhanVienSuaChua n = new NhanVienSuaChua(rs.getInt("id"), rs.getString("tenNV"),
                        rs.getDate("ngaySinh").toLocalDate(), rs.getString("CCCD"), rs.getString("soDT"), rs.getString("diaChi"), rs.getString("email"));
                employees.add(n);
            }
        }
        return employees;
    }

    public int getIdEmployee(String name) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM nhanviensuachua WHERE tenNV=?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    public String getEmail(int idNhanVien) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT email FROM nhanviensuachua WHERE id=?");
            stm.setInt(1, idNhanVien);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getString("email");
            }
        }
        return null;
    }

    public void addEmployee(String tenNV, LocalDate ngaySinh, String CCCD, String soDT, String diaChi, String email) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("INSERT INTO nhanviensuachua(tenNV, ngaySinh, CCCD, soDT, diaChi, email, idadmin) VALUE(?, ?, ?, ?, ?, ?, ?)");
            stm.setString(1, tenNV);
            stm.setDate(2, Date.valueOf(ngaySinh));
            stm.setString(3, CCCD);
            stm.setString(4, soDT);
            stm.setString(5, diaChi);
            stm.setString(6, email);
            stm.setInt(7, AdminServices.idAdmin);
            stm.executeUpdate();
            conn.commit();
        }
    }

    public void updateEmployee(NhanVienSuaChua nv) throws SQLException {
        try (Connection conn = JdbcUtils.getConn()) {
            System.out.println(nv.getNgaySinh());
            PreparedStatement stm = conn.prepareCall("UPDATE nhanviensuachua SET tenNV=?, ngaySinh=?, CCCD=?, soDT=?, diaChi=?, email=?, idadmin=? WHERE id=?");
            stm.setString(1, nv.getTenNV());
            stm.setDate(2, Date.valueOf(nv.getNgaySinh()));
            stm.setString(3, nv.getCCCD());
            stm.setString(4, nv.getSoDT());
            stm.setString(5, nv.getDiaChi());
            stm.setString(6, nv.getEmail());
            stm.setInt(7, AdminServices.idAdmin);
            stm.setInt(8, nv.getId());
            stm.executeUpdate();
            conn.commit();
        }
    }

    public boolean validateEmployeeInput(String tenNV, LocalDate ngaySinh, String CCCD, String soDT, String diaChi, String email) throws SQLException {
        if (tenNV == null || tenNV.isEmpty() || CCCD == null || CCCD.isEmpty()
                || soDT == null || soDT.isEmpty() || ngaySinh == null
                || diaChi == null || diaChi.isEmpty() || email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng điền đầy đủ thông tin!");
        }

        if (tenNV.length() > 50) 
            throw new IllegalArgumentException("Tên nhân viên tối đa 50 ký tự");

        if (CCCD.length() != 12) 
            throw new IllegalArgumentException("CCCD 12 số");

        if (soDT.length() != 10) 
            throw new IllegalArgumentException("Số điện thoại 10 số");

        if (diaChi.length() > 250) 
            throw new IllegalArgumentException("Địa chỉ tối đa 250 ký tự");

        if (email.length() > 50) 
            throw new IllegalArgumentException("Email tối đa 50 ký tự");
        
        if(containsSpecialCharacters(tenNV)) 
            throw new IllegalArgumentException("Tên nhân viên không được chứa ký tự đặc biệt");
        
        if(containsSpecialCharacters(diaChi)) 
            throw new IllegalArgumentException("Địa chỉ không được chứa ký tự đặc biệt");
        
        if(isNumber(CCCD))
            throw new IllegalArgumentException("Căn cước công dân chỉ được chứa ký tự số");
        
        if(isNumber(soDT))
            throw new IllegalArgumentException("Số điện thoại chỉ được chứa ký tự số");

        LocalDate birthdDate = LocalDate.now().minusYears(18);
        if (!ngaySinh.isBefore(birthdDate)) 
            throw new IllegalArgumentException("Nhân viên không đủ 18 tuổi");

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) 
            throw new IllegalArgumentException("Email không hợp lệ. Vui lòng nhập lại email với định dạng hợp lệ (ví dụ: example@domain.com).");
        return true;
    }

    public boolean containsSpecialCharacters(String input) {
        String specialCharactersPattern = "^[\\p{L}\\p{N}\\s]+$";
        return !input.matches(specialCharactersPattern);
    }
    
    public boolean isNumber(String input) {
        return !input.matches("\\d+");
    }
}
