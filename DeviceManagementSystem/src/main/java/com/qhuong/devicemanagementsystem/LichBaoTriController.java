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
import java.util.List;
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
        if (maintenanceDate.getValue() != null && cbEmployee.getValue() != null && txtDeviceCode.getText().equals("") == false && txtName.getText().equals("") == false) {
            LocalDate d = null;
            try {
                d = maintenanceService.getDateTime(Integer.parseInt(txtDeviceCode.getText())).toLocalDate();
            } catch (SQLException ex) {
                Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
            }
            LocalDate dateNow = (d != null) ? d : LocalDate.now();
            System.out.println(dateNow);
            if (maintenanceDate.getValue().isAfter(dateNow.plusMonths(6)) && maintenanceDate.getValue().isBefore(dateNow.plusYears(1))) {
                LocalDate date = maintenanceDate.getValue();
                LocalDateTime ngayBaoTri = date.atTime(hourSpinner.getValue(), 0);
                int idNhanVien = -1;
                try {
                    idNhanVien = employeeService.getIdEmployee(cbEmployee.getValue().getTenNV());
                } catch (SQLException ex) {
                    Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (checkEmployeeWorkload(idNhanVien, date, LocalTime.of(hourSpinner.getValue(), 0)) == true) {
                    if (checkSameTime(idNhanVien, date, LocalTime.of(hourSpinner.getValue(), 0)) == true) {
                        try {
                            int idThietBi = equipmentService.getIdEquipment(txtName.getText());
                            maintenanceService.addMaintenanceSchedule(ngayBaoTri, idThietBi, idNhanVien);
                            alert.getAlert("Lưu thành công!").show();
                            loadDataMaintenance();
                        } catch (SQLException ex) {
                            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        alert.getAlert("Lỗi! Nhân viên làm trùng giờ").show();
                    }
                } else {
                    alert.getAlert("Nhân viên chỉ được làm tối đa 3 công việc 1 ngày").show();
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

    public boolean checkEmployeeWorkload(int id, LocalDate date, LocalTime time) {
        if (id > 0) {
            try {
                List<LocalDateTime> dateTime = maintenanceService.getListDateTime(id);
                long count = dateTime.stream().filter(t -> t.toLocalDate().equals(date) && t.toLocalTime().equals(time)).count();
                return count <= 2;
            } catch (SQLException ex) {
                Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }

    public boolean checkSameTime(int id, LocalDate date, LocalTime time) {
        List<LocalDateTime> dateTime;
        try {
            dateTime = maintenanceService.getListDateTime(id);
            long count = dateTime.stream().filter(t -> t.toLocalDate().equals(date) && t.toLocalTime().equals(time)).count();
            return count == 0;
        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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

}
