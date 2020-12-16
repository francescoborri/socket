package tcp.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer extends Thread {
    private final ServerSocket server;
    private final HashMap<InetSocketAddress, ChatServerThread> connections;

    public ChatServer(String name, int port) throws IOException {
        super(name);
        server = new ServerSocket(port);
        connections = new HashMap<>();
        server.setSoTimeout(1000);
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket socket = server.accept();
                ChatServerThread clientThread = new ChatServerThread(socket.getRemoteSocketAddress().toString(), this, socket);
                connections.put((InetSocketAddress) socket.getRemoteSocketAddress(), clientThread);

                clientThread.start();
            } catch (IOException ignored) {
            }
        }
    }

    public void close() throws IOException {
        this.interrupt();

        for (Map.Entry<InetSocketAddress, ChatServerThread> entry : connections.entrySet()) {
            if (!entry.getValue().isClosed())
                entry.getValue().close();
        }

        server.close();
    }
}
