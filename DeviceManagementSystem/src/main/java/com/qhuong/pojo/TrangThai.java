/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.pojo;

/**
 *
 * @author lehuu
 */
public class TrangThai {
    private int id;
    private String tenTrangThai;
    private ThietBi idThietBi;
    
    public TrangThai(int id, String trangThai){
        this.id = id;
        this.tenTrangThai = trangThai;
    };
    
    public TrangThai( String trangThai){
        this.tenTrangThai = trangThai;
    };


    @Override
    public String toString() {
        return this.getTenTrangThai();
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
     * @return the idThietBi
     */
    public ThietBi getIdThietBi() {
        return idThietBi;
    }

    /**
     * @param idThietBi the idThietBi to set
     */
    public void setIdThietBi(ThietBi idThietBi) {
        this.idThietBi = idThietBi;
    }

    /**
     * @return the tenTrangThai
     */
    public String getTenTrangThai() {
        return tenTrangThai;
    }

    /**
     * @param tenTrangThai the tenTrangThai to set
     */
    public void setTenTrangThai(String tenTrangThai) {
        this.tenTrangThai = tenTrangThai;
    }
}
