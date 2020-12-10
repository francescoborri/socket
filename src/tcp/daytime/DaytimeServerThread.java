package tcp.daytime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

            if (last != null && last.equals("CLOSE_SERVER"))
                server.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String readUntilStop() throws IOException {
        String request, reply;

        do {
            request = in.readLine();
            reply = manage(request);
            write(reply);
        } while (request != null && !request.equals("CLOSE_SOCKET") && !request.equals("CLOSE_SERVER"));

        return request;
    }

    public String manage(String request) {
        String reply;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        if (request == null)
            return null;

        switch (request) {
            case "DATE":
                reply = dateFormatter.format(LocalDateTime.now());
                break;
            case "TIME":
                reply = timeFormatter.format(LocalDateTime.now());
                break;
            case "CLOSE_SOCKET":
                reply = "CLOSING SOCKET...";
                break;
            case "CLOSE_SERVER":
                reply = "CLOSING SERVER...";
                break;
            default:
                reply = "UNKNOWN REQUEST";
                break;
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
