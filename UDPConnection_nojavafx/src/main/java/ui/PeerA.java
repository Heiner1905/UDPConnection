package ui;

import util.UDPConnection;

public class PeerA {
    public static void main(String[] args) {
        UDPConnection connection = UDPConnection.getInstance();
        connection.setPort(5000);
        connection.setDestination("192.168.1.5", 6000);
        connection.start();
    }
}
