package tcp.chat.client;

import java.io.IOException;

public class ChatClientReader extends Thread {
    private final ChatClient chatClient;

    public ChatClientReader(ChatClient chatClient) {
        super(String.format("%s-reader", chatClient.getName()));
        this.chatClient = chatClient;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                String message = chatClient.receive();
                if (message == null)
                    break;
                System.out.printf("\r%s\n%s", message, chatClient.getPrefix());
            }
        } catch (IOException ignored) {
        }
    }
}
