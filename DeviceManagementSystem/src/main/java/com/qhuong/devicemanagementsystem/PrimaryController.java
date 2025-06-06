package com.qhuong.devicemanagementsystem;

import com.qhuong.services.AdminServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PrimaryController implements Initializable {

    @FXML
    TextField txtUsername;
    @FXML
    PasswordField txtPassword;
    @FXML
    Button btnLogin;
    @FXML
    Label timer;
    private Utils alert = new Utils();
    private int countPassError;
    private int remainingSeconds = 5 * 60;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        AdminServices admin = new AdminServices();
//        try {
//            admin.addAdmin("lehuuhau", "Lehuuhau1231@", "Le", "Hau", "lehuuhau1231@gmail.com");
////              admin.addAdmin("qhuong", "@Huong123", "Tran", "Huong", "quynhhuongtran314@gmail.com");
//        } catch (SQLException ex) {
//            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void loginHandler(ActionEvent e) throws IOException {
        AdminServices admin = new AdminServices();
        try {
            admin.checkLogin(txtUsername.getText(), txtPassword.getText());

            Parent listEquipment = FXMLLoader.load(getClass().getResource("DanhSachThietBi.fxml"));
            Scene listEquipmentScene = new Scene(listEquipment);
            Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
            window.setScene(listEquipmentScene);
            window.show();
        } catch (IllegalArgumentException ex) {
            alert.getAlert(ex.getMessage()).show();
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        countPassError++;
        if (countPassError >= 2) {
//            alert.getAlert("Vui lòng chờ sau 5 phút để đăng nhập").show();
//            btnLogin.setDisable(true);
            countPassError = 0;
//            delay();
        }
    }

    public void delay() {
        PauseTransition delay = new PauseTransition(Duration.seconds(10));
        delay.setOnFinished(e -> {
            btnLogin.setDisable(false);
        });
        delay.play();
    }

    public void forgetPasswordHandler(ActionEvent e) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("QuenMatKhau.fxml"));
        Parent forgetPassword;
        try {
            forgetPassword = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene forgetPasswordScene = new Scene(forgetPassword);
            stage.setScene(forgetPasswordScene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
