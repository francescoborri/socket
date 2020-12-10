package udp.simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Server extends Thread {
    private final ClientManager clientManager;
    private final DatagramSocket socket;
    private final DatagramPacket request;

    public Server(int port) throws SocketException {
        clientManager = new ClientManager(500, 60000, 25, 1000);
        socket = new DatagramSocket(port);
        socket.setSoTimeout(0);
        byte[] requestBuffer = new byte[8192];
        request = new DatagramPacket(requestBuffer, requestBuffer.length);
    }

    public void run() {
        byte[] answerBuffer;
        DatagramPacket answer;
        String requestData;
        String answerData;
        String answerStatus;

        long start = System.currentTimeMillis();
        while (!Thread.interrupted()) {
            try {
                socket.receive(request);
                requestData = new String(request.getData(), 0, request.getLength(), StandardCharsets.UTF_8);

                boolean toAccept = clientManager.manageRequest(request.getAddress());
                ClientInformation clientInformation = clientManager.getClientInformation(request.getAddress());
                if (toAccept) {
                    answerData = String.format("Request accepted.\nYou said: %s\nYour average requests per second: %.2fr/s.\nYour IP address: %s.",
                            requestData,
                            clientInformation.averageRequestsPerSecond(),
                            clientInformation.getInetAddress().getHostAddress());
                    answerStatus = "Accepted.";
                } else {
                    if (clientInformation.isBanned()) {
                        answerData = String.format("You are banned for %.2f minute.", clientInformation.getBanDuration() / 60000.0);
                        answerStatus = String.format("Banned for %.2f minute.", clientInformation.getBanDuration() / 60000.0);
                    } else {
                        answerData = "Request blocked.";
                        answerStatus = "Blocked.";
                    }
                }

                System.out.printf("{ %s | average: %.2fr/s | time: %.2fs } said \"%s\" -> %s\n",
                        request.getAddress().getHostAddress(),
                        clientManager.getClientInformation(request.getAddress()).averageRequestsPerSecond(),
                        (double) (System.currentTimeMillis() - start) / 1000.0,
                        requestData,
                        answerStatus);

                answerBuffer = answerData.getBytes(StandardCharsets.UTF_8);
                answer = new DatagramPacket(answerBuffer, answerBuffer.length, request.getAddress(), request.getPort());
                socket.send(answer);
            } catch (IOException exception) {
                System.err.println("Error: " + exception.getMessage());
            }
        }
    }

    public void close() {
        socket.close();
        this.interrupt();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8142);
        Scanner scanner = new Scanner(System.in);
        server.start();
        String temp;
        do {
            temp = scanner.nextLine();
        } while (!temp.equals("stop"));
        server.close();
    }
}
