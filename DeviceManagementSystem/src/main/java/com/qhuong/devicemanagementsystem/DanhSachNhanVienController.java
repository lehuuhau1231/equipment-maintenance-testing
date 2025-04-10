/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.NhanVienSuaChua;
import com.qhuong.services.NhanVienSuaChuaServices;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author PC
 */
public class DanhSachNhanVienController implements Initializable {

    @FXML
    private TableView<NhanVienSuaChua> tbEmployee;
    @FXML
    private TextField txtName;
    @FXML
    TextField txtID;
    @FXML
    TextField txtPhoneNumber;
    @FXML
    TextField txtAddress;
    @FXML
    TextField txtEmail;
    @FXML
    private DatePicker birthDate;
    @FXML
    private Button btnAddEmployee = new Button();
    @FXML
    private Button btnUpdateEmployee = new Button();
    private Utils alert = new Utils();
    private int idSelected;
    private NhanVienSuaChuaServices employeeService = new NhanVienSuaChuaServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L}\\p{N} ]*")) {
                txtName.setText(oldValue);
            }
        });

        txtPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,10}")) {
                txtPhoneNumber.setText(oldValue);
            }
        });

        txtID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,12}")) {
                txtID.setText(oldValue);
            }
        });

        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\w\\@\\.]*")) {
                txtEmail.setText(oldValue);
            }
        });

        txtAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L}\\p{N}\\, ]*")) {
                txtAddress.setText(oldValue);
            }
        });
        btnUpdateEmployee.setDisable(true);
        loadColumn();
        loadData();
        selectItemTableView();
    }

    public void loadData() {
        try {
            tbEmployee.setItems(FXCollections.observableList(employeeService.getNhanVien()));
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachNhanVienController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumn() {
        TableColumn colEmployeeCode = new TableColumn("Mã nhân viên");
        colEmployeeCode.setCellValueFactory(new PropertyValueFactory("id"));
        colEmployeeCode.setPrefWidth(130);

        TableColumn colName = new TableColumn("Tên nhân viên");
        colName.setCellValueFactory(new PropertyValueFactory("tenNV"));
        colName.setPrefWidth(130);

        TableColumn colBirthDate = new TableColumn("Ngày sinh");
        colBirthDate.setCellValueFactory(new PropertyValueFactory("ngaySinh"));
        colBirthDate.setPrefWidth(130);

        TableColumn colID = new TableColumn("Căn cước công dân");
        colID.setCellValueFactory(new PropertyValueFactory("CCCD"));
        colID.setPrefWidth(130);

        TableColumn colPhoneNumber = new TableColumn("Số điện thoại");
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory("soDT"));
        colPhoneNumber.setPrefWidth(130);

        TableColumn colAddress = new TableColumn("Địa chỉ");
        colAddress.setCellValueFactory(new PropertyValueFactory("diaChi"));
        colAddress.setPrefWidth(130);

        TableColumn colEmail = new TableColumn("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory("email"));
        colEmail.setPrefWidth(130);

        tbEmployee.getColumns().addAll(colEmployeeCode, colName, colBirthDate, colID, colPhoneNumber, colAddress, colEmail);
    }

    public void addEmployee(ActionEvent e) {
        try {
            employeeService.validateEmployeeInput(txtName.getText(), birthDate.getValue(), txtID.getText(), txtPhoneNumber.getText(), txtAddress.getText(), txtEmail.getText());
            employeeService.addEmployee(txtName.getText(), birthDate.getValue(), txtID.getText(), txtPhoneNumber.getText(), txtAddress.getText(), txtEmail.getText());
            alert.getAlert("Thêm thành công!").show();
            btnAddEmployee.setDisable(false);
            btnUpdateEmployee.setDisable(true);
            resetInput();
            loadData();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachNhanVienController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void selectItemTableView() {
        tbEmployee.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                NhanVienSuaChua selectedItem = tbEmployee.getSelectionModel().getSelectedItem();
                idSelected = selectedItem.getId();
                txtName.setText(selectedItem.getTenNV());
                txtID.setText(selectedItem.getCCCD());
                txtPhoneNumber.setText(selectedItem.getSoDT());
                birthDate.setValue(selectedItem.getNgaySinh());
                txtAddress.setText(selectedItem.getDiaChi());
                txtEmail.setText(selectedItem.getEmail());
                btnAddEmployee.setDisable(true);
                btnUpdateEmployee.setDisable(false);
            }
        });

        tbEmployee.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue != oldValue) {
                btnAddEmployee.setDisable(false);
                btnUpdateEmployee.setDisable(true);
                resetInput();
            }
        });
    }

    public void resetInput() {
        txtName.setText("");
        txtID.setText("");
        txtPhoneNumber.setText("");
        birthDate.setValue(null);
        txtAddress.setText("");
        txtEmail.setText("");
    }

    public void updateEmployee() {
        NhanVienSuaChua nv = new NhanVienSuaChua(idSelected, txtName.getText(), birthDate.getValue(), txtID.getText(), txtPhoneNumber.getText(), txtAddress.getText(), txtEmail.getText());
        try {
            employeeService.validateEmployeeInput(txtName.getText(), birthDate.getValue(), txtID.getText(), txtPhoneNumber.getText(), txtAddress.getText(), txtEmail.getText());
            employeeService.updateEmployee(nv);
            alert.getAlert("Cập nhật thành công").show();
            btnAddEmployee.setDisable(false);
            btnUpdateEmployee.setDisable(true);
            loadData();
            resetInput();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachNhanVienController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void switchTabEquipment(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "DanhSachThietBi.fxml");
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
