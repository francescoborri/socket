package simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPTooSimpleServer {
    public static void main(String[] args) {
        try {
            // Apertura del socket sulla porta 7
            DatagramSocket socket = new DatagramSocket(7);

            // Creazione di un buffer per l'invio/ricezione di dati della dimensione specificata
            byte[] buffer = new byte[8192];

            // Creazione di un datagram UDP a partire dal buffer appena creato
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            // Attesa della ricezione di un datagram
            socket.receive(request);

            // Ricezione del datagram di richiesta, costruzione della stringa a partire della richiesta ottenuta
            String requestString = new String(request.getData(), 0, request.getLength(), StandardCharsets.ISO_8859_1);
            System.out.println("Ricevuta richiesta: " + requestString);

            // Costruzione del datagram di risposta (identico ma con il messaggio tutto maiuscolo)
            DatagramPacket answer = new DatagramPacket(requestString.toUpperCase().getBytes(), request.getLength(), request.getAddress(), request.getPort());

            // Trasmissione del datagram di risposta e chiusura del socket
            socket.send(answer);
            socket.close();
        } catch (SocketException exception) {
            System.err.println("Errore nell'apertura del socket!\n" + exception.getMessage());
        } catch (IOException exception) {
            System.err.println("Errore di I/O!\n" + exception.getMessage());
        }
    }
}