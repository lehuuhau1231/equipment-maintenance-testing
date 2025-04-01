/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.qhuong.devicemanagementsystem;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author lehuu
 */
public class Utils {
    public static Alert getAlert(String content) {
        return new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
    }
    
    public void switchTab(ActionEvent e, String fileNameFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fileNameFXML));
            Parent equipment = loader.load();
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            Scene equipmentScene = new Scene(equipment);
            stage.setScene(equipmentScene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(DanhSachThietBiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
