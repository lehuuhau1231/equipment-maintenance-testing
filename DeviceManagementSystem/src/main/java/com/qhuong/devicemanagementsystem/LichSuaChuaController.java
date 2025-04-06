/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.NhanVienSuaChua;
import com.qhuong.pojo.NhanVienSuaThietBi;
import com.qhuong.pojo.ThietBi;
import com.qhuong.services.NhanVienSuaChuaServices;
import com.qhuong.services.NhanVienSuaThietBiServices;
import com.qhuong.services.ThietBiServices;
import com.qhuong.services.TrangThaiServices;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author PC
 */
public class LichSuaChuaController implements Initializable {

    @FXML
    private TableView<NhanVienSuaThietBi> tbRepair;
    @FXML
    private ComboBox<NhanVienSuaChua> cbEmployee;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtDeviceCode;
    @FXML
    private DatePicker repairDate;
    @FXML
    private Spinner<Integer> hourSpinner;
    private static final NhanVienSuaThietBiServices repairService = new NhanVienSuaThietBiServices();
    private static final NhanVienSuaChuaServices employeeService = new NhanVienSuaChuaServices();
    private static final ThietBiServices equipmentService = new ThietBiServices();
    private static final Utils alert = new Utils();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(9, 17, 9));
        loadColumn();
        loadDataRepair();
        loadEmployee();
    }

    public void setDeviceData(ThietBi t) {
        txtDeviceCode.setText(String.valueOf(t.getId()));
        txtName.setText(t.getTenThietBi());
    }

    public void loadDataRepair() {
        try {
            tbRepair.setItems(FXCollections.observableList(repairService.getNhanVienSuaThietBi(false)));
        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadEmployee() {
        try {
            cbEmployee.setItems(FXCollections.observableList(employeeService.getNhanVien()));
        } catch (SQLException ex) {
            Logger.getLogger(LichSuaChuaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumn() {
        TableColumn colRepairCode = new TableColumn("Mã sửa chữa");
        colRepairCode.setCellValueFactory(new PropertyValueFactory("id"));
        colRepairCode.setPrefWidth(130);

        TableColumn colName = new TableColumn("Tên thiết bị");
        colName.setCellValueFactory(new PropertyValueFactory("tenThietBi"));
        colName.setPrefWidth(130);

        TableColumn colRepairDate = new TableColumn("Ngày sửa chữa");
        colRepairDate.setCellValueFactory(new PropertyValueFactory("ngaySua"));
        colRepairDate.setPrefWidth(130);

        TableColumn colEmployee = new TableColumn("Người thực hiện");
        colEmployee.setCellValueFactory(new PropertyValueFactory("tenNV"));
        colEmployee.setPrefWidth(130);

        TableColumn colPayment = new TableColumn("Thanh toán");
        colPayment.setPrefWidth(100);
        colPayment.setCellFactory(e -> new TableCell<NhanVienSuaThietBi, Void>() {
            Button btn = new Button("Thanh toán");
            
            {
                btn.setOnAction(evt -> {
                    try {
                        NhanVienSuaThietBi t = getTableView().getItems().get(getIndex());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("ThanhToan.fxml"));
                        
                        Parent receipt = loader.load();

                        ThanhToanController controller = loader.getController();
                        controller.setDeviceData(t);

                        Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
                        Scene receiptScene = new Scene(receipt);
                        stage.setScene(receiptScene);
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }

            TableCell cell = new TableCell();

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); // Ẩn nút nếu dòng trống
                } else {
                    setGraphic(btn);
                }
            }
        });

        tbRepair.getColumns().addAll(colRepairCode, colName, colRepairDate, colEmployee, colPayment);
    }

    public void saveRepairSchedule(ActionEvent e) {
        if (txtDeviceCode.getText().equals("") || txtName.getText().equals("") || repairDate.getValue() == null || cbEmployee.getValue() == null) {
            alert.getAlert("Vui lòng điền đầy đủ thông tin").show();
            return;
        }

        int idNhanVien = - 1;
        LocalDate repairValue = repairDate.getValue();
        LocalTime hourValue = LocalTime.of(hourSpinner.getValue(), 0);
        try {
            idNhanVien = employeeService.getIdEmployee(cbEmployee.getValue().getTenNV());
        } catch (SQLException ex) {
            Logger.getLogger(LichSuaChuaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (checkSameTime(idNhanVien, repairValue, hourValue) == false) {
            alert.getAlert("Lỗi! Nhân viên làm trùng giờ").show();
            return;
        }

        if (checkEmployeeWorkload(idNhanVien, repairValue, hourValue) == false) {
            alert.getAlert("Nhân viên chỉ được làm tối đa 3 công việc 1 ngày").show();
            return;
        }

        LocalDate dateNow = LocalDate.now();
        if (repairValue.isAfter(dateNow.plusDays(-1)) && repairValue.isBefore(dateNow.plusDays(4)) == true) {
            int idThietBi;
            try {
                idThietBi = equipmentService.getIdEquipment(txtName.getText());
                LocalDateTime ngaySua = repairValue.atTime(hourValue);
                repairService.updateRepairSchedule(ngaySua, idThietBi, idNhanVien);
                alert.getAlert("Lập lịch sửa chữa thành công!").show();
                
                TrangThaiServices statusService = new TrangThaiServices();
                int idTrangThai = statusService.getIdStatus("Đang sửa");
                equipmentService.updateStatus(Integer.parseInt(txtDeviceCode.getText()), idTrangThai);
                
                loadDataRepair();
                txtDeviceCode.setText("");
                txtName.setText("");
                repairDate.setValue(null);
                cbEmployee.setValue(null);
            } catch (SQLException ex) {
                Logger.getLogger(LichSuaChuaController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            alert.getAlert("Ngày sửa phải nằm trong 3 ngày kể từ ngày hiện tại").show();
        }

    }

    public boolean checkSameTime(int idNhanVien, LocalDate date, LocalTime time) {
        List<LocalDateTime> dateTime;
        try {
            dateTime = repairService.getListDateTime(idNhanVien);
            long count = dateTime.stream().filter(t -> t.toLocalDate().equals(date) && t.toLocalTime().equals(time)).count();
            return count == 0;
        } catch (SQLException ex) {
            Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean checkEmployeeWorkload(int idNhanVien, LocalDate date, LocalTime time) {
        if (idNhanVien > 0) {
            try {
                List<LocalDateTime> dateTime = repairService.getListDateTime(idNhanVien);
                long count = dateTime.stream().filter(t -> t.toLocalDate().equals(date)).count();
                return count <= 2;
            } catch (SQLException ex) {
                Logger.getLogger(LichBaoTriController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }

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
    
    public void switchTabReceipt(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "ThanhToan.fxml");
    }
    
    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
