package udp.simpleudp;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {
    private final DatagramSocket socket;
    private final DatagramPacket answer;
    private final InetAddress inetAddress;
    private final int port;
    private final String requestData;
    private final boolean spam;
    private final int pause;
    private final int requests;

    public Client(String host, int port, String requestData, boolean spam, int pause, int requests) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);
        byte[] answerBuffer = new byte[8192];
        answer = new DatagramPacket(answerBuffer, answerBuffer.length);
        this.inetAddress = InetAddress.getByName(host);
        this.port = port;
        this.requestData = requestData;
        this.spam = spam;
        this.pause = pause;
        this.requests = requests;
    }

    public void run() {
        if (spam) for (int i = 0; i < requests; i++) {
            try {
                System.out.println("Answer: " + this.sendAndReceive());
                if (pause > 0)
                    sleep(pause);
            } catch (IOException | InterruptedException ignored) { }
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
        int port, requests, pause;
        boolean spam;

        if (args.length != 6) {
            host = "127.0.0.1";
            port = 8142;
            data = "Hello, world!";
            spam = true;
            pause = 499;
            requests = Integer.MAX_VALUE;
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
            data = args[2];
            spam = Boolean.parseBoolean(args[3]);
            pause = Integer.parseInt(args[4]);
            requests = Integer.parseInt(args[5]);
        }

        Client client = new Client(host, port, data, spam, pause, requests);
        client.start();
    }
}