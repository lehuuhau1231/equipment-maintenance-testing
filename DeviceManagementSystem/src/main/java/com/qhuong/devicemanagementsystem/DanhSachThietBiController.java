/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.BaoTri;
import com.qhuong.pojo.NhanVienSuaThietBi;
import com.qhuong.pojo.ThietBi;
import com.qhuong.pojo.TrangThai;
import com.qhuong.services.BaoTriServices;
import com.qhuong.services.NhanVienSuaThietBiServices;
import com.qhuong.services.ThietBiServices;
import com.qhuong.services.TrangThaiServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author PC
 */
public class DanhSachThietBiController implements Initializable {

    @FXML
    private TableView<ThietBi> tbEquipment;
    @FXML
    private ComboBox<TrangThai> cbStatus;
    @FXML
    private TextField txtName;
    @FXML
    private DatePicker importDate;
    @FXML
    private DatePicker disposalDate;
    @FXML
    Button btnAddEquipment;
    @FXML
    Button btnUpdateEquipment;

    private Map<Integer, String> statusMap;
    private static TrangThaiServices status = new TrangThaiServices();
    private static ThietBiServices equipment = new ThietBiServices();
    private static Utils alert = new Utils();
    private String tenTrangThaiDaThanhLy;
    private int idEquipment;
    private BaoTriServices maintenanceServices = new BaoTriServices();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            tenTrangThaiDaThanhLy = status.getTrangThaiDaThanhLy();
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            statusMap = status.getStatusMap();
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }

        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\p{L}\\p{N} ]*")) {
                txtName.setText(oldValue);
            }
        });
        btnUpdateEquipment.setDisable(true);
        LocalDate nowDate = LocalDate.now();
        try {
            List<BaoTri> list = maintenanceServices.getListMaintenanceDate();
            for (BaoTri b : list) {
                if (b.getNgayBaoTri().toLocalDate().equals(nowDate)) {
                    equipment.updateStatus(b.getIdThietbi(), status.getIdStatus("Bảo trì"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateRepairStatus();
        updateMantenanceNotification();
        loadStatus(false, false);
        loadColumn();
        loadData();
        selectItemTableView();
        comboBoxChange();
    }

    public void updateMantenanceNotification() {
        List<ThietBi> devices;
        try {
            devices = equipment.getImportDateEquipment();
            for (ThietBi t : devices) {
                int maintenanceCount = maintenanceServices.getMaintenanceTimes(t.getId());
                LocalDate nowDate = LocalDate.now();
                if (maintenanceCount == 0) {
                    LocalDate threeMonthsAfterImport = t.getNgayNhap().toLocalDate().plusMonths(3);
                    if (nowDate.isAfter(threeMonthsAfterImport.minusWeeks(1)) && nowDate.isBefore(threeMonthsAfterImport)) {
                        equipment.addNotification(t.getId(), "Lập lịch bảo trì lần 1");
                    }
                } else if (maintenanceCount == 1) {
                    LocalDateTime firstMaintenanceDate = maintenanceServices.getMaintenanceDate(t.getId());
                    if (firstMaintenanceDate != null) {
                        LocalDate threeMonthsAfterFirst = firstMaintenanceDate.toLocalDate().plusMonths(3);
                        if (nowDate.isAfter(threeMonthsAfterFirst.minusWeeks(1)) && nowDate.isBefore(threeMonthsAfterFirst)) {
                            equipment.addNotification(t.getId(), "Lập lịch bảo trì lần 2");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateRepairStatus() {
        NhanVienSuaThietBiServices repairService = new NhanVienSuaThietBiServices();
        try {
            List<NhanVienSuaThietBi> repairs  = repairService.getListNotRepair();
            int idTrangThai = status.getIdStatus("Đang sửa");
            for(NhanVienSuaThietBi r : repairs)
                if(r.getNgaySua().toLocalDate().equals(LocalDate.now()))
                    equipment.updateStatus(r.getIdThietBi(), idTrangThai);
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void loadData() {
        try {
            tbEquipment.setItems(FXCollections.observableList(equipment.getThietBi()));
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadStatus(boolean getFull, boolean getDamageStatus) {
        try {
            cbStatus.setItems(FXCollections.observableList(status.getTrangThai(getFull, getDamageStatus)));
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadColumn() {
        TableColumn colDeviceCode = new TableColumn("Mã thiết bị");
        colDeviceCode.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colName = new TableColumn("Tên thiết bị");
        colName.setPrefWidth(130);
        colName.setCellValueFactory(new PropertyValueFactory("tenThietBi"));

        TableColumn colImportDate = new TableColumn("Ngày nhập");
        colImportDate.setPrefWidth(130);
        colImportDate.setCellValueFactory(new PropertyValueFactory("ngayNhap"));

        TableColumn colDisposalDate = new TableColumn("Ngày thanh lý");
        colDisposalDate.setPrefWidth(130);
        colDisposalDate.setCellValueFactory(new PropertyValueFactory("ngayThanhLy"));

        TableColumn<ThietBi, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setPrefWidth(130);
        colStatus.setCellValueFactory(cellData -> {
            int idThietBi = cellData.getValue().getIdTrangThai();
            String trangThai = statusMap.get(idThietBi);
            return new SimpleStringProperty(trangThai);
        });

        TableColumn colNotification = new TableColumn("Thông báo");
        colNotification.setPrefWidth(130);
        colNotification.setCellValueFactory(new PropertyValueFactory("thongBao"));

        TableColumn colAction = new TableColumn("Lập lịch");
        colAction.setPrefWidth(180);
        colAction.setCellFactory(e -> new TableCell<ThietBi, Void>() {
            Button btnMaintenance = new Button("Bảo trì");
            Button btnFix = new Button("Sửa chữa");
            HBox hbox = new HBox(10, btnMaintenance, btnFix);

            {
                btnMaintenance.setOnAction(evt -> {
                    try {
                        ThietBi t = getTableView().getItems().get(getIndex());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("LichBaoTri.fxml"));
                        Parent maintenance = loader.load();

                        LichBaoTriController controller = loader.getController();
                        controller.setDeviceData(t);

                        Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
                        Scene maintenanceScene = new Scene(maintenance);
                        stage.setScene(maintenanceScene);
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                btnFix.setOnAction(evt -> {
                    try {
                        ThietBi t = getTableView().getItems().get(getIndex());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("LichSuaChua.fxml"));
                        Parent fix = loader.load();

                        LichSuaChuaController controller = loader.getController();
                        controller.setDeviceData(t);

                        Scene fixScene = new Scene(fix);
                        Stage stage = (Stage) ((Node) evt.getSource()).getScene().getWindow();
                        stage.setScene(fixScene);
                        stage.show();
                    } catch (IOException ex) {
                        Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); // Ẩn nút nếu dòng trống
                } else {
                    try {
                        ThietBi t = getTableView().getItems().get(getIndex());
                        String trangThai = statusMap.get(t.getIdTrangThai());
                        if ("Đang hoạt động".equals(trangThai) == false || maintenanceServices.getMaintenanceTimes(t.getId()) == 2) {
                            btnMaintenance.setDisable(true);
                        } else {
                            btnMaintenance.setDisable(false);
                        }

                        NhanVienSuaThietBiServices repairService = new NhanVienSuaThietBiServices();
                        if ("Hỏng hóc".equals(trangThai)) {
                            btnFix.setDisable(false);
                        } else {
                            btnFix.setDisable(true);
                        }
                        setGraphic(hbox); // Hiển thị nếu có dữ liệu
                    } catch (SQLException ex) {
                        Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        tbEquipment.getColumns().addAll(colDeviceCode, colName, colImportDate, colDisposalDate, colStatus, colAction, colNotification);
    }

    public void addEquipment(ActionEvent e) {
        try {
            int idTrangThai = getValueStatusMap();
            equipment.validateAddThietBi(txtName.getText(), importDate.getValue(), idTrangThai);
            equipment.addThietBi(txtName.getText(), importDate.getValue(), idTrangThai);
            alert.getAlert("Thêm thiết bị thành công!").show();
            resetInputData();
            loadData();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (IllegalStateException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, "Lỗi hệ thống: " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void selectItemTableView() {
        tbEquipment.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                ThietBi selectedItem = tbEquipment.getSelectionModel().getSelectedItem();
                if (selectedItem.getNgayThanhLy() != null) {
                    alert.getAlert("Không được cập nhật thiết bị ĐÃ THANH LÝ!").show();
                    return;
                } else if (selectedItem.getIdTrangThai() == 3) {
                    alert.getAlert("Không được cập nhật thiết bị ĐANG SỬA!").show();
                    return;
                } else if (selectedItem != null) {
                    btnAddEquipment.setDisable(true);
                    btnUpdateEquipment.setDisable(false);
                    idEquipment = selectedItem.getId();
                    txtName.setText(selectedItem.getTenThietBi());
                    TrangThai t = new TrangThai(statusMap.get(selectedItem.getIdTrangThai()));
                    cbStatus.setValue(t);
                    LocalDate ngayNhap = selectedItem.getNgayNhap().toLocalDate();
                    importDate.setValue(ngayNhap);
                    if (selectedItem.getIdTrangThai() == 4) {
                        loadStatus(true, true);
                    } else {
                        loadStatus(true, false);
                    }
                }
            } else {
                btnUpdateEquipment.setDisable(true);
                btnAddEquipment.setDisable(false);
            }
        });

        tbEquipment.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || oldValue != newValue) {
                resetInputData();
                loadStatus(false, false);
                btnAddEquipment.setDisable(false);
                btnUpdateEquipment.setDisable(true);
            }
        });
    }

    public void updateEquipment(ActionEvent e) {
        int idTrangThai = getValueStatusMap();
        try {
            equipment.validateUpdateThietBi(idEquipment, txtName.getText(), importDate.getValue(), disposalDate.getValue(), idTrangThai);
            equipment.updateThietBi(idEquipment, txtName.getText(), importDate.getValue(), disposalDate.getValue(), idTrangThai);
            alert.getAlert("Cập nhật thông tin thành công").show();
            resetInputData();
            loadData();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resetInputData() {
        txtName.clear();
        importDate.setValue(null);
        cbStatus.setValue(null);
        if (disposalDate.getValue() != null) {
            disposalDate.setValue(null);
        }
    }

    public void comboBoxChange() {
        cbStatus.setOnAction(event -> {
            if (cbStatus.getValue() != null && cbStatus.getValue().toString().equals(tenTrangThaiDaThanhLy)) {
                disposalDate.setDisable(false);
            } else {
                disposalDate.setDisable(true);
            }
        });
    }

    public int getValueStatusMap() {
        int idTrangThai = 0;
        for (Integer i : statusMap.keySet()) {
            if (statusMap.get(i).trim().equals(String.valueOf(cbStatus.getValue()).trim())) {
                idTrangThai = i;
                break;
            }
        }
        return idTrangThai;
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

    public void switchTabReceipt(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "ThanhToan.fxml");
    }

    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
