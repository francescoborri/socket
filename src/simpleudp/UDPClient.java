package simpleudp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Classe che implementa il lato client UDP, inviando una richiesta e stampando la risposta.
 */
public class UDPClient {
    /**
     * Socket che verrà utilizzato per inviare e ricevere richieste e risposte.
     */
    private final DatagramSocket socket;

    /**
     * Costruttore che inizializza il socket e imposta il suo timeout.
     * @throws SocketException eccezione lanciata nel caso in cui non si possa inizializzare il socket
     */
    public UDPClient() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);
    }

    /**
     * Metodo che invia un pacchetto di richiesta e stampa la risposta del server.
     * @param request messaggio da inviare al server
     * @param host indirizzo IP del server sottoforma di String, formattato con x.x.x.x
     * @param port porta sul quale inviare la richiesta
     * @return messaggio di risposta del server
     * @throws IOException eccezione lanciata in casi di timeout o altri errori legati alla connessione
     */
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

        // Costruzione del datagram di richiesta a partire da messaggio, indirizzo IP e porta
        datagram = new DatagramPacket(buffer, buffer.length, address, port);

        // Trasmissione del datagram di richiesta
        socket.send(datagram);

        // Attesa della ricezione del datagram di risposta (tempo massimo di attesa: 1s)
        socket.receive(datagram);

        // Verifica del socket di provenienza del datagram di risposta
        if (datagram.getAddress().equals(address) && datagram.getPort() == port)
            // Estrazione del messaggio dal datagram di risposta
            answer = new String(datagram.getData(), 0, datagram.getLength(), StandardCharsets.ISO_8859_1);
        else
            throw new SocketTimeoutException();

        return answer;
    }

    /**
     * Metodo che chiude il socket.
     */
    public void closeSocket() {
        socket.close();
    }

    /**
     * Main da lanciare per inviare richieste al server.
     * @param args indirizzo IP formattato come x.x.x.x, porta, messaggio
     */
    public static void main(String[] args) {
        int port;
        String ip, request, answer;

        // Se vengono passati argomenti al momento del lancio del programma si utilizzano questi, altrimenti vengono
        // specificati sul codice.
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

            // Creazione e invio del datagram, e (se non viene superato il timeout) ricezione della risposta
            answer = client.sendAndReceive(request, ip, port);

            // Stampa della risposta
            System.out.println("Ricevuto in risposta: " + answer);

            // Chiusura del socket
            client.closeSocket();
        } catch (SocketException exception) {
            // Errore incontrato durante la creazione del socket
            System.err.println("Errore creazione socket!\n" + exception.getMessage());
        } catch (UnknownHostException exception) {
            // Errore dovuto ad un indirizzo IP errato o non raggiungibile o non esistente
            System.err.println("Indirizzo IP errato!\n" + exception.getMessage());
        } catch (SocketTimeoutException exception) {
            // Errore dovuto al superamento del timeout
            System.err.println("Nessuna risposta dal server!\n" + exception.getMessage());
        } catch (IOException exception) {
            // Altri errori dovuti alla connessione
            System.err.println("Errore generico di comunicazione!\n" + exception.getMessage());
        }
    }
}