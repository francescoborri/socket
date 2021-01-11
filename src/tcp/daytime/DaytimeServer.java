package tcp.daytime;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class DaytimeServer extends Thread {
    private final ServerSocket server;
    private final HashMap<InetSocketAddress, DaytimeServerThread> connections;

    public DaytimeServer(String name, int port) throws IOException {
        super(name);
        server = new ServerSocket(port);
        connections = new HashMap<>();
        server.setSoTimeout(1000);
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket socket = server.accept();

                System.out.printf("[server] new client -> %s:%d\n", socket.getInetAddress().getCanonicalHostName(), socket.getPort());

                DaytimeServerThread daytimeServerThread = new DaytimeServerThread(socket.getRemoteSocketAddress().toString(), this, socket);
                connections.put((InetSocketAddress) socket.getRemoteSocketAddress(), daytimeServerThread);
                daytimeServerThread.start();
            } catch (IOException ignored) {
            }
        }
    }

    public void close() throws IOException {
        this.interrupt();

        for (Map.Entry<InetSocketAddress, DaytimeServerThread> entry : connections.entrySet()) {
            if (!entry.getValue().isClosed())
                entry.getValue().close();
        }

        server.close();
    }
}
