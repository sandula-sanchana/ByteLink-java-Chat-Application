package edu.ijse.fxchatapplication.controller;

import edu.ijse.fxchatapplication.util.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ServerUiController implements Initializable {
    Socket localSocket;
    private DataInputStream dis;
    private DataOutputStream  dos;

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    @FXML
    void onSend(ActionEvent event) {
        try {
            String msg = messageField.getText();
            dos.writeUTF(msg);
            dos.flush();
            appendMessage("you: " + msg);
            messageField.clear();
        } catch (IOException e) {
            appendMessage("Error sending message.");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new Thread(() -> {
            try ( ServerSocket serverSocket = new ServerSocket(2003)) {
                appendMessage("Server started on port 2003...");
                localSocket = serverSocket.accept();
                appendMessage("Client connected!");

                dis = new DataInputStream(localSocket.getInputStream());
                dos = new DataOutputStream(localSocket.getOutputStream());

                while (true) {
                    String msg = dis.readUTF();
                    appendMessage("Client: " + msg);
                    if (msg.equalsIgnoreCase("exit"))
                    {
                        appendMessage("server disconnected.");
                        break;
                    }
                }
            } catch (IOException e) {
                appendMessage("Connection closed.");
            }
        }).start();

    }

    private void appendMessage(String msg) {
        javafx.application.Platform.runLater(() -> chatArea.appendText(msg + "\n"));
    }



}
