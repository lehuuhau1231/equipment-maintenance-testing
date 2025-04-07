/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.Email;
import com.qhuong.services.AdminServices;
import com.qhuong.services.NhanVienSuaChuaServices;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
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
 *
 * @author lehuu
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
                Parent otp;
                try {
                    otp = loader.load();
                    OTPController controller = loader.getController();
                    controller.setDeviceData(txtUsername.getText());

                    Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    Scene otpScene = new Scene(otp);
                    stage.setScene(otpScene);
                    stage.show();
                } catch (IOException ex) {
                    Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                alert.getAlert("Tài khoản không tồn tại!").show();
            }
        } catch (SQLException ex) {
            Logger.getLogger(QuenMatKhauController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
