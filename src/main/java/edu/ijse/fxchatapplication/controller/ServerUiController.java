package edu.ijse.fxchatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerUiController implements Initializable {
    public ImageView imageShower;
    Socket localSocket;
    private DataInputStream dis;
    private DataOutputStream  dos;

    Image  image;

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
                    String type = dis.readUTF();

                    if (type.equals("TEXT")) {
                        String msg = dis.readUTF();
                        appendMessage("Client: " + msg);

                        if (msg.equalsIgnoreCase("exit")) {
                            appendMessage("Client disconnected.");
                            break;
                        }

                    } else if (type.equals("IMAGE")) {
                        int length = dis.readInt();
                        byte[] imageBytes = new byte[length];
                        dis.readFully(imageBytes);
                        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new ByteArrayInputStream(imageBytes));

                        javafx.application.Platform.runLater(() -> {
                            imageShower.setImage(fxImage);
                            appendMessage("Image received and displayed.");
                        });
                        saveImage(imageBytes);
                        appendMessage("Image received and saved.");
                    } else {
                        appendMessage("Unknown message type received: " + type);
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

    private void saveImage(byte[] imageBytes) {
        try {
            java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
            if (image != null) {
                String filename = "received_" + System.currentTimeMillis() + ".jpg";
                javax.imageio.ImageIO.write(image, "jpg", new java.io.File(filename));
                System.out.println("Image saved as: " + filename);
            } else {
                System.err.println("Failed to decode image data.");
            }
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }




}
