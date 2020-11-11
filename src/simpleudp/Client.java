package simpleudp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private final DatagramSocket socket;
    private final byte[] answerBuffer = new byte[8192];
    private final DatagramPacket answer = new DatagramPacket(answerBuffer, answerBuffer.length);

    public Client() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(10000);
    }

    public String sendAndReceive(String requestData, String host, int port) throws IOException {
        String answerData;
        InetAddress address = InetAddress.getByName(host);

        if (socket.isClosed())
            throw new IOException();

        byte[] requestBuffer = requestData.getBytes(StandardCharsets.UTF_8);
        DatagramPacket request = new DatagramPacket(requestBuffer, requestBuffer.length, address, port);

        socket.send(request);
        socket.receive(answer);

        if (answer.getAddress().equals(address) && answer.getPort() == port)
            answerData = new String(answer.getData(), 0, answer.getLength(), StandardCharsets.UTF_8);
        else
            throw new SocketTimeoutException();

        return answerData;
    }

    public void closeSocket() {
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        String ip, data, answer;
        int port, requests;
        boolean spam;

        if (args.length != 5) {
            ip = "127.0.0.1";
            port = 7;
            data = "Ciao!";
            spam = true;
            requests = 1000;
        } else {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            data = args[2];
            spam = Boolean.parseBoolean(args[3]);
            requests = Integer.parseInt(args[4]);
        }

        Client client = new Client();

        if (spam) {
            for (int i = 0; i < requests; i++) {
                try {
                    answer = client.sendAndReceive(data, ip, port);
                    System.out.println("Ricevuto in risposta: " + answer);
                } catch (IOException ignored) { }
            }
        } else {
            try {
                answer = client.sendAndReceive(data, ip, port);
                System.out.println("Ricevuto in risposta: " + answer);
            } catch (IOException exception) {
                System.err.println("Errore: " + exception.getMessage());
            }
        }
        client.closeSocket();
    }
}