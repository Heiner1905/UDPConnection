package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UDPConnection;

public class PeerB extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/message.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("Peer B");
        primaryStage.setScene(scene);
        primaryStage.show();

        UDPConnection.getInstance().startListener(6000); // Escucha en otro puerto
    }

    public static void main(String[] args) {
        launch(args);
    }
}
