module edu.ijse.fxchatapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.ijse.fxchatapplication to javafx.fxml;
    exports edu.ijse.fxchatapplication;
    exports  edu.ijse.fxchatapplication.controller;

    requires javafx.graphics;
    requires javafx.base;



    opens  edu.ijse.fxchatapplication.controller to javafx.fxml;
}