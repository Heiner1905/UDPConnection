package ui;

import util.UDPConnection;

public class PeerB {
    public static void main(String[] args) {
        UDPConnection connection = UDPConnection.getInstance();
        connection.setPort(6000);
        connection.setDestination("192.168.1.5", 5000);
        connection.start();
    }
}
