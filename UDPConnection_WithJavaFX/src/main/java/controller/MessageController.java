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
    public TextField ipField;

    @FXML
    public TextField portField;

    @FXML
    private TextField messageInput;

    // Puedes establecer estos valores desde PeerA o PeerB si quieres prellenarlos
    private String defaultDestIp;
    private int defaultDestPort;
    private String peerName = "Peer"; // Nombre por defecto

    public void setPeerInfo(String name, String defaultDestIp, int defaultDestPort) {
        this.peerName = name;
        this.defaultDestIp = defaultDestIp;
        this.defaultDestPort = defaultDestPort;
        // Opcional: Pre-rellenar los campos si se desea
        // if (ipField != null) ipField.setText(defaultDestIp);
        // if (portField != null) portField.setText(String.valueOf(defaultDestPort));
    }

    @FXML
    public void initialize() {
        // Configura el callback para mensajes recibidos
        UDPConnection.getInstance().setOnMessageReceived(message -> {
            // Platform.runLater ya no es necesario aquí si se hace en UDPConnection
            // pero no hace daño tenerlo por si acaso se llama desde otro sitio.
            Platform.runLater(() -> chatArea.appendText("Otro → " + message + "\n"));
        });

        // Listener para la tecla Enter en el campo de mensaje
        messageInput.setOnAction(event -> sendMessage());
    }

    @FXML
    public void sendMessage() {
        String message = messageInput.getText().trim();
        String ip = ipField.getText().trim();
        String portStr = portField.getText().trim();

        if (message.isEmpty()) {
            appendToChat("⚠️ El mensaje no puede estar vacío.\n");
            return;
        }
        if (ip.isEmpty()) {
            appendToChat("⚠️ La IP de destino no puede estar vacía.\n");
            return;
        }
        if (portStr.isEmpty()) {
            appendToChat("⚠️ El puerto de destino no puede estar vacío.\n");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                appendToChat("⚠️ Puerto inválido. Debe estar entre 1 y 65535.\n");
                return;
            }

            UDPConnection.getInstance().sendDatagram(message, ip, port);
            appendToChat("Yo (" + peerName + ") → " + message + "\n");
            messageInput.clear();

        } catch (NumberFormatException e) {
            appendToChat("⚠️ Puerto inválido. Debe ser un número.\n");
        }
    }

    private void appendToChat(String text) {
        Platform.runLater(() -> chatArea.appendText(text));
    }

    // Método para mostrar un mensaje de error o estado general
    public void showStatusMessage(String message) {
        appendToChat("Sistema: " + message + "\n");
    }
}