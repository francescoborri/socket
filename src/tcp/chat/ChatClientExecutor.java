package tcp.chat;

import java.io.IOException;
import java.util.Scanner;

public class ChatClientExecutor {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Server address: ");
        String serverAddress = scanner.nextLine();

        System.out.print("Server port: ");
        int serverPort = Integer.parseInt(scanner.nextLine());

        System.out.print("Your name: ");
        String name = scanner.nextLine();

        ChatClient chatClient = new ChatClient(serverAddress, serverPort, name);
        chatClient.start();

        String request;
        do {
            System.out.print("[you] ");
            request = scanner.nextLine();
            chatClient.send(request);
        } while (!request.equals("exit") && !request.equals("shutdown"));

        chatClient.disconnect();
    }
}
