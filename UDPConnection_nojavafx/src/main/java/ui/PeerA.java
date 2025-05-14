package ui;

import util.UDPConnection;

public class PeerA {
    public static void main(String[] args) {
        UDPConnection connection = UDPConnection.getInstance();
        connection.setPort(5000); // Puerto en el que PeerA escuchará
        // Configura el destino a la IP y puerto de PeerB
        // Para pruebas locales, usa 127.0.0.1. Cambia "127.0.0.1" por la IP real de PeerB si está en otra máquina.
        connection.setDestination("127.0.0.1", 6000);

        // Añadir un shutdown hook para cerrar la conexión si la JVM termina abruptamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("PeerA: Shutdown hook activado. Deteniendo conexión...");
            connection.shutdown();
        }));

        connection.start(); // Inicia el hilo de UDPConnection (que a su vez inicia el receptor)
    }
}