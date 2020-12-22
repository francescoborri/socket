package tcp.chat.server;

import tcp.chat.client.ChatClientInformation;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class ChatServerCommandLine extends Thread {
    private final ChatServer chatServer;
    private final Scanner cli;

    public ChatServerCommandLine(ChatServer chatServer) {
        super("chat-server-cli");
        this.chatServer = chatServer;
        cli = new Scanner(System.in);
    }

    public void run() {
        try {
            String cmd;
            do {
                System.out.printf("[%s]: ", getPrefix());
                cmd = cli.nextLine();
                manage(cmd);
            } while (!cmd.equals("shutdown") && !Thread.interrupted());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void manage(String cmd) throws IOException {
        switch (cmd) {
            case "shutdown":
                chatServer.close();
                break;
            case "recap":
                recap();
                break;
            case "online":
                online();
                break;
        }
    }

    public String getPrefix() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public void recap() {
        for (Map.Entry<InetSocketAddress, ChatClientInformation> entry : chatServer.getClients().entrySet()) {
            System.out.printf("[%s | %s:%d] sent %d request(s)\n",
                    entry.getValue().getName(),
                    entry.getValue().getInetSocketAddress().getAddress().getCanonicalHostName(),
                    entry.getValue().getInetSocketAddress().getPort(),
                    entry.getValue().getRequests()
            );
        }
    }

    public void online() {
        for (Map.Entry<InetSocketAddress, ChatServerThread> entry : chatServer.getConnections().entrySet()) {
            System.out.printf("%s is online\n", entry.getValue().getName());
        }
    }

    public void close() throws IOException {
        System.out.println("shutting down server...");
        this.interrupt();
    }
}
