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

    public ChatServerThread(String name, ChatServer server, Socket socket, ChatClientInformation clientInformation) throws IOException {
        super(name);
        this.server = server;
        this.socket = socket;
        this.clientInformation = clientInformation;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        server.broadcast(
                clientInformation.getInetSocketAddress(),
                (String.format("%s joined the chat\n", clientInformation.getName()))
        );
    }

    public void run() {
        try {
            String last = receive();
            close();

            if (last != null && last.equals("shutdown"))
                server.close();
        } catch (IOException ignored) {
        }
    }

    public void send(String message) {
        out.println(message);
    }

    public String receive() throws IOException {
        String request;

        do {
            request = in.readLine();
            clientInformation.newRequest();
            manage(request);
        } while (request != null && !request.equals("exit") && !request.equals("shutdown"));

        return request;
    }

    public void manage(String request) {
        if (request == null)
            return;

        switch (request) {
            case "exit":
            case "shutdown":
                break;
            default:
                server.broadcast(
                        clientInformation.getInetSocketAddress(),
                        String.format("[%s]: %s\n", getName(), request)
                );
                break;
        }
    }

    public void close() throws IOException {
        server.connectionOnClose(clientInformation.getInetSocketAddress());
        socket.shutdownOutput();
        socket.close();
        server.broadcast(
                clientInformation.getInetSocketAddress(),
                String.format("%s left the chat\n", clientInformation.getName())
        );
    }
}
