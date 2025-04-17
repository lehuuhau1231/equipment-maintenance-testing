module com.qhuong.devicemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires org.junit.jupiter.api;

    opens com.qhuong.devicemanagementsystem to javafx.fxml;
    exports com.qhuong.devicemanagementsystem;
    exports com.qhuong.pojo;
    exports com.qhuong.services;
    requires jbcrypt;
    requires jakarta.mail;
}
