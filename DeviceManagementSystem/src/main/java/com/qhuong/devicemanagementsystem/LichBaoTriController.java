/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.BaoTri;
import com.qhuong.pojo.NhanVienSuaChua;
import com.qhuong.pojo.ThietBi;
import com.qhuong.services.BaoTriServices;
import com.qhuong.services.NhanVienSuaChuaServices;
import com.qhuong.services.ThietBiServices;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author PC
 */
public class LichBaoTriController implements Initializable {

    @FXML
    private TableView<BaoTri> tbMaintenance;
    @FXML
    private ComboBox<NhanVienSuaChua> cbEmployee;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtDeviceCode;
    @FXML
    private DatePicker maintenanceDate;
    @FXML
    Spinner<Integer> hourSpinner;
    private static final NhanVienSuaChuaServices employeeService = new NhanVienSuaChuaServices();
    private static final ThietBiServices equipmentService = new ThietBiServices();
    private static final Utils alert = null;
    private static final BaoTriServices maintenanceService = new BaoTriServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17, 9));
        loadDataMaintenance();
        loadDataEmployee();
        loadColumn();

    }

    public void setDeviceData(ThietBi t) {
        txtDeviceCode.setText(String.valueOf(t.getId()));
        txtName.setText(t.getTenThietBi());

    }

    public void loadDataMaintenance() {
        try {
            tbMaintenance.setItems(FXCollections.observableList(maintenanceService.getBaoTri()));
        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadDataEmployee() {
        try {
            cbEmployee.setItems(FXCollections.observableList(employeeService.getNhanVien()));
        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createSchedule(ActionEvent e) {
        if (maintenanceDate.getValue() != null || cbEmployee.getValue() != null) {
            if (maintenanceDate.getValue().isAfter(LocalDate.now().plusMonths(6)) && maintenanceDate.getValue().isBefore(LocalDate.now().plusYears(1))) {
                LocalDate date = maintenanceDate.getValue();
                LocalDateTime ngayBaoTri = date.atTime(hourSpinner.getValue(), 0);

                try {
                    int idThietBi = equipmentService.getIdEquipment(txtName.getText());
                    int idNhanVien = employeeService.getIdEmployee(cbEmployee.getValue().getTenNV());
                    maintenanceService.addMaintenanceSchedule(ngayBaoTri, idThietBi, idNhanVien);
                    alert.getAlert("Lưu thành công!").show();
                    loadDataMaintenance();
                } catch (SQLException ex) {
                    Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                alert.getAlert("Vui lòng nhập ngày bảo trì trong khoảng 6 đến 12 tháng").show();
            }
        } else {
            alert.getAlert("Vui lòng điền đầy đủ thông tin").show();
        }
    }

    public void loadColumn() {
        TableColumn colDeviceCode = new TableColumn("Mã thiết bị");
        colDeviceCode.setCellValueFactory(new PropertyValueFactory("id"));
        colDeviceCode.setPrefWidth(130);

        TableColumn colName = new TableColumn("Tên thiết bị");
        colName.setCellValueFactory(new PropertyValueFactory("tenThietBi"));
        colName.setPrefWidth(130);

        TableColumn colMaintenanceDate = new TableColumn("Ngày bảo trì");
        colMaintenanceDate.setCellValueFactory(new PropertyValueFactory("ngayBaoTri"));
        colMaintenanceDate.setPrefWidth(130);

        TableColumn colEmployee = new TableColumn("Người thực hiện");
        colEmployee.setCellValueFactory(new PropertyValueFactory("tenNV"));
        colEmployee.setPrefWidth(130);

        tbMaintenance.getColumns().addAll(colDeviceCode, colName, colMaintenanceDate, colEmployee);
    }

}
