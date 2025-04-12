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
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
    @FXML
    Button btnCreateSchedule;
    @FXML
    Button btnUpdateSchedule;
    private static final NhanVienSuaChuaServices employeeService = new NhanVienSuaChuaServices();
    private static final ThietBiServices equipmentService = new ThietBiServices();
    private static final Utils alert = null;
    private static final BaoTriServices maintenanceService = new BaoTriServices();
    private LocalDate selectedDate;
    private int idMaintenance;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17, 9));
        btnUpdateSchedule.setDisable(true);
        loadDataMaintenance();
        loadDataEmployee();
        loadColumn();
        selectItemTableView();
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
        if(txtDeviceCode.getText().isEmpty() || txtName.getText().isEmpty()) {
            alert.getAlert("Vui lòng điền đầy đủ thông tin").show();
            return;
        }
        
        LocalDate date = maintenanceDate.getValue();
        LocalDateTime ngayBaoTri = date.atTime(hourSpinner.getValue(), 0);
        try {
            int idNhanVien = employeeService.getIdEmployee(cbEmployee.getValue().getTenNV());
            LocalDateTime ngayLapLich = LocalDateTime.now();
            int idThietBi = equipmentService.getIdEquipment(txtName.getText());

            maintenanceService.validateAddMaintenanceSchedule(ngayLapLich, ngayBaoTri, idThietBi, idNhanVien);

            maintenanceService.addMaintenanceSchedule(ngayLapLich, ngayBaoTri, idThietBi, idNhanVien);
            alert.getAlert("Lưu thành công!").show();

            equipmentService.addNotification(idThietBi, "");
            loadDataMaintenance();
            resetInputData();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resetInputData() {
        txtDeviceCode.setText("");
        txtName.setText("");
        maintenanceDate.setValue(null);
        cbEmployee.setValue(null);
    }

    public void loadColumn() {
        TableColumn colDeviceCode = new TableColumn("Mã bảo trì");
        colDeviceCode.setCellValueFactory(new PropertyValueFactory("id"));
        colDeviceCode.setPrefWidth(130);

        TableColumn colName = new TableColumn("Tên thiết bị");
        colName.setCellValueFactory(new PropertyValueFactory("tenThietBi"));
        colName.setPrefWidth(130);

        TableColumn colScheduleDate = new TableColumn("Ngày lập lịch");
        colScheduleDate.setCellValueFactory(new PropertyValueFactory("ngayLapLich"));
        colScheduleDate.setPrefWidth(130);

        TableColumn colMaintenanceDate = new TableColumn("Ngày bảo trì");
        colMaintenanceDate.setCellValueFactory(new PropertyValueFactory("ngayBaoTri"));
        colMaintenanceDate.setPrefWidth(130);

        TableColumn colEmployee = new TableColumn("Người thực hiện");
        colEmployee.setCellValueFactory(new PropertyValueFactory("tenNV"));
        colEmployee.setPrefWidth(130);

        tbMaintenance.getColumns().addAll(colDeviceCode, colName, colScheduleDate, colMaintenanceDate, colEmployee);
    }

    public void selectItemTableView() {
        tbMaintenance.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                BaoTri selectedItem = tbMaintenance.getSelectionModel().getSelectedItem();
                selectedDate = selectedItem.getNgayBaoTri().toLocalDate();
                try {
                    maintenanceService.checkLastTwoDaysUpdate(selectedDate);

                    btnCreateSchedule.setDisable(true);
                    btnUpdateSchedule.setDisable(false);
                    maintenanceDate.setDisable(true);
                    idMaintenance = selectedItem.getId();
                    LocalDate storeDate = selectedItem.getNgayBaoTri().toLocalDate();
                    LocalTime storeTime = selectedItem.getNgayBaoTri().toLocalTime();
                    hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17, storeTime.getHour()));
                    maintenanceDate.setValue(storeDate);
                    NhanVienSuaChua nv = new NhanVienSuaChua(selectedItem.getTenNV());
                    cbEmployee.setValue(nv);
                    String tenThietBi = selectedItem.getTenThietBi();
                    txtName.setText(tenThietBi);
                    txtDeviceCode.setText(String.valueOf(equipmentService.getIdEquipment(tenThietBi)));
                } catch (IllegalArgumentException ex) {
                    alert.getAlert(ex.getMessage()).show();
                } catch (SQLException ex) {
                    Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                resetInputData();
                btnUpdateSchedule.setDisable(true);
                btnCreateSchedule.setDisable(false);
                maintenanceDate.setDisable(false);
            }
        }
        );

        tbMaintenance.getSelectionModel()
                .selectionModeProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == null || oldValue != newValue) {
                        resetInputData();
                        btnUpdateSchedule.setDisable(false);
                        btnCreateSchedule.setDisable(true);
                    }
                }
                );
    }

    public void updateScheduleMaintenance(ActionEvent e) {
        int idThietBi = Integer.parseInt(txtDeviceCode.getText());
        try {
            int idNhanVien = employeeService.getIdEmployee(cbEmployee.getValue().toString());
            LocalDate date = maintenanceDate.getValue();
            LocalTime time = LocalTime.of(hourSpinner.getValue(), 0);
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            maintenanceService.validateUpdateScheduleMaintenance(idMaintenance, dateTime, idNhanVien);
            maintenanceService.updateScheduleMaintenance(idMaintenance, idNhanVien);
            alert.getAlert("Cập nhật thành công!").show();
            btnUpdateSchedule.setDisable(true);
            btnCreateSchedule.setDisable(false);
            loadDataMaintenance();
            resetInputData();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();

        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void switchTabEquipment(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachThietBi.fxml");
    }

    public void switchTabFix(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "LichSuaChua.fxml");
    }

    public void switchTabEmployee(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachNhanVien.fxml");
    }

    public void switchTabReceipt(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "ThanhToan.fxml");
    }

    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }

}
