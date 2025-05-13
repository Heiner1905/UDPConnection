package util;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPConnection extends Thread {
    private static UDPConnection instance;
    private DatagramSocket socket;
    private int destinationPort;
    private String destinationIP;

    private UDPConnection() {
    }

    public static UDPConnection getInstance() {
        if (instance == null) {
            instance = new UDPConnection();
        }
        return instance;
    }

    public void setPort(int port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void setDestination(String ip, int port) {
        this.destinationIP = ip;
        this.destinationPort = port;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Thread receiverThread = new Thread(() -> {
            try {
                System.out.println("Listening for messages...");
                while (true) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                    InetAddress senderAddress = packet.getAddress();
                    int senderPort = packet.getPort();
                    System.out.println("\n Message from " + senderAddress.getHostAddress() + ":" + senderPort + " → " + msg);

                }
            } catch (IOException e) {
                System.err.println("Receiver stopped.");
            }
        });

        receiverThread.start();

        try {
            while (true) {
                System.out.print("\n Enter message (or type 'exit' to quit): ");
                String msg = scanner.nextLine();

                if (msg.equalsIgnoreCase("exit")) {
                    socket.close();
                    break;
                }

                sendDatagram(msg, destinationIP, destinationPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDatagram(String msg, String ip, int port) {
        new Thread(() -> {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), ipAddress, port);
                socket.send(packet);
                System.out.println("Message sent to " + ip + ":" + port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
