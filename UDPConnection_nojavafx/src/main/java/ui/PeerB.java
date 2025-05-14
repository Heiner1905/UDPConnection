package ui;

import util.UDPConnection;

public class PeerB {
    public static void main(String[] args) {
        UDPConnection connection = UDPConnection.getInstance();
        connection.setPort(6000); // Puerto en el que PeerB escuchará
        // Configura el destino a la IP y puerto de PeerA
        // Para pruebas locales, usa 127.0.0.1. Cambia "127.0.0.1" por la IP real de PeerA si está en otra máquina.
        connection.setDestination("127.0.0.1", 5000);

        // Añadir un shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("PeerB: Shutdown hook activado. Deteniendo conexión...");
            connection.shutdown();
        }));

        connection.start(); // Inicia el hilo de UDPConnection
    }
}