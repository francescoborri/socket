package multiecho;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client extends Thread {
    private final DatagramSocket socket;
    private final DatagramPacket reply;
    private final InetAddress inetAddress;
    private final int port;
    private final int count;
    private static final int SOCKET_TIMEOUT = 5000;

    public Client(String name, int count) throws SocketException, UnknownHostException {
        this(name, "127.0.0.1", 8142, count);
    }

    public Client(String name, String host, int port, int count) throws SocketException, UnknownHostException {
        super(name);
        socket = new DatagramSocket();
        socket.setSoTimeout(SOCKET_TIMEOUT);
        byte[] buffer = new byte[8192];
        reply = new DatagramPacket(buffer, buffer.length);
        this.inetAddress = InetAddress.getByName(host);
        this.port = port;
        this.count = count;
    }

    public void run() {
        send(String.format("Ciao da %s", this.getName()));
        for (int i = 0; i < count; i++)
            receive();
    }

    public void send(String requestMessage) {
        byte[] buffer;
        DatagramPacket request;
        try {
            if (socket.isClosed())
                throw new IOException();

            buffer = requestMessage.getBytes(StandardCharsets.UTF_8);
            request = new DatagramPacket(buffer, buffer.length, inetAddress, port);

            socket.send(request);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void receive() {
        try {
            socket.receive(reply);
            String replyMessage = new String(reply.getData(), 0, reply.getLength(), StandardCharsets.UTF_8);
            System.out.printf("CLIENT %s:%d RECEIVED : %s [%dB]\n", this.getName(), socket.getLocalPort(), replyMessage, reply.getLength());
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
