/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

import java.time.LocalDate;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaChua {
    private int id;
    private String tenNV;
    private LocalDate ngaySinh;
    private String CCCD;
    private String soDT;
    private String diaChi;
    private String email;
    private int idAdmin;
    
    public NhanVienSuaChua(int id, String tenNV, LocalDate ngaySinh, String CCCD, String soDT, String diaChi, String email) {
        this.id = id;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.CCCD = CCCD;
        this.soDT = soDT;
        this.diaChi = diaChi;
        this.email = email;
    };
    
    public NhanVienSuaChua(String tenNV) {
        this.tenNV = tenNV;
    };

    @Override
    public String toString() {
        return tenNV;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the tenNV
     */
    public String getTenNV() {
        return tenNV;
    }

    /**
     * @param tenNV the tenNV to set
     */
    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    /**
     * @return the CCCD
     */
    public String getCCCD() {
        return CCCD;
    }

    /**
     * @param CCCD the CCCD to set
     */
    public void setCCCD(String CCCD) {
        this.CCCD = CCCD;
    }

    

    /**
     * @return the diaChi
     */
    public String getDiaChi() {
        return diaChi;
    }

    /**
     * @param diaChi the diaChi to set
     */
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the soDT
     */
    public String getSoDT() {
        return soDT;
    }

    /**
     * @param soDT the soDT to set
     */
    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    /**
     * @return the ngaySinh
     */
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    /**
     * @param ngaySinh the ngaySinh to set
     */
    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    
}
