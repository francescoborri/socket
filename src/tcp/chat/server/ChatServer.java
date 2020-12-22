package tcp.chat.server;

import tcp.chat.client.ChatClientInformation;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer extends Thread {
    private final ServerSocket server;
    private final HashMap<InetSocketAddress, ChatClientInformation> clients;
    private final HashMap<InetSocketAddress, ChatServerThread> connections;
    private final ChatServerCommandLine chatServerCommandLine;

    public ChatServer(String name, int port) throws IOException {
        super(name);

        server = new ServerSocket(port);
        clients = new HashMap<>();
        connections = new HashMap<>();
        chatServerCommandLine = new ChatServerCommandLine(this);

        chatServerCommandLine.start();
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                Socket socket = server.accept();
                String name = readName(socket);

                InetSocketAddress clientSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
                ChatClientInformation clientInformation;

                if (clients.containsKey(clientSocketAddress))
                    clientInformation = clients.get(clientSocketAddress);
                else
                    clientInformation = new ChatClientInformation(clientSocketAddress, name);

                ChatServerThread clientThread = new ChatServerThread(name, this, socket, clientInformation);

                clients.put(clientSocketAddress, clientInformation);
                connections.put(clientSocketAddress, clientThread);

                clientThread.start();
            } catch (IOException ignored) {
            }
        }
    }

    public String readName(Socket socket) throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
    }

    public HashMap<InetSocketAddress, ChatClientInformation> getClients() {
        return clients;
    }

    public HashMap<InetSocketAddress, ChatServerThread> getConnections() {
        return connections;
    }

    public ChatServerCommandLine getChatServerCommandLine() {
        return chatServerCommandLine;
    }

    public void broadcast(InetSocketAddress source, String message) {
        for (Map.Entry<InetSocketAddress, ChatServerThread> entry : connections.entrySet()) {
            if (entry.getKey().equals(source))
                continue;

            entry.getValue().send(message);
        }
    }

    public void connectionOnClose(InetSocketAddress key) {
        connections.remove(key);
    }

    public void close() throws IOException {
        this.interrupt();

        for (Map.Entry<InetSocketAddress, ChatServerThread> entry : connections.entrySet()) {
            entry.getValue().close();
        }

        server.close();
        chatServerCommandLine.close();
    }
}
