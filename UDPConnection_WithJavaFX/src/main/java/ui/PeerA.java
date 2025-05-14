package ui;

import controller.MessageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UDPConnection;

import java.io.IOException;
import java.net.SocketException;

public class PeerA extends Application {

    private static final int LISTEN_PORT_A = 5000;
    private static final int DEFAULT_DEST_PORT_B = 6000; // Puerto donde PeerB escucha
    private static final String DEFAULT_DEST_IP = "127.0.0.1"; // Asumir localhost por defecto

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/message.fxml"));
        Scene scene = new Scene(loader.load());

        MessageController controller = loader.getController();
        controller.setPeerInfo("Peer A", DEFAULT_DEST_IP, DEFAULT_DEST_PORT_B);
        // Opcional: pre-rellenar los campos de IP y Puerto en la UI
        // controller.ipField.setText(DEFAULT_DEST_IP); // Necesitaría que ipField sea público o tener un método setter
        // controller.portField.setText(String.valueOf(DEFAULT_DEST_PORT_B));

        primaryStage.setTitle("Chat UDP - Peer A (Escuchando en " + LISTEN_PORT_A + ")");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Cerrando Peer A y deteniendo conexión UDP...");
            UDPConnection.getInstance().stop();
            Platform.exit(); // Opcional, asegura que la app JavaFX termine
            System.exit(0); // Asegura que la JVM termine si hay hilos no demonio rebeldes
        });

        UDPConnection.getInstance().startListener(LISTEN_PORT_A);
        controller.showStatusMessage("Escuchando en el puerto " + LISTEN_PORT_A);
        // Pre-rellenar campos de destino para facilitar la comunicación con Peer B
        // Asumiendo que los campos son públicos en MessageController o mediante setters
        // controller.ipField.setText(DEFAULT_DEST_IP);
        // controller.portField.setText(String.valueOf(DEFAULT_DEST_PORT_B));
        // Mejor hacerlo con un método en el controlador:
        if (controller.ipField != null) controller.ipField.setText(DEFAULT_DEST_IP);
        if (controller.portField != null) controller.portField.setText(String.valueOf(DEFAULT_DEST_PORT_B));
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Método stop() de PeerA llamado.");
        UDPConnection.getInstance().stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}