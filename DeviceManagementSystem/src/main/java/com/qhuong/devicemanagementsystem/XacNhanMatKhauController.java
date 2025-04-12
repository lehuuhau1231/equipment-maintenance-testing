/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author lehuu
 */
public class XacNhanMatKhauController {

    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    private String username;

    public void checkNewPasswordHander(ActionEvent e) {
        Utils alert = new Utils();
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        AdminServices adminService = new AdminServices();
        try {

            adminService.validateNewPassword(newPassword, confirmPassword);

            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
            adminService.updatePassword(username, hashedPassword);
            alert.getAlert("Đổi mật khẩu thành công!").show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("primary.fxml"));
            Parent homePage;
            try {
                homePage = loader.load();
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                Scene homePageScene = new Scene(homePage);
                stage.setScene(homePageScene);
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Logger.getLogger(XacNhanMatKhauController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDeviceData(String username) {
        this.username = username;
    }

    public void switchTabLogin(ActionEvent e) {
        Utils a = new Utils();
        a.switchTab(e, "primary.fxml");
    }
}
