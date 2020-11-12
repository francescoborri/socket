package simpleudp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {
    private final DatagramSocket socket;
    private final byte[] answerBuffer = new byte[8192];
    private final DatagramPacket answer = new DatagramPacket(answerBuffer, answerBuffer.length);
    private final InetAddress inetAddress;
    private final int port;
    private final String requestData;
    private final boolean spam;
    private final int requests;

    public Client(String host, int port, String requestData, boolean spam, int requests) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);
        this.inetAddress = InetAddress.getByName(host);
        this.port = port;
        this.requestData = requestData;
        this.spam = spam;
        this.requests = requests;
    }

    public void run() {
        if (spam) for (int i = 0; i < requests; i++) {
            try {
                System.out.println("Answer: " + this.sendAndReceive());
            } catch (IOException ignored) { }
        } else try {
            System.out.println("Answer: " + this.sendAndReceive());
        } catch (IOException exception) {
            System.err.println("Error: " + exception.getMessage());
        }
        socket.close();
    }

    public String sendAndReceive() throws IOException {
        if (socket.isClosed())
            throw new IOException();

        String answerData;
        byte[] requestBuffer = requestData.getBytes(StandardCharsets.UTF_8);
        DatagramPacket request = new DatagramPacket(requestBuffer, requestBuffer.length, inetAddress, port);

        socket.send(request);
        socket.receive(answer);

        if (answer.getAddress().equals(inetAddress) && answer.getPort() == port)
            answerData = new String(answer.getData(), 0, answer.getLength(), StandardCharsets.UTF_8);
        else
            throw new SocketTimeoutException();
        return answerData;
    }

    public static void main(String[] args) throws IOException {
        String host, data;
        int port, requests;
        boolean spam;

        if (args.length != 5) {
            host = "127.0.0.1";
            port = 8142;
            data = "Hello, world!";
            spam = false;
            requests = Integer.MAX_VALUE;
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
            data = args[2];
            spam = Boolean.parseBoolean(args[3]);
            requests = Integer.parseInt(args[4]);
        }

        Client client = new Client(host, port, data, spam, requests);
        client.start();
    }
}