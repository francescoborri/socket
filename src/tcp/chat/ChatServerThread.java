package tcp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatServerThread extends Thread {
    private final ChatServer server;
    private final ChatClientInformation clientInformation;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean closed;

    public ChatServerThread(String name, ChatServer server, Socket socket, ChatClientInformation clientInformation) throws IOException {
        super(name);
        this.server = server;
        this.socket = socket;
        this.clientInformation = clientInformation;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        closed = false;

        server.log(String.format("%s joined the chat\n", clientInformation.getName()));
    }

    public void run() {
        try {
            String last = receive();
            close();

            if (last != null && last.equals("shutdown"))
                server.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String receive() throws IOException {
        String request, reply;

        do {
            request = in.readLine();
            clientInformation.newRequest();

            reply = manage(request);
            reply(reply);
        } while (request != null && !request.equals("exit") && !request.equals("shutdown"));

        return request;
    }

    public String manage(String request) throws IOException {
        String reply;

        if (request == null)
            return null;

        switch (request) {
            case "exit":
                reply = "closing connection...";
                break;
            case "shutdown":
                reply = "shutdown server...";
                break;
            default:
                reply = "ok";
                break;
        }

        if (reply.equals("ok"))
            log(request);

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
        server.log(String.format("%s left the chat\n", clientInformation.getName()));
    }

    public void log(String request) throws IOException {
        server.log(String.format("[%s]: %s\n", getName(), request));
    }

    public boolean isOpened() {
        return !closed;
    }
}
