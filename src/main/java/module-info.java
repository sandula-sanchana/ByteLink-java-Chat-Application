module edu.ijse.fxchatapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.ijse.fxchatapplication to javafx.fxml;
    exports edu.ijse.fxchatapplication;
}