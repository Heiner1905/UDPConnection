package ui;

import controller.MessageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UDPConnection;

import java.net.SocketException;

public class PeerB extends Application {

    private static final int LISTEN_PORT_B = 6000;
    private static final int DEFAULT_DEST_PORT_A = 5000; // Puerto donde PeerA escucha
    private static final String DEFAULT_DEST_IP = "127.0.0.1"; // Asumir localhost por defecto


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/message.fxml"));
        Scene scene = new Scene(loader.load());

        MessageController controller = loader.getController();
        controller.setPeerInfo("Peer B", DEFAULT_DEST_IP, DEFAULT_DEST_PORT_A);

        primaryStage.setTitle("Chat UDP - Peer B (Escuchando en " + LISTEN_PORT_B + ")");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Cerrando Peer B y deteniendo conexión UDP...");
            UDPConnection.getInstance().stop();
            Platform.exit(); // Opcional
            System.exit(0); // Asegura que la JVM termine
        });

        UDPConnection.getInstance().startListener(LISTEN_PORT_B);
        controller.showStatusMessage("Escuchando en el puerto " + LISTEN_PORT_B);
        // Pre-rellenar campos de destino para facilitar la comunicación con Peer A
        if (controller.ipField != null) controller.ipField.setText(DEFAULT_DEST_IP);
        if (controller.portField != null) controller.portField.setText(String.valueOf(DEFAULT_DEST_PORT_A));

    }

    @Override
    public void stop() throws Exception {
        System.out.println("Método stop() de PeerB llamado.");
        UDPConnection.getInstance().stop();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}