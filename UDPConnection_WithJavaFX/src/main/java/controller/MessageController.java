package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import util.UDPConnection;

public class MessageController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private TextField messageInput;

    @FXML
    public void initialize() {
        // Configura el callback para mensajes recibidos
        UDPConnection.getInstance().setOnMessageReceived(message -> {
            Platform.runLater(() -> chatArea.appendText("Peer → " + message + "\n"));
        });
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText().trim();
        String ip = ipField.getText().trim();
        String portStr = portField.getText().trim();

        if (!message.isEmpty() && !ip.isEmpty() && !portStr.isEmpty()) {
            try {
                int port = Integer.parseInt(portStr);
                UDPConnection.getInstance().sendDatagram(message, ip, port);
                chatArea.appendText("Yo → " + message + "\n");
                messageInput.clear();
            } catch (NumberFormatException e) {
                chatArea.appendText("⚠️ Puerto inválido.\n");
            }
        } else {
            chatArea.appendText("⚠️ Rellena todos los campos antes de enviar.\n");
        }
    }
}
