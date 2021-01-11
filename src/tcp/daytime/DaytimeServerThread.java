package tcp.daytime;

import udp.multiecho.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DaytimeServerThread extends Thread {
    private final DaytimeServer server;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean closed;

    public DaytimeServerThread(String name, DaytimeServer server, Socket socket) throws IOException {
        super(name);
        this.server = server;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        closed = false;
    }

    public void run() {
        try {
            String last = readUntilStop();

            close();

            if (last != null && last.equals("shutdown"))
                server.close();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    public String readUntilStop() throws IOException, InterruptedException {
        String request, reply;

        do {
            request = in.readLine();
            reply = manage(request);
            write(reply);
        } while (request != null && !request.equals("close") && !request.equals("shutdown"));

        return request;
    }

    public String manage(String request) throws InterruptedException {
        String reply = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").localizedBy(Locale.ITALY);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss").localizedBy(Locale.ITALY);

        if (request == null)
            return null;

        if (request.startsWith("day_of_week ")) {
            reply = LocalDate.parse(request.split(" ")[1], dateFormatter).getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, Locale.ITALY).toLowerCase(Locale.ROOT);
        } else if (request.startsWith("ping ")) {
            sleep(Integer.parseInt(request.split(" ")[1]));
            reply = "pong";
        }

        if (reply == null) {
            switch (request) {
                case "date":
                    reply = dateFormatter.format(LocalDateTime.now());
                    break;
                case "time":
                    reply = timeFormatter.format(LocalDateTime.now());
                    break;
                case "close":
                    reply = "closing connection...";
                    break;
                case "shutdown":
                    reply = "shutting down server...";
                    break;
                default:
                    reply = "unknown request";
                    break;
            }
        }

        return reply;
    }

    public void write(String reply) {
        if (reply != null)
            out.println(reply);
    }

    public void close() throws IOException {
        socket.shutdownOutput();
        socket.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
