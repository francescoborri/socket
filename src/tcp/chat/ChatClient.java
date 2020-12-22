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
    private static final int timeout = 1000;

    public ChatClient(String serverAddress, int serverPort, String name) throws IOException {
        super(name);

        socket = new Socket();
        socket.setSoTimeout(timeout);
        socket.connect(new InetSocketAddress(serverAddress, serverPort), timeout);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        send(getName());
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                String message = receive();
                System.out.println("\n" + message);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void disconnect() throws IOException {
        if (!socket.isConnected())
            throw new IOException();

        this.interrupt();
        socket.shutdownOutput();
        socket.close();
    }

    public void send(String request) throws IOException {
        if (!socket.isConnected())
            throw new IOException();

        out.println(request);
    }

    public String receive() throws IOException {
        return in.readLine();
    }
}
