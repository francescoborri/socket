package simpleudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Server {
    private final ClientManager clientManager;
    private final DatagramSocket socket;
    private final DatagramPacket request;

    public Server(int port) throws SocketException {
        clientManager = new ClientManager(500, 60000, 10);
        socket = new DatagramSocket(port);
        socket.setSoTimeout(0);
        byte[] requestBuffer = new byte[8192];
        request = new DatagramPacket(requestBuffer, requestBuffer.length);
    }

    public void listen() {
        byte[] answerBuffer;
        DatagramPacket answer;
        String requestData;
        String answerData;

        long start = System.currentTimeMillis();
        while (true) {
            try {
                socket.receive(request);
                requestData = new String(request.getData(), 0, request.getLength(), StandardCharsets.UTF_8);

                answerData = !clientManager.acceptRequest(request.getAddress()) ?
                        (clientManager.getClientInformation(request.getAddress()).isBanned() ?
                                "You are banned for 1 minute." :
                                "Request blocked.") :
                        "Request accepted.";

                System.out.printf("{ %s | average: %.2fr/s | time: %.2fs } said \"%s\" -> %s\n",
                        request.getAddress().getHostAddress(),
                        clientManager.getClientInformation(request.getAddress()).averageRequestsPerSecond(),
                        (double)(System.currentTimeMillis() - start) / 1000.0,
                        requestData,
                        answerData);

                answerBuffer = answerData.getBytes(StandardCharsets.UTF_8);
                answer = new DatagramPacket(answerBuffer, answerBuffer.length, request.getAddress(), request.getPort());
                socket.send(answer);
            } catch (IOException exception) {
                System.out.printf("[ERROR] -> %s\n", exception.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8142);
        server.listen();
    }
}
