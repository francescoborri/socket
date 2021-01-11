package tcp.daytime;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DaytimeClient {
    private final InetSocketAddress serverSocketAddress;
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final int timeout;

    public DaytimeClient(String serverAddress, int serverPort) throws IOException {
        serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);
        socket = new Socket();
        in = null;
        out = null;
        timeout = 0;
        socket.setSoTimeout(timeout);
    }

    private String getTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public void connect() throws IOException {
        if (socket.isConnected())
            throw new IOException();
        socket.connect(serverSocketAddress, timeout);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void disconnect() throws IOException {
        if (!socket.isConnected())
            throw new IOException();
        in = null;
        out = null;
        socket.shutdownOutput();
        socket.close();
    }

    public void sendReceive(String request) throws IOException {
        sendReceive(request, false);
    }

    public void sendReceive(String request, boolean printTime) throws IOException {
        if (!socket.isConnected())
            throw new IOException();
        out.println(request);
        if (printTime) System.out.printf("[client] %s(%s) -> %s(%s)\n", request, getTime(), in.readLine(), getTime());
        else System.out.printf("[client] %s -> %s\n", request, in.readLine());
    }
}
