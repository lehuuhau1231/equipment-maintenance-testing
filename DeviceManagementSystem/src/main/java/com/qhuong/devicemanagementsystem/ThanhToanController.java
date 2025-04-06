/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.NhanVienSuaThietBi;
import com.qhuong.services.NhanVienSuaChuaServices;
import com.qhuong.services.NhanVienSuaThietBiServices;
import com.qhuong.services.ThietBiServices;
import com.qhuong.services.TrangThaiServices;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author lehuu
 */
public class ThanhToanController implements Initializable {

    @FXML
    private TableView<NhanVienSuaThietBi> tbReceipt;
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
    private TextField txtPrice;
    @FXML
    private TextField txtHour;

    private int idReceipt;
    private int idThietBi;
    private static final Utils alert = new Utils();
    private static final ThietBiServices equipmentService = new ThietBiServices();
    private static final NhanVienSuaChuaServices employeeService = new NhanVienSuaChuaServices();
    private static final NhanVienSuaThietBiServices repairService = new NhanVienSuaThietBiServices();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtPrice.setText(oldValue);
            }
        });
        txtDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L}\\p{N} ]*")) {
                txtDescription.setText(oldValue);
            }
        });
        loadColumn();
        loadData();
    }

    public void setDeviceData(NhanVienSuaThietBi t) {
        try {
            idThietBi = equipmentService.getIdEquipment(txtName.getText().trim());
        } catch (SQLException ex) {
            Logger.getLogger(ThanhToanController.class.getName()).log(Level.SEVERE, null, ex);
        }
        txtDeviceCode.setText(String.valueOf(idThietBi));
        txtName.setText(t.getTenThietBi());
        txtEmployee.setText(t.getTenNV());
        repairDate.setValue(t.getNgaySua().toLocalDate());
        txtHour.setText(String.valueOf(t.getNgaySua().toLocalTime().getHour()));

        idReceipt = t.getId();
    }

    public void loadData() {
        try {
            tbReceipt.setItems(FXCollections.observableList(repairService.getNhanVienSuaThietBi(true)));
        } catch (SQLException ex) {
            Logger.getLogger(ThanhToanController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumn() {
        TableColumn colReceiptCode = new TableColumn("Mã hóa đơn");
        colReceiptCode.setCellValueFactory(new PropertyValueFactory("id"));
        colReceiptCode.setPrefWidth(130);

        TableColumn colName = new TableColumn("Tên thiết bị");
        colName.setCellValueFactory(new PropertyValueFactory("tenThietBi"));
        colName.setPrefWidth(130);

        TableColumn colRepairDate = new TableColumn("Ngày sửa chữa");
        colRepairDate.setCellValueFactory(new PropertyValueFactory("ngaySua"));
        colRepairDate.setPrefWidth(130);

        TableColumn colEmployee = new TableColumn("Người thực hiện");
        colEmployee.setCellValueFactory(new PropertyValueFactory("tenNV"));
        colEmployee.setPrefWidth(130);

        TableColumn colPrice = new TableColumn("Chi phí");
        colPrice.setCellValueFactory(new PropertyValueFactory("chiPhi"));
        colPrice.setPrefWidth(130);

        TableColumn colDescription = new TableColumn("Mô tả");
        colDescription.setCellValueFactory(new PropertyValueFactory("moTa"));
        colDescription.setPrefWidth(130);

        tbReceipt.getColumns().addAll(colReceiptCode, colName, colRepairDate, colEmployee, colPrice, colDescription);
    }

    public void payment(ActionEvent e) {
        if (txtDeviceCode.getText().equals("") || txtName.getText().equals("")
                || txtEmployee.getText().equals("") || repairDate.getValue() == null
                || txtDescription.getText().equals("") || txtPrice.getText().equals("")) {
            alert.getAlert("Vui lòng điền đầy đủ thông tin").show();
        } else {
            try {
                int idNhanVien = employeeService.getIdEmployee(txtEmployee.getText());
                repairService.updateReceipt(idReceipt, Double.parseDouble(txtPrice.getText()), txtDescription.getText());
                alert.getAlert("Thanh toán thành công!").show();

                TrangThaiServices statusService = new TrangThaiServices();
                int idTrangThai = statusService.getIdStatus("Đang hoạt động");
                equipmentService.updateStatus(idThietBi, idTrangThai);

                loadData();
                txtDeviceCode.setText("");
                txtName.setText("");
                txtEmployee.setText("");
                txtHour.setText("");
                repairDate.setValue(null);
                txtDescription.setText("");
                txtPrice.setText("");
            } catch (SQLException ex) {
                Logger.getLogger(ThanhToanController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
    
    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
