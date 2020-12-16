package tcp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatServerThread extends Thread {
    private final ChatServer server;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean closed;

    public ChatServerThread(String name, ChatServer server, Socket socket) throws IOException {
        super(name);
        this.server = server;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        closed = false;
    }

    public void run() {
        try {
            String last = receive();

            close();

            if (last != null && last.equals("CLOSE_SERVER"))
                server.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String receive() throws IOException {
        String request, reply;

        do {
            request = in.readLine();
            log(request);
            reply = manage(request);
            reply(reply);
        } while (request != null && !request.equals("CLOSE_CONNECTION") && !request.equals("CLOSE_SERVER"));

        return request;
    }

    public String manage(String request) {
        String reply;

        if (request == null)
            return null;

        switch (request) {
            case "CLOSE_CONNECTION":
                reply = "CLOSING CONNECTION...";
                break;
            case "CLOSE_SERVER":
                reply = "CLOSING SERVER...";
                break;
            default:
                reply = "OK";
                break;
        }

        return reply;
    }

    public void reply(String reply) {
        if (reply != null)
            out.println(reply);
    }

    public void close() throws IOException {
        socket.shutdownOutput();
        socket.close();
        closed = true;
    }

    public void log(String request) {
        String clientAddress = ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName();
        int clientPort = ((InetSocketAddress) socket.getRemoteSocketAddress()).getPort();
        System.out.printf("[%s:%d]: %s\n", clientAddress, clientPort, request);
    }

    public boolean isClosed() {
        return closed;
    }
}
