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
    private int idThietBi;
    private int idNhanVien;
    
    public BaoTri(int id, LocalDateTime ngayLapLich, LocalDateTime ngayBaoTri, String idThietBi, String idNhanVien){
        this.id = id;
        this.ngayLapLich = ngayLapLich;
        this.ngayBaoTri = ngayBaoTri;
        this.tenThietBi = idThietBi;
        this.tenNV = idNhanVien;
    };
    
    public BaoTri(int id, LocalDateTime ngayBaotri, int idThietBi, int idNhanVien) {
        this.id = id;
        this.ngayBaoTri = ngayBaotri;
        this.idThietBi = idThietBi;
        this.idNhanVien = idNhanVien;
    }
    
    public BaoTri(LocalDateTime ngayBaotri, int idThietBi) {
        this.ngayBaoTri = ngayBaotri;
        this.idThietBi = idThietBi;
    }
    
    public BaoTri(LocalDateTime ngayBaotri, LocalDateTime ngayLapLich, int idThietBi) {
        this.ngayBaoTri = ngayBaotri;
        this.ngayLapLich = ngayLapLich;
        this.idThietBi = idThietBi;
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
        this.setTenNV(tenNV);
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

    /**
     * @param tenNV the tenNV to set
     */
    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    /**
     * @return the idNhanVien
     */
    public int getIdNhanVien() {
        return idNhanVien;
    }

    /**
     * @param idNhanVien the idNhanVien to set
     */
    public void setIdNhanVien(int idNhanVien) {
        this.idNhanVien = idNhanVien;
    }

    /**
     * @return the idThietBi
     */
    public int getIdThietBi() {
        return idThietBi;
    }

    /**
     * @param idThietBi the idThietBi to set
     */
    public void setIdThietBi(int idThietBi) {
        this.idThietBi = idThietBi;
    }
    
}
