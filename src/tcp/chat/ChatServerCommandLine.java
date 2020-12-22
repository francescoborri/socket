package tcp.chat;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatServerCommandLine extends Thread {
    private final ChatServer chatServer;
    private final BufferedReader cli;

    public ChatServerCommandLine(ChatServer chatServer) {
        super("chat-server-cli");
        this.chatServer = chatServer;
        cli = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        try {
            String cmd;
            do {
                System.out.printf("[%s] ", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
                cmd = cli.readLine();
                manage(cmd);
            } while (!cmd.equals("shutdown") && !Thread.interrupted());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void clear() {
        System.out.print("\b\b\b\b\b\b\b\b");
    }

    private void manage(String cmd) throws IOException {
        switch (cmd) {
            case "shutdown":
                chatServer.close();
                break;
            case "recap":
                chatServer.recap();
                break;
            case "who_is_online":
                chatServer.online();
                break;
            default:
                break;
        }
    }

    public void close() throws IOException {
        this.interrupt();
    }
}
