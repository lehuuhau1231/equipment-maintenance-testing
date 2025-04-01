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
        return this.tenTrangThai;
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
     * @return the trangThai
     */
    public String gettenTrangThai() {
        return tenTrangThai;
    }

    /**
     * @param tenTrangThai the trangThai to set
     */
    public void settenTrangThai(String tenTrangThai) {
        this.tenTrangThai = tenTrangThai;
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
}
