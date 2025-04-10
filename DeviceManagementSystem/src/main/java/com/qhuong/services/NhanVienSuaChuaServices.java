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
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT * FROM nhanviensuachua");
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                NhanVienSuaChua n = new NhanVienSuaChua(rs.getInt("id"), rs.getString("tenNV"), 
                        rs.getDate("ngaySinh").toLocalDate(), rs.getString("CCCD"), rs.getString("soDT"), rs.getString("diaChi"), rs.getString("email"));
                employees.add(n);
            }
        }
        return employees;
    }
    
    public int getIdEmployee(String name) throws SQLException {
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT id FROM nhanviensuachua WHERE tenNV=?");
            stm.setString(1, name);
            ResultSet rs = stm.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        }
        return -1;
    }
    
    public String getEmail(int idNhanVien) throws SQLException {
        try(Connection conn = JdbcUtils.getConn()) {
            PreparedStatement stm = conn.prepareCall("SELECT email FROM nhanviensuachua WHERE id=?");
            stm.setInt(1, idNhanVien);
            ResultSet rs = stm.executeQuery();
            if (rs.next())
                return rs.getString("email");
        }
        return null;
    }
    
    public void addEmployee(String tenNV, LocalDate ngaySinh, String CCCD, String soDT, String diaChi, String email) throws SQLException {
        try(Connection conn = JdbcUtils.getConn()) {
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
        try(Connection conn = JdbcUtils.getConn()) {
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
}
