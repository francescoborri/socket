package tcp.daytime;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 55000;

        try {
            DaytimeServer server = new DaytimeServer("main-server", serverPort);
            DaytimeClient client1 = new DaytimeClient(serverAddress, serverPort);
            DaytimeClient client2 = new DaytimeClient(serverAddress, serverPort);
            DaytimeClient client3 = new DaytimeClient(serverAddress, serverPort);
            DaytimeClient client4 = new DaytimeClient(serverAddress, serverPort);

            System.out.println("starting server...");
            server.start();

            Thread.sleep(2000);
            System.out.println();

            client1.connect();
            client1.sendReceive("date");
            client1.sendReceive("time");
            client1.sendReceive("close");
            client1.disconnect();

            Thread.sleep(2000);
            System.out.println();

            client2.connect();
            client2.sendReceive("day_of_week 11/01/2021");
            client2.sendReceive("close");
            client2.disconnect();

            Thread.sleep(2000);
            System.out.println();

            client3.connect();
            client3.sendReceive("ping 2000", true);
            client3.sendReceive("ping 6000", true);
            client3.sendReceive("close");
            client3.disconnect();

            Thread.sleep(2000);
            System.out.println();

            client4.connect();
            client4.sendReceive("shutdown");
            client4.disconnect();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
