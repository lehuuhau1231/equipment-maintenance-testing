/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.NhanVienSuaThietBi;
import com.qhuong.pojo.ThietBi;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 *
 * @author lehuu
 */
public class ThanhToanController implements Initializable {

    @FXML
    private TableView<ThietBi> tbReceipt;
    @FXML
    private TextField txtDeviceCode;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmployee;
    @FXML
    private DatePicker repairDate;
    @FXML
    private TextField txtDescription;
    @FXML
    private Spinner<Double> priceSpinner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setDeviceData(NhanVienSuaThietBi t) {
        txtDeviceCode.setText(String.valueOf(t.getId()));
        txtName.setText(t.getTenThietBi());
        txtEmployee.setText(t.getTenNV());
        repairDate.setValue(t.getNgaySua().toLocalDate());
    }
    
    public void switchTabMaintenance(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "LichBaoTri.fxml");
    }
    
    public void switchTabFix(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "LichSuaChua.fxml");
    }
    
    public void switchTabEmployee(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachNhanVien.fxml");
    }

    public void switchTabEquipment(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachThietBi.fxml");
    }
}
