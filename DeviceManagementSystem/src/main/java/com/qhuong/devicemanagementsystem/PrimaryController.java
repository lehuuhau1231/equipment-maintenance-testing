package com.qhuong.devicemanagementsystem;

import com.qhuong.services.AdminServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PrimaryController implements Initializable{
    @FXML TextField txtUsername;
    @FXML PasswordField txtPassword;
    Utils alert = new Utils();
    
    @FXML
        private void switchToSecondary() throws IOException {
            App.setRoot("secondary");
        }
    
    public void loginHandler(ActionEvent e) throws SQLException, IOException {
        if (txtUsername.getText().isEmpty()) {
            alert.getAlert("Chưa điền username").show();
            return;
        } else if (txtPassword.getText().isEmpty()) {
            alert.getAlert("Chưa điền password").show();
            return;
        } else {
            AdminServices admin = new AdminServices();
            if(admin.getAdmin(txtUsername.getText(), txtPassword.getText()) == true) {
                Parent listEquipment = FXMLLoader.load(getClass().getResource("DanhSachThietBi.fxml"));
                Scene listEquipmentScene = new Scene(listEquipment);
                Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                window.setScene(listEquipmentScene);
                window.show();
            } else {
                alert.getAlert("Sai tài khoản hoặc mật khẩu").show();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        AdminServices admin = new AdminServices();
//        try {
//            admin.addAdmin("lehuuhau", "Lehuuhau1231@", "Le", "Hau", "lehuuhau1231@gmail.com");
//        } catch (SQLException ex) {
//            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
