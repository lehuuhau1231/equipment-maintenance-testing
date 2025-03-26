/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.ThietBi;
import com.qhuong.pojo.TrangThai;
import com.qhuong.services.ThietBiServices;
import com.qhuong.services.TrangThaiServices;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

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

    private Map<Integer, String> statusMap;
    private static TrangThaiServices status = new TrangThaiServices();
    private static ThietBiServices equipment = new ThietBiServices();
    private static Utils alert = new Utils();
    private String tenTrangThaiDaThanhLy;
    private int idEquipment;

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

        loadStatus(false);
        loadColumn();
        loadData();
        selectItemTableView();
        comboBoxChange();
    }

    public void loadData() {
        try {
            tbEquipment.setItems(FXCollections.observableList(equipment.getThietBi()));
        } catch (SQLException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadStatus(boolean getFull) {
        try {
            cbStatus.setItems(FXCollections.observableList(status.getTrangThai(getFull)));
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

        TableColumn colAction = new TableColumn("Lập lịch");
        colAction.setPrefWidth(180);
        colAction.setCellFactory(e -> new TableCell<ThietBi, Void>() {
            Button btnMaintenance = new Button("Bảo trì");
            Button btnFix = new Button("Sửa chữa");
            HBox hbox = new HBox(10, btnMaintenance, btnFix);

            TableCell cell = new TableCell();

//            cell.setGraphic(hbox);
//            
//            
//            return cell;
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null); // Ẩn nút nếu dòng trống
                } else {
                    setGraphic(hbox); // Hiển thị nếu có dữ liệu
                }
            }
        });
        tbEquipment.getColumns().addAll(colDeviceCode, colName, colImportDate, colDisposalDate, colStatus, colAction);
    }

    public void addEquipment(ActionEvent e) {
        if (txtName.getText().isEmpty() || importDate.getValue() == null || cbStatus.getValue() == null) {
            alert.getAlert("Vui lòng điền đầy đủ thông tin").show();
        } else {
            try {
                int idTrangThai = getValueStatusMap();
                equipment.addThietBi(txtName.getText(), importDate.getValue(), idTrangThai);
                alert.getAlert("Thêm thiết bị thành công!").show();
                txtName.clear();
                importDate.setValue(null);
                cbStatus.setValue(null);
                loadData();
            } catch (SQLException ex) {
                Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void selectItemTableView() {
        tbEquipment.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ThietBi selectedItem = tbEquipment.getSelectionModel().getSelectedItem();
                if (selectedItem.getNgayThanhLy() != null) {
                    alert.getAlert("Không được cập nhật thiết bị ĐÃ THANH LÝ!").show();
                    return;
                } else if (selectedItem != null) {
                    idEquipment = selectedItem.getId();
                    txtName.setText(selectedItem.getTenThietBi());
                    TrangThai t = new TrangThai(statusMap.get(selectedItem.getIdTrangThai()));
                    cbStatus.setValue(t);
                    LocalDate ngayNhap = new java.sql.Date(selectedItem.getNgayNhap().getTime()).toLocalDate();
                    importDate.setValue(ngayNhap);
                    loadStatus(true);
                }
            }
        });
    }

    public void updateEquipment(ActionEvent e) {
        if (txtName.getText().isEmpty() || importDate.getValue() == null || cbStatus.getValue() == null) {
            alert.getAlert("Vui lòng điền đầy đủ thông tin").show();
        } else {
            if (disposalDate.getValue() == null || disposalDate.getValue().isAfter(importDate.getValue())) {
                int idTrangThai = getValueStatusMap();
                try {
                    equipment.updateThietBi(idEquipment, txtName.getText(), importDate.getValue(), disposalDate.getValue(), idTrangThai);
                    alert.getAlert("Cập nhật thông tin thành công").show();
                    loadData();
                } catch (SQLException ex) {
                    Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                alert.getAlert("Ngày thanh lý phải lớn hơn ngày nhập").show();
            }
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

}
