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
    private final ChatClientReader reader;
    private final ChatClientWriter writer;
    private final String name;
    private final String prefix;

    public ChatClient(String serverAddress, int serverPort, String name) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(serverAddress, serverPort));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        reader = new ChatClientReader(this);
        writer = new ChatClientWriter(this);
        this.name = name;
        prefix = "[you]: ";

        reader.start();
        writer.start();

        send(name);
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void disconnect() throws IOException {
        reader.interrupt();
        writer.interrupt();
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
