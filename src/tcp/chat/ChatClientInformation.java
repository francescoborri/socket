package tcp.chat;

import java.net.InetSocketAddress;

public class ChatClientInformation {
    private final InetSocketAddress inetSocketAddress;
    private final String name;
    private int requests;

    public ChatClientInformation(InetSocketAddress inetSocketAddress, String name) {
        this.inetSocketAddress = inetSocketAddress;
        this.name = name;
        requests = 0;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public String getName() {
        return name;
    }

    public int getRequests() {
        return requests;
    }

    public void newRequest() {
        requests++;
    }
}
