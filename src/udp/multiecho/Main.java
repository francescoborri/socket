package udp.multiecho;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SocketException {
        Scanner scanner = new Scanner(System.in);
        int wait = 3000;

        try {
            Server server = new Server(8142);
            Client ada = new Client("ADA", 3);
            Client bill = new Client("BILL", 2);
            Client shannon = new Client("SHANNON", 1);

            server.start();

            ada.start();

            Thread.sleep(wait);
            System.out.println();

            bill.start();

            Thread.sleep(wait);
            System.out.println();

            shannon.start();

            Thread.sleep(wait);
            System.out.println();

            String temp;
            do temp = scanner.nextLine();
            while (!temp.equals("stop"));

            server.close();
        } catch (InterruptedException | UnknownHostException exception) {
            System.err.println(exception.getMessage());
        }
    }
}
