package util;

import java.io.IOException;
import java.net.*;
import java.util.function.Consumer;

public class UDPConnection {

    private static UDPConnection instance;

    private DatagramSocket socket;
    private boolean running = false;
    private Thread receiveThread;

    private Consumer<String> onMessageReceived;

    private UDPConnection() {
        // Constructor privado para singleton
    }

    public static UDPConnection getInstance() {
        if (instance == null) {
            instance = new UDPConnection();
        }
        return instance;
    }

    public void startListener(int listenPort) {
        try {
            socket = new DatagramSocket(listenPort);
            running = true;

            receiveThread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (running) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String msg = new String(packet.getData(), 0, packet.getLength());
                        if (onMessageReceived != null) {
                            javafx.application.Platform.runLater(() -> onMessageReceived.accept(msg));
                        }
                    } catch (IOException e) {
                        if (running) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            receiveThread.setDaemon(true);
            receiveThread.start();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendDatagram(String message, String ip, int port) {
        if (socket == null || socket.isClosed()) {
            System.err.println("No se puede enviar el datagrama, el socket no está inicializado o está cerrado.");
            // Opcionalmente, notificar al usuario a través de la UI
            if (onMessageReceived != null) {
                javafx.application.Platform.runLater(() -> onMessageReceived.accept("⚠️ Error: El socket no está listo para enviar. Intenta reiniciar."));
            }
            return;
        }

        try {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
            socket.send(packet);
        } catch (UnknownHostException e){
            if (onMessageReceived != null) { // Reutilizar el callback para mensajes de error internos
                javafx.application.Platform.runLater(() -> onMessageReceived.accept("⚠️ Error: IP de destino desconocida: " + ip));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnMessageReceived(Consumer<String> handler) {
        this.onMessageReceived = handler;
    }

    public void stop() {
        System.out.println("Intentando detener UDPConnection...");
        running = false; // Señal para que el hilo de recepción termine
        if (receiveThread != null && receiveThread.isAlive()) {
            // El cierre del socket debería interrumpir el bloqueo de socket.receive()
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Esto causará una SocketException en el hilo de receive, que lo terminará
            }
            try {
                receiveThread.join(1000); // Esperar un poco a que el hilo termine
            } catch (InterruptedException e) {
                System.err.println("Interrupción mientras se esperaba al hilo de recepción: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restaurar estado de interrupción
            }
        }
        if (socket != null && !socket.isClosed()) { // Doble chequeo por si el hilo no lo cerró
            socket.close();
        }
        System.out.println("UDPConnection detenido. Socket cerrado: " + (socket == null || socket.isClosed()));
        socket = null; // Ayuda al GC y previene reutilización accidental
    }
}
