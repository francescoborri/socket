package simpleudp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private final DatagramSocket socket;

    public UDPClient() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);
    }

    public String sendAndReceive(String request, String host, int port) throws IOException {
        byte[] buffer;
        DatagramPacket datagram;
        String answer;

        // Indirizzo IP del destinatario del datagram
        InetAddress address = InetAddress.getByName(host);

        // Verifica che il socket non sia chiuso
        if (socket.isClosed())
            throw new IOException();

        // Trasformazione in array di byte della stringa da inviare
        buffer = request.getBytes(StandardCharsets.UTF_8);

        // Costruzione del datagram di richiesta a partire da dati, indirizzo ip e porta
        datagram = new DatagramPacket(buffer, buffer.length, address, port);

        // Trasmissione del datagram di richiesta
        socket.send(datagram);

        // Attesa della ricezione del datagram di risposta (tempo massimo di attesa: 1s)
        socket.receive(datagram);

        // Verifica del socket di provenienza del datagram di risposta
        if (datagram.getAddress().equals(address) && datagram.getPort() == port)
            answer = new String(datagram.getData(), 0, datagram.getLength(), StandardCharsets.ISO_8859_1);
        else
            throw new SocketTimeoutException();

        return answer;
    }

    public void closeSocket() {
        socket.close();
    }

    public static void main(String[] args) {
        int port;
        String ip, request, answer;

        if (args.length != 3) {
            ip = "127.0.0.1";
            port = 7;
            request = "Ciao!";
        } else {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            request = args[2];
        }

        try {
            UDPClient client = new UDPClient();
            answer = client.sendAndReceive(request, ip, port);
            System.out.println("Ricevuto in risposta: " + answer);
            client.closeSocket();
        } catch (SocketException exception) {
            System.err.println("Errore creazione socket!\n" + exception.getMessage());
        } catch (UnknownHostException exception) {
            System.err.println("Indirizzo IP errato!\n" + exception.getMessage());
        } catch (SocketTimeoutException exception) {
            System.err.println("Nessuna risposta dal server!\n" + exception.getMessage());
        } catch (IOException exception) {
            System.err.println("Errore generico di comunicazione!\n" + exception.getMessage());
        }
    }
}