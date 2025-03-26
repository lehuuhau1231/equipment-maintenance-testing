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
public class ThietBi {
    private int id;
    private String tenThietBi;
    private Date ngayNhap;
    private int idTrangThai;
    private Date ngayThanhLy;
    private int idAdmin;
    
    public ThietBi() {};
    
    public ThietBi(int id, String tenThietBi, Date ngayNhap, int idTrangThai, Date ngayThanhLy) {
        this.id = id;
        this.tenThietBi = tenThietBi;
        this.idTrangThai = idTrangThai;
        this.ngayNhap = ngayNhap;
        this.ngayThanhLy = ngayThanhLy;
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
     * @return the idTrangThai
     */
    public int getIdTrangThai() {
        return idTrangThai;
    }

    /**
     * @param idTrangThai the idTrangThai to set
     */
    public void setIdTrangThai(int idTrangThai) {
        this.idTrangThai = idTrangThai;
    }

    /**
     * @return the thanhLy
     */
    public Date getNgayThanhLy() {
        return ngayThanhLy;
    }

    /**
     * @param ngayThanhLy the thanhLy to set
     */
    public void setNgayThanhLy(Date ngayThanhLy) {
        this.ngayThanhLy = ngayThanhLy;
    }

    /**
     * @return the ngayNhap
     */
    public Date getNgayNhap() {
        return ngayNhap;
    }

    /**
     * @param ngayNhap the ngayNhap to set
     */
    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    /**
     * @return the idAdmin
     */
    public int getIdAdmin() {
        return idAdmin;
    }

    /**
     * @param idAdmin the idAdmin to set
     */
    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }
}
