package com.qhuong.devicemanagementsystem;
import com.qhuong.services.AdminServices;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController implements Initializable{

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("TEssss");
        AdminServices admin = new AdminServices();
        try {
            admin.getAdmin("admin", "admin123");
        } catch (SQLException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
