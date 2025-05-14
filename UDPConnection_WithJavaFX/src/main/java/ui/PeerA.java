package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UDPConnection;

public class PeerA extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/message.fxml")); // Asegúrate de que la ruta esté bien
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Peer A");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Puedes iniciar aquí cualquier lógica adicional de PeerA si aplica
        UDPConnection.getInstance().startListener(5000); // Escucha en el puerto 5000 (o el que uses para PeerA)
    }

    public static void main(String[] args) {
        launch(args);
    }
}
