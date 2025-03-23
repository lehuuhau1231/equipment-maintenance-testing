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
public class BaoTri {
    private int id;
    private Date ngayBaoTri;
    private int idThietBi;
    
    public BaoTri(){};

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
    public Date getNgayBaoTri() {
        return ngayBaoTri;
    }

    /**
     * @param ngayBaoTri the ngayBaoTri to set
     */
    public void setNgayBaoTri(Date ngayBaoTri) {
        this.ngayBaoTri = ngayBaoTri;
    }
    
}
