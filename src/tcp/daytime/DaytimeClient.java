package tcp.daytime;

import java.io.*;
import java.net.*;

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
        timeout = 1000;
        socket.setSoTimeout(timeout);
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

    public String get(String request) throws IOException {
        if (!socket.isConnected())
            throw new IOException();
        out.println(request);
        return in.readLine();
    }
}
