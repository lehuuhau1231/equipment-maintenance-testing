/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import com.qhuong.pojo.Email;
import com.qhuong.services.AdminServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author lehuu
 */
public class OTPController implements Initializable {

    @FXML
    private TextField txtOTP;
    private String username;
    private int otp;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtOTP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                txtOTP.setText(oldValue);
            }
        });
    }

    public void checkOTPHandler(ActionEvent e) {
        Utils alert = new Utils();
        if (txtOTP.getText().isEmpty()) {
            alert.getAlert("Vui lòng điền đủ thông tin!").show();
            return;
        }

        if (txtOTP.getLength() != 6) {
            alert.getAlert("Mã OTP chỉ 6 ký tự!").show();
            return;
        }

        if (Integer.parseInt(txtOTP.getText().trim()) != otp) {
            alert.getAlert("Sai mã OTP!").show();
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("XacNhanMatKhau.fxml"));
        Parent confirmPassword;
        try {
            confirmPassword = loader.load();
            XacNhanMatKhauController controller = loader.getController();
            controller.setDeviceData(username);

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene confirmPasswordScene = new Scene(confirmPassword);
            stage.setScene(confirmPasswordScene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOTPAsync() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        this.otp = code; // Lưu OTP ngay để sử dụng sau

        Task<Void> sendEmailTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                AdminServices adminService = new AdminServices();
                try {
                    String toEmail = adminService.getEmail(username);
                    if (toEmail == null) {
                        Logger.getLogger(OTPController.class.getName()).log(Level.SEVERE, "Email không tồn tại cho username: " + username);
                        return null;
                    }
                    String subject = "Xác thực OTP!";
                    String body = "Mã OTP là: " + code;
                    Email.sendEmail(toEmail, subject, body);
                } catch (SQLException ex) {
                    Logger.getLogger(OTPController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };

        sendEmailTask.setOnFailed(event -> {
            Utils alert = new Utils();
            alert.getAlert("Không thể gửi OTP qua email. Vui lòng thử lại!").show();
        });

        new Thread(sendEmailTask).start();
    }

    public void setDeviceData(String username) {
        this.username = username;
        sendOTPAsync();
    }

    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
