package tcp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClient {
    private final InetSocketAddress serverSocketAddress;
    private final Socket socket;
    private final String name;
    private BufferedReader in;
    private PrintWriter out;
    private final int timeout;

    public ChatClient(String serverAddress, int serverPort, String name) throws IOException {
        serverSocketAddress = new InetSocketAddress(serverAddress, serverPort);
        socket = new Socket();
        this.name = name;
        in = null;
        out = null;
        timeout = 1000;
        socket.setSoTimeout(timeout);
    }

    public String getName() {
        return name;
    }

    public void connect() throws IOException {
        if (socket.isConnected())
            throw new IOException();
        socket.connect(serverSocketAddress, timeout);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        send(name, false);
    }

    public void disconnect() throws IOException {
        if (!socket.isConnected())
            throw new IOException();
        in = null;
        out = null;
        socket.shutdownOutput();
        socket.close();
    }

    public String send(String request, boolean waitForReply) throws IOException {
        if (!socket.isConnected())
            throw new IOException();
        out.println(request);
        return waitForReply ? in.readLine() : null;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }
}
