/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

import java.sql.Date;

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
    private String thongBao;
    private int idAdmin;
    
    public ThietBi() {};
    
    public ThietBi(int id, String tenThietBi, Date ngayNhap, int idTrangThai, Date ngayThanhLy, String thongBao) {
        this.id = id;
        this.tenThietBi = tenThietBi;
        this.idTrangThai = idTrangThai;
        this.ngayNhap = ngayNhap;
        this.ngayThanhLy = ngayThanhLy;
        this.thongBao = thongBao;
    }
    
    public ThietBi(int id, Date ngayNhap) {
        this.id = id;
        this.ngayNhap = ngayNhap;
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

    /**
     * @return the thongBao
     */
    public String getThongBao() {
        return thongBao;
    }

    /**
     * @param thongBao the thongBao to set
     */
    public void setThongBao(String thongBao) {
        this.thongBao = thongBao;
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
     * @return the ngayThanhLy
     */
    public Date getNgayThanhLy() {
        return ngayThanhLy;
    }

    /**
     * @param ngayThanhLy the ngayThanhLy to set
     */
    public void setNgayThanhLy(Date ngayThanhLy) {
        this.ngayThanhLy = ngayThanhLy;
    }

}
