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
public class BaoTri {
    private int id;
    private LocalDateTime ngayLapLich;
    private LocalDateTime ngayBaoTri;
    private String tenThietBi;
    private String tenNV;
    
    public BaoTri(int id, LocalDateTime ngayLapLich, LocalDateTime ngayBaoTri, String idThietBi, String idNhanVien){
        this.id = id;
        this.ngayLapLich = ngayLapLich;
        this.ngayBaoTri = ngayBaoTri;
        this.tenThietBi = idThietBi;
        this.tenNV = idNhanVien;
    };

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
     * @return the ngayBaoTri
     */
    public LocalDateTime getNgayBaoTri() {
        return ngayBaoTri;
    }

    /**
     * @param ngayBaoTri the ngayBaoTri to set
     */
    public void setNgayBaoTri(LocalDateTime ngayBaoTri) {
        this.ngayBaoTri = ngayBaoTri;
    }

    /**
     * @return the idThietBi
     */
    public String getTenThietBi() {
        return tenThietBi;
    }

    /**
     * @param tenThietBi the idThietBi to set
     */
    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

    /**
     * @return the idNhanVien
     */
    public String getTenNV() {
        return tenNV;
    }

    /**
     * @param tenNV the idNhanVien to set
     */
    public void setIdNhanVien(String tenNV) {
        this.tenNV = tenNV;
    }

    /**
     * @return the ngayLapLich
     */
    public LocalDateTime getNgayLapLich() {
        return ngayLapLich;
    }

    /**
     * @param ngayLapLich the ngayLapLich to set
     */
    public void setNgayLapLich(LocalDateTime ngayLapLich) {
        this.ngayLapLich = ngayLapLich;
    }
    
}
