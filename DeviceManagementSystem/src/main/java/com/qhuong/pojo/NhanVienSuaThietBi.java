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
public class NhanVienSuaThietBi {
    private Date ngaySua;
    private double chiPhi;
    private int idThietBi;
    private int idNhanVien;
    
    public NhanVienSuaThietBi() {};

    /**
     * @return the ngaySua
     */
    public Date getNgaySua() {
        return ngaySua;
    }

    /**
     * @param ngaySua the ngaySua to set
     */
    public void setNgaySua(Date ngaySua) {
        this.ngaySua = ngaySua;
    }

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
}
