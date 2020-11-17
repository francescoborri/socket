package multiecho;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

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

                System.out.printf("SERVER RECEIVED FROM %s:%d -> [%dB] - %s\n\n", request.getAddress().getCanonicalHostName(), request.getPort(), request.getLength(), requestMessage);

                for (DatagramPacket client : requests) {
                    replyMessage = requestMessage;
                    reply = new DatagramPacket(replyMessage.getBytes(), replyMessage.length(), client.getAddress(), client.getPort());
                    System.out.printf("SERVER SENDING TO %s:%d -> MESSAGE: %s\n\n", client.getAddress().getCanonicalHostName(), client.getPort(), replyMessage);
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

    public static void main(String[] args) throws SocketException {
        Scanner scanner = new Scanner(System.in);
        int wait = 3000;

        try {
            Server server = new Server(8142);
            server.start();

            sleep(wait);

            Client ada = new Client("ADA", 3);
            ada.start();

            sleep(wait);

            Client bill = new Client("BILL", 2);
            bill.start();

            sleep(wait);

            Client shannon = new Client("SHANNON", 1);
            shannon.start();

            String temp;
            do {
                temp = scanner.nextLine();
            } while (!temp.equals("stop"));

            server.close();
        } catch (InterruptedException | UnknownHostException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
