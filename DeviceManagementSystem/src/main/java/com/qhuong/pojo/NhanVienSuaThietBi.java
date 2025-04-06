/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

import java.time.LocalDateTime;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaThietBi {
    private int id;
    private LocalDateTime ngaySua;
    private long chiPhi;
    private String tenThietBi;
    private String tenNV;
    private String moTa;
    
    public NhanVienSuaThietBi(int id, LocalDateTime ngaySua, String tenThietBi, String tenNV) {
        this.id = id;
        this.ngaySua = ngaySua;
        this.tenThietBi = tenThietBi;
        this.tenNV = tenNV;
    };
    
    public NhanVienSuaThietBi(int id, LocalDateTime ngaySua, String tenThietBi, String tenNV, long chiPhi, String moTa) {
        this.id = id;
        this.ngaySua = ngaySua;
        this.tenThietBi = tenThietBi;
        this.tenNV = tenNV;
        this.chiPhi = chiPhi;
        this.moTa = moTa;
    }
    /**
     * @return the chiPhi
     */
    public long getChiPhi() {
        return chiPhi;
    }

    /**
     * @param chiPhi the chiPhi to set
     */
    public void setChiPhi(long chiPhi) {
        this.chiPhi = chiPhi;
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
     * @return the ngaySua
     */
    public LocalDateTime getNgaySua() {
        return ngaySua;
    }

    /**
     * @param ngaySua the ngaySua to set
     */
    public void setNgaySua(LocalDateTime ngaySua) {
        this.ngaySua = ngaySua;
    }

    /**
     * @return the tenThietBi
     */
    public String getTenThietBi() {
        return tenThietBi;
    }

    /**
     * @param tenThietBi the tenThietBi to set
     */
    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
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
     * @return the moTa
     */
    public String getMoTa() {
        return moTa;
    }

    /**
     * @param moTa the moTa to set
     */
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
