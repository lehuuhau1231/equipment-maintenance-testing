/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

import java.util.Date;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaChua {
    private int id;
    private String tenNV;
    private Date ngaySinh;
    private String CCCD;
    private String soDT;
    private String diaChi;
    private String email;
    private int idAdmin;
    
    public NhanVienSuaChua(int id, String tenNV, Date ngaySinh, String CCCD, String SoDT, String diaChi, String email) {
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
     * @return the ngaySinh
     */
    public Date getNgaySinh() {
        return ngaySinh;
    }

    /**
     * @param ngaySinh the ngaySinh to set
     */
    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    /**
     * @return the cccd
     */
    public String getCCCD() {
        return CCCD;
    }

    /**
     * @param cccd the cccd to set
     */
    public void setCCCD(String CCCD) {
        this.CCCD = CCCD;
    }

    /**
     * @return the soDt
     */
    public String getSoDt() {
        return soDT;
    }

    /**
     * @param soDt the soDt to set
     */
    public void setSoDT(String soDT) {
        this.soDT = soDT;
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
    
}
