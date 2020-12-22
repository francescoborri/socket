package tcp.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClient {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String name;
    private final String prefix;

    public ChatClient(String serverAddress, int serverPort, String name) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(serverAddress, serverPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.name = name;
        prefix = "[you]: ";

        new ChatClientReader(this).start();
        new ChatClientWriter(this).start();

        send(name);
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void disconnect() throws IOException {
        socket.shutdownOutput();
        socket.close();
    }

    public void send(String request) throws IOException {
        out.println(request);
    }

    public String receive() throws IOException {
        return in.readLine();
    }
}
