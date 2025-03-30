/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.ThietBi;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author PC
 */
public class LichSuaChuaController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void setDeviceData(ThietBi t) {}
    
    public void switchTabMaintenance(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "LichBaoTri.fxml");
    }
    
    public void switchTabEquipmnt(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachThietBi.fxml");
    }
    
    public void switchTabEmployee(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachNhanVien.fxml");
    }
}
