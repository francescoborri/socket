package tcp.chat;

import java.io.IOException;
import java.util.Scanner;

public class ChatClientWriter extends Thread {
    private final ChatClient chatClient;

    public ChatClientWriter(ChatClient chatClient) {
        super(String.format("%s-writer", chatClient.getName()));
        this.chatClient = chatClient;
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            String request;
            do {
                System.out.print(chatClient.getPrefix());
                request = scanner.nextLine();
                chatClient.send(request);
            } while (!request.equals("exit") && !request.equals("shutdown"));

            chatClient.disconnect();
        } catch (IOException ignored) {
        }
    }
}
