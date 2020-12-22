package tcp.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ChatClient extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public ChatClient(String serverAddress, int serverPort, String name) throws IOException {
        super(name);

        socket = new Socket();
        socket.connect(new InetSocketAddress(serverAddress, serverPort));

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        send(getName());
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                String message = receive();

                if (message == null)
                    break;

                System.out.println(message);
            }
        } catch (IOException ignored) {
        }
    }

    public void disconnect() throws IOException {
        this.interrupt();
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
