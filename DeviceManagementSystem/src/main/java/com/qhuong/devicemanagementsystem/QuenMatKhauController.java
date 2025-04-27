package com.qhuong.devicemanagementsystem;

import com.qhuong.services.AdminServices;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for forgot password functionality
 * 
 * Author: lehuu
 */
public class QuenMatKhauController {

    @FXML
    private TextField txtUsername;

    public void checkUsernameHandler(ActionEvent e) {
        Utils alert = new Utils();
        if (txtUsername.getText().isEmpty()) {
            alert.getAlert("Vui lòng điền đủ thông tin!").show();
            return;
        }

        AdminServices adminService = new AdminServices();
        try {
            if (adminService.isUsernameExist(txtUsername.getText())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("OTP.fxml"));
                Parent otp = loader.load();

                OTPController controller = loader.getController();
                controller.setDeviceData(txtUsername.getText());

                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(otp));
                stage.show();
            } else {
                alert.getAlert("Tài khoản không tồn tại!").show();
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(QuenMatKhauController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
