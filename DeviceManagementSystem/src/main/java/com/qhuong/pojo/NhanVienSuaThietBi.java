/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author lehuu
 */
public class NhanVienSuaThietBi {
    private int id;
    private LocalDateTime ngaySua;
    private double chiPhi;
    private String tenThietBi;
    private String tenNV;
    
    public NhanVienSuaThietBi(int id, LocalDateTime ngaySua, String tenThietBi, String tenNV) {
        this.id = id;
        this.ngaySua = ngaySua;
        this.tenThietBi = tenThietBi;
        this.tenNV = tenNV;
    };
    /**
     * @return the chiPhi
     */
    public double getChiPhi() {
        return chiPhi;
    }

    /**
     * @param chiPhi the chiPhi to set
     */
    public void setChiPhi(double chiPhi) {
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
}
