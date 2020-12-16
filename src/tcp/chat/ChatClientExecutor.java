package tcp.chat;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class ChatClientExecutor extends Thread {
    private final ChatClient chatClient;
    private final String[] requests;

    public ChatClientExecutor(ChatClient chatClient, String[] requests) {
        this.chatClient = chatClient;
        this.requests = requests;
    }

    public void run() {
        try {
            chatClient.connect();

            while (true) {
                for (String request : requests)
                    System.out.printf("[%s] %s -> %s\n", chatClient.getName(), request, chatClient.send(request));

                if (ThreadLocalRandom.current().nextInt(1000) == 0) {
                    chatClient.send("CLOSE_CONNECTION");
                    break;
                }
            }

            chatClient.disconnect();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Number of clients: ");
        int numberOfClients = scanner.nextInt();

        System.out.print("Server address: ");
        String serverAddress = scanner.next();

        System.out.print("Server port: ");
        int serverPort = scanner.nextInt();

        try {
            for (int i = 0; i < numberOfClients; i++) {
                ChatClient chatClient = new ChatClient(serverAddress, serverPort, String.format("client-%d", i));
                ChatClientExecutor chatClientExecutor = new ChatClientExecutor(chatClient, new String[]{"DATE", "TIME"});
                chatClientExecutor.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
