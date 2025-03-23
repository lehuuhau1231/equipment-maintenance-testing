module com.qhuong.devicemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;

    opens com.qhuong.devicemanagementsystem to javafx.fxml;
    exports com.qhuong.devicemanagementsystem;
    requires jbcrypt;
}
