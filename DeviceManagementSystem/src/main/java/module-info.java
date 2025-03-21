module com.qhuong.devicemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens com.qhuong.devicemanagementsystem to javafx.fxml;
    exports com.qhuong.devicemanagementsystem;
}
