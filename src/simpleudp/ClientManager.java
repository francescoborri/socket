package simpleudp;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;

public class ClientManager {
    private final HashMap<InetAddress, ClientInformation> log;
    private final int TIME_TO_WAIT;
    private final int TIME_TO_UNBAN;
    private final int MAX_REQUEST_UNTIL_BAN;

    public ClientManager(int TIME_TO_WAIT, int TIME_TO_UNBAN, int MAX_REQUEST_UNTIL_BAN) {
        this.log = new HashMap<>();
        this.TIME_TO_WAIT = TIME_TO_WAIT;
        this.TIME_TO_UNBAN = TIME_TO_UNBAN;
        this.MAX_REQUEST_UNTIL_BAN = MAX_REQUEST_UNTIL_BAN;
    }

    public boolean acceptRequest(InetAddress inetAddress) {
        boolean accept;
        ClientInformation clientInformation;
        if (log.containsKey(inetAddress)) {
            clientInformation = log.get(inetAddress);
            if (clientInformation.isBanned() && System.currentTimeMillis() - clientInformation.getLastRequest() > TIME_TO_UNBAN) {
                clientInformation.setBanned(false);
                clientInformation.setRejectedRequests(0);
                clientInformation.newRequest();
                accept = true;
            } else if (clientInformation.isBanned() || System.currentTimeMillis() - clientInformation.getLastRequest() < TIME_TO_WAIT) {
                clientInformation.newRejectedRequest();
                if (clientInformation.getRejectedRequests() >= MAX_REQUEST_UNTIL_BAN && !clientInformation.isBanned())
                    clientInformation.setBanned(true);
                accept = false;
            } else {
                clientInformation.newRequest();
                accept = true;
            }
        } else {
            clientInformation = new ClientInformation(inetAddress, System.currentTimeMillis());
            clientInformation.newRequest();
            accept = true;
            log.put(inetAddress, clientInformation);
        }
        clientInformation.setLastRequest(System.currentTimeMillis());
        return accept;
    }

    public ClientInformation getClientInformation(InetAddress inetAddress) {
        return log.get(inetAddress);
    }
}
