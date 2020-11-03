package simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPEchoServer extends Thread {
    private final DatagramSocket socket;

    public UDPEchoServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setSoTimeout(1000);
    }

    public void run() {
        // Creazione di un buffer per l'invio/ricezione dei dati
        byte[] buffer = new byte[8192];

        // Creazione di un datagram UDP a partire dal buffer
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);

        // Finché il thread non viene interrotto, il thread aspetta la ricezione di un datagram, e ad ogni secondo viene lanciata una
        // IOException se non viene ricevuto nessun datagram nel secondo, altrimenti viene inviata una risposta e il server
        // rimane in ascolto
        while (!Thread.interrupted()) {
            try {
                // Attesa ricezione datagram di richiesta (il tempo massimo di attesa è 1s)
                socket.receive(request);
                // Costruzione datagram di risposta (identico al datagram di richiesta)
                DatagramPacket answer = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                // Trasmissione datagram di risposta
                socket.send(answer);
            } catch (IOException exception) {
                System.err.println("Errore nell'invio/ricezione del datagram!\n" + exception.getMessage());
            }
        }

        // Chiusura del socket
        socket.close();
    }

    public static void main(String[] args) {
        try {
            UDPEchoServer echoserver = new UDPEchoServer(7);
            echoserver.start();
            int ignored = System.in.read();
            echoserver.interrupt();
            echoserver.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
