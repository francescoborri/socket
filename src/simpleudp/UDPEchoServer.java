package simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * Semplice server UDP (basato su un Thread) che accetta più richieste, stampando il messaggio di esse nel System.out
 * e rispondendo ad esse con lo stesso messaggio della richiesta.
 */
public class UDPEchoServer extends Thread {
    /**
     * Socket utilizzato per inviare/ricevere datagram.
     */
    private final DatagramSocket socket;

    /**
     * Costruttore che inizializza il socket, assegnandogli una porta e impostando il timeout.
     * @param port porta sul quale il server ascolterà per le richieste
     * @throws SocketException eccezione lanciata in caso di creazione non andata a buon fine del socket
     */
    public UDPEchoServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setSoTimeout(10000);
    }

    /**
     * Override del metodo run() sulla classe Thread, avvia il server UDP.
     * Accetta richieste finché non viene interrotto il thread, ad ogni richiesta viene stampato il messaggio
     * che essa contiene insieme all'indirizzo IP, e successivamente rispondo al mittente con lo stesso messaggio.
     */
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

                //Stampa dell'indirizzo IP di provenienza e del messaggio inviato dal mittente
                System.out.printf("[%s] -> %s\n", request.getAddress().getHostAddress(), new String(request.getData(), 0, request.getLength(), StandardCharsets.ISO_8859_1));

                // Costruzione datagram di risposta (identico al datagram di richiesta)
                DatagramPacket answer = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());

                // Trasmissione datagram di risposta
                socket.send(answer);
            } catch (IOException exception) {
                // Errore dovuto al superamento del timeout di attesa per connessioni
                System.out.printf("[ERROR] -> %s\n", exception.getMessage());
            }
        }

        // Chiusura del socket
        socket.close();
    }

    public static void main(String[] args) {
        try {
            // Creazione del server
            UDPEchoServer echoserver = new UDPEchoServer(7);

            // Avvio del server
            echoserver.start();

            // Appena viene scritto qualcosa sul System.in, il server sul thread viene interrotto e aspettata la sua
            // effettiva terminazione
            int ignored = System.in.read();
            echoserver.interrupt();
            echoserver.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
