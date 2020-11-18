package multiecho;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Server extends Thread {
    private final ArrayList<DatagramPacket> requests = new ArrayList<>();
    private final DatagramSocket socket;

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setSoTimeout(0);
    }

    public void run() {
        byte[] buffer = new byte[8192];
        DatagramPacket request;
        DatagramPacket reply;
        String replyMessage;

        while (!Thread.interrupted()) {
            try {
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                requests.add(request);
                String requestMessage = new String(request.getData(), 0, request.getLength(), StandardCharsets.UTF_8);

                System.out.printf("SERVER RECEIVED FROM %s:%d : %s [%dB]\n", request.getAddress().getCanonicalHostName(), request.getPort(), requestMessage, request.getLength());

                for (DatagramPacket client : requests) {
                    replyMessage = requestMessage;
                    reply = new DatagramPacket(replyMessage.getBytes(), replyMessage.length(), client.getAddress(), client.getPort());
                    System.out.printf("SERVER SENDING TO %s:%d : %s\n", client.getAddress().getCanonicalHostName(), client.getPort(), replyMessage);
                    socket.send(reply);
                }
            } catch (IOException exception) {
                System.err.println(exception.getMessage());
            }
        }
        socket.close();
    }

    public void close() {
        socket.close();
        this.interrupt();
    }
}
