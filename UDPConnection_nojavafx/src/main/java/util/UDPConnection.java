package util;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class UDPConnection extends Thread {
    private static UDPConnection instance;
    private DatagramSocket socket;
    private int destinationPort;
    private String destinationIP;
    private volatile boolean running = false; // Controla el estado de los hilos

    private UDPConnection() {
        // Constructor privado para singleton
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
            System.out.println("Socket UDP escuchando en el puerto: " + port);
        } catch (SocketException e) {
            System.err.println("Error al inicializar el socket en el puerto " + port + ": " + e.getMessage());
            // Considerar lanzar una RuntimeException si este error es crítico para el inicio
            // e.printStackTrace(); // Original
        }
    }

    public void setDestination(String ip, int port) {
        this.destinationIP = ip;
        this.destinationPort = port;
        System.out.println("Destino configurado a IP: " + ip + ", Puerto: " + port);
    }

    @Override
    public void run() {
        if (socket == null) {
            System.err.println("El socket no ha sido inicializado. Por favor, llama a setPort() antes de start().");
            return;
        }
        running = true;
        Scanner scanner = new Scanner(System.in);

        Thread receiverThread = new Thread(() -> {
            System.out.println("Hilo receptor: Iniciado. Escuchando mensajes...");
            while (running) {
                try {
                    byte[] buf = new byte[1024]; // El tamaño del buffer puede ajustarse
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet); // Operación bloqueante

                    if (!running) { // Comprobar estado después de despertar de receive()
                        break;
                    }

                    String msg = new String(packet.getData(), 0, packet.getLength()).trim();
                    InetAddress senderAddress = packet.getAddress();
                    int senderPort = packet.getPort();
                    // Imprimir el mensaje recibido y luego el prompt para nueva entrada
                    System.out.println("\nMensaje de " + senderAddress.getHostAddress() + ":" + senderPort + " \u2192 " + msg);
                    System.out.print("Escribe un mensaje (o 'exit' para salir): "); // Reimprimir prompt

                } catch (SocketException se) {
                    if (running) { // Si running es true, el cierre no fue intencional desde 'exit'
                        System.err.println("Hilo receptor: SocketException (¿Socket cerrado por otra causa?): " + se.getMessage());
                        running = false; // Detener otros bucles si el socket falla
                    }
                    // Si !running, es probable que el socket se haya cerrado desde el hilo principal (por 'exit')
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Hilo receptor: IOException: " + e.getMessage());
                        e.printStackTrace();
                        running = false; // Detener en caso de otros errores de IO
                    }
                }
            }
            System.out.println("Hilo receptor: Detenido.");
        });
        receiverThread.setName("UDP-Receiver-Thread");
        receiverThread.setDaemon(true); // Permite que la JVM termine si solo queda este hilo
        receiverThread.start();

        // Este es el "hilo de transmisión" principal para la entrada/salida de consola
        System.out.println("Hilo de envío (consola): Iniciado. Escribe 'exit' para salir.");
        try {
            while (running) {
                System.out.print("Escribe un mensaje (o 'exit' para salir): ");
                if (!scanner.hasNextLine()){ // Manejar fin de entrada (ej. Ctrl+D en Linux)
                    System.out.println("Hilo de envío (consola): Fin de la entrada detectado.");
                    running = false;
                    break;
                }
                String msg = scanner.nextLine().trim();

                if (!running) { // Comprobar estado si receiverThread lo cambió
                    break;
                }

                if (msg.equalsIgnoreCase("exit")) {
                    running = false; // Señal para que todos los bucles terminen
                    break;          // Sale del bucle de envío (este hilo)
                }

                if (destinationIP == null || destinationPort == 0) {
                    System.out.println("⚠️ Destino no configurado. Usa setDestination() o asegúrate de que esté configurado.");
                } else if (!msg.isEmpty()){
                    sendDatagram(msg, destinationIP, destinationPort);
                }
            }
        } catch (IllegalStateException ise){ // Puede ocurrir si el Scanner se cierra prematuramente
            if(running) System.err.println("Hilo de envío (consola): Error con Scanner - " + ise.getMessage());
        }
        catch (Exception e) { // Captura general para otros posibles errores
            if (running) {
                System.err.println("Hilo de envío (consola): Error inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        } finally {
            running = false; // Asegurar que esté falso al salir
            System.out.println("Hilo de envío (consola): Terminando...");

            if (socket != null && !socket.isClosed()) {
                System.out.println("Cerrando el socket UDP...");
                socket.close();
            }
            // No cerramos el System.in (scanner.close()) explícitamente aquí
            // para evitar problemas si se reutiliza System.in o en ciertos entornos.
            // La JVM lo gestionará al salir.

            // Esperar brevemente a que el hilo receptor (demonio) termine después de cerrar el socket
            try {
                if(receiverThread.isAlive()){
                    receiverThread.join(500); // Espera hasta 500ms
                }
            } catch (InterruptedException e) {
                System.err.println("Interrumpido mientras se esperaba al hilo receptor.");
                Thread.currentThread().interrupt();
            }
            System.out.println("UDPConnection: Todos los hilos y recursos deberían estar detenidos/liberados.");
        }
    }

    /**
     * Envía un mensaje datagrama. Esta operación ya se ejecuta en un nuevo hilo separado
     * para no bloquear al hilo llamador (que en este caso es el hilo de consola de UDPConnection).
     * @param msg El mensaje a enviar.
     * @param ip La IP de destino.
     * @param port El puerto de destino.
     */
    public void sendDatagram(String msg, String ip, int port) {
        if (!running) {
            // System.out.println("No se envía el mensaje, la conexión no está activa.");
            return;
        }
        if (socket == null || socket.isClosed()){
            System.err.println("⚠️ No se puede enviar mensaje: el socket está cerrado o no ha sido inicializado.");
            return;
        }

        // La lógica de transmisión (socket.send) se ejecuta en este nuevo hilo.
        new Thread(() -> {
            try {
                InetAddress ipAddress = InetAddress.getByName(ip);
                // Es buena práctica especificar el charset, ej: msg.getBytes(StandardCharsets.UTF_8)
                byte[] msgBytes = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, ipAddress, port);
                socket.send(packet);
                // System.out.println("Mensaje '" + msg + "' enviado a " + ip + ":" + port); // Opcional: log de envío
            } catch (SocketException se) {
                // Comprobar 'running' para no mostrar errores si nos estamos cerrando intencionalmente
                if (running) System.err.println("⚠️ Error de Socket al enviar a " + ip + ":" + port + " (¿Socket cerrado inesperadamente?): " + se.getMessage());
            }
            catch (IOException e) {
                if (running) System.err.println("⚠️ Error de E/S al enviar mensaje a " + ip + ":" + port + ": " + e.getMessage());
                // e.printStackTrace(); // Puede ser muy verboso
            }
        }, "UDP-SendOp-" + System.currentTimeMillis()).start(); // Nombre del hilo para depuración
    }

    /**
     * Método para solicitar la detención de la conexión desde fuera.
     * La forma principal de detener es escribiendo 'exit' en la consola.
     */
    public void shutdown() {
        if (!running) return; // Ya se está deteniendo o detenido

        System.out.println("UDPConnection: Solicitud de apagado recibida...");
        running = false; // Señal para que los bucles terminen

        if (socket != null && !socket.isClosed()) {
            socket.close(); // Esto debería interrumpir socket.receive() en receiverThread
        }
        // El hilo 'this' (run()) debería terminar al detectar 'running = false'
        // o al interrumpirse la entrada de Scanner.
        // Se podría intentar this.interrupt() si el hilo está bloqueado en scanner.nextLine(),
        // pero su efectividad es limitada para IO bloqueante de consola.
    }
}