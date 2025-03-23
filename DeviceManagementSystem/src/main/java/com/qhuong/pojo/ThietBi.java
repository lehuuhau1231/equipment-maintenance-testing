/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

/**
 *
 * @author lehuu
 */
public class ThietBi {
    private int id;
    private String tenThietBi;
    private String trangThai;
    private boolean thanhLy;
    private int idAdmin;
    
    public ThietBi() {};

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
     * @return the trangThai
     */
    public String getTrangThai() {
        return trangThai;
    }

    /**
     * @param trangThai the trangThai to set
     */
    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    /**
     * @return the thanhLy
     */
    public boolean isThanhLy() {
        return thanhLy;
    }

    /**
     * @param thanhLy the thanhLy to set
     */
    public void setThanhLy(boolean thanhLy) {
        this.thanhLy = thanhLy;
    }
}
