package edu.ijse.fxchatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
       Parent parent = FXMLLoader.load(getClass().getResource("/view/serverui.fxml"));
       Scene scene = new Scene(parent);
       stage.setScene(scene);
       stage.setTitle("Chat Application-server interface");
       stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
