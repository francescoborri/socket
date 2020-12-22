package tcp.chat;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class ChatClientSpamExecutor extends Thread {
    private final ChatClient chatClient;
    private final String[] requests;
    private final double closingPercentage;

    public ChatClientSpamExecutor(ChatClient chatClient, String[] requests, double closingPercentage) {
        this.chatClient = chatClient;
        this.requests = requests;
        this.closingPercentage = closingPercentage;
    }

    public void run() {
        try {
            while (true) {
                boolean close = ThreadLocalRandom.current().nextDouble() <= closingPercentage;

                String request = close ? "exit" : requests[ThreadLocalRandom.current().nextInt(requests.length)];
                chatClient.send(request);
                System.out.printf("[%s]: %s\n", chatClient.getName(), request);

                if (close)
                    break;
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
            String[] requests = {
                    "Hello, World!",
                    "Message sample",
                    "Hello there"
            };

            for (int i = 0; i < numberOfClients; i++) {
                ChatClient chatClient = new ChatClient(serverAddress, serverPort, String.format("client-%d", i));
                ChatClientSpamExecutor chatClientSpamExecutor = new ChatClientSpamExecutor(chatClient, requests, 0.01);
                chatClientSpamExecutor.start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
