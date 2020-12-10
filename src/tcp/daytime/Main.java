package tcp.daytime;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String serverAddress = "127.0.0.1";
        int serverPort = 55000;

        if (args.length == 2) {
            serverAddress = args[0];
            serverPort = Integer.parseInt(args[1]);
        }

        try {
            DaytimeServer server = new DaytimeServer("main-server", serverPort);
            DaytimeClient client1 = new DaytimeClient(serverAddress, serverPort);
            DaytimeClient client2 = new DaytimeClient(serverAddress, serverPort);

            System.out.println("STARTING SERVER...");
            server.start();

            Thread.sleep(2000);
            System.out.println();

            client1.connect();
            System.out.printf("[CLIENT] %s -> %s\n", "DATE", client1.get("DATE"));
            System.out.printf("[CLIENT] %s -> %s\n", "TIME", client1.get("TIME"));
            System.out.printf("[CLIENT] %s -> %s\n", "CLOSE_SOCKET", client1.get("CLOSE_SOCKET"));
            client1.disconnect();

            Thread.sleep(2000);
            System.out.println();

            client2.connect();
            System.out.printf("[CLIENT] %s -> %s\n", "TIME", client2.get("TIME"));
            System.out.printf("[CLIENT] %s -> %s\n", "DATE", client2.get("DATE"));
            System.out.printf("[CLIENT] %s -> %s\n", "CLOSE_SERVER", client2.get("CLOSE_SERVER"));
            client2.disconnect();

            String temp;
            do {
                temp = scanner.nextLine();
            } while (!temp.equals("stop"));

            server.close();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
