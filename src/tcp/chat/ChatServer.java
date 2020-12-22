package tcp.chat;

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
        server.setSoTimeout(1000);
        chatServerCommandLine = new ChatServerCommandLine(this);

        chatServerCommandLine.start();
    }

    public void log(String text) throws IOException {
        File log = new File("res/chat.txt");
        boolean append = !log.createNewFile();
        new FileWriter(log, append).append(text).close();
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

    public void recap() {
        chatServerCommandLine.clear();
        for (Map.Entry<InetSocketAddress, ChatClientInformation> entry : clients.entrySet()) {
            System.out.printf("[%s | %s:%d] sent %d request(s)\n",
                    entry.getValue().getName(),
                    entry.getValue().getInetSocketAddress().getAddress().getCanonicalHostName(),
                    entry.getValue().getInetSocketAddress().getPort(),
                    entry.getValue().getRequests()
            );
        }
    }

    public void online() {
        chatServerCommandLine.clear();
        for (Map.Entry<InetSocketAddress, ChatServerThread> entry : connections.entrySet()) {
            if (entry.getValue().isOpened())
                System.out.printf("%s is online\n", entry.getValue().getName());
        }
    }

    public void close() throws IOException {
        this.interrupt();

        for (Map.Entry<InetSocketAddress, ChatServerThread> entry : connections.entrySet()) {
            if (entry.getValue().isOpened())
                entry.getValue().close();
        }

        server.close();
        chatServerCommandLine.close();
    }
}
