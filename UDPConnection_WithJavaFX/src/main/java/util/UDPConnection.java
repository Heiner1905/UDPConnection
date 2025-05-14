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
                            onMessageReceived.accept(msg);
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
        try {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnMessageReceived(Consumer<String> handler) {
        this.onMessageReceived = handler;
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
