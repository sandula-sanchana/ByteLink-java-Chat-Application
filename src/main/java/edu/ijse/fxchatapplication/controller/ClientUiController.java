package edu.ijse.fxchatapplication.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientUiController implements Initializable {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField messageField;

    private DataInputStream dis;
    private DataOutputStream dos;


    @FXML
    private void onSend() {
        try {
            String msg = messageField.getText();
            dos.writeUTF("TEXT");
            dos.writeUTF(msg);
            dos.flush();

            appendMessage("You: " + msg);
            messageField.clear();
        } catch (IOException e) {
            appendMessage("Error sending message.");
        }
    }

    private void appendMessage(String msg) {
        javafx.application.Platform.runLater(() -> chatArea.appendText(msg + "\n"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 2003);
                appendMessage("Connected to server.");

                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String msg = dis.readUTF();
                    appendMessage("Server: " + msg);
                    if (msg.equalsIgnoreCase("exit")) {
                        appendMessage("server disconnected.");
                        break;
                    }
                }
            } catch (IOException e) {
                appendMessage("Connection closed.");
            }
        }).start();
    }

    public void sendImage(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                appendMessage("Invalid image file.");
                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // Send type header
            dos.writeUTF("IMAGE");
            dos.flush();

            // Send image length and bytes
            dos.writeInt(imageBytes.length);
            dos.write(imageBytes);
            dos.flush();

            appendMessage("You sent an image.");
        } catch (IOException e) {
            appendMessage("Error sending image: " + e.getMessage());
        }
    }

    @FXML
    private void onSendImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            sendImage(selectedFile);
        }
    }

}
