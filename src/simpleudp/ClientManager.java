package simpleudp;

import java.net.InetAddress;
import java.util.HashMap;

public class ClientManager {
    private final HashMap<InetAddress, ClientInformation> log;
    private final int TIME_TO_WAIT;
    private final int DEFAULT_BAN_DURATION;
    private final int MAX_REQUEST_UNTIL_BAN;

    public ClientManager(int TIME_TO_WAIT, int DEFAULT_BAN_DURATION, int MAX_REQUEST_UNTIL_BAN) {
        this.log = new HashMap<>();
        this.TIME_TO_WAIT = TIME_TO_WAIT;
        this.DEFAULT_BAN_DURATION = DEFAULT_BAN_DURATION;
        this.MAX_REQUEST_UNTIL_BAN = MAX_REQUEST_UNTIL_BAN;
    }

    public boolean manageRequest(InetAddress inetAddress) {
        boolean toAccept;
        ClientInformation clientInformation;

        if (!log.containsKey(inetAddress))
            return newClient(inetAddress);

        clientInformation = log.get(inetAddress);

        if (clientInformation.isBanned() && System.currentTimeMillis() - clientInformation.getLastRequest() > clientInformation.getBanDuration())
            toAccept = unban(clientInformation);
        else if (clientInformation.isBanned())
            toAccept = checkBan(clientInformation);
        else if (System.currentTimeMillis() - clientInformation.getLastRequest() < TIME_TO_WAIT)
            toAccept = blockAndCheckForBan(clientInformation);
        else
            toAccept = accept(clientInformation);

        clientInformation.setLastRequest(System.currentTimeMillis());
        return toAccept;
    }

    public boolean accept(ClientInformation clientInformation) {
        clientInformation.newRequest();
        return true;
    }

    public boolean blockAndCheckForBan(ClientInformation clientInformation) {
        clientInformation.newRejectedRequest();
        if (clientInformation.getRejectedRequests() >= MAX_REQUEST_UNTIL_BAN)
            clientInformation.setBanned(true);
        return false;
    }

    public boolean checkBan(ClientInformation clientInformation) {
        clientInformation.newRejectedRequest();
        if (System.currentTimeMillis() - clientInformation.getLastRequest() < TIME_TO_WAIT)
            clientInformation.setBanDuration(clientInformation.getBanDuration() + DEFAULT_BAN_DURATION);
        return false;
    }

    public boolean unban(ClientInformation clientInformation) {
        clientInformation.setBanned(false);
        clientInformation.setRejectedRequests(0);
        clientInformation.setBanDuration(DEFAULT_BAN_DURATION);
        clientInformation.newRequest();
        return true;
    }

    public boolean newClient(InetAddress inetAddress) {
        ClientInformation clientInformation = new ClientInformation(inetAddress, System.currentTimeMillis(), DEFAULT_BAN_DURATION);
        clientInformation.newRequest();
        log.put(inetAddress, clientInformation);
        return true;
    }

    public ClientInformation getClientInformation(InetAddress inetAddress) {
        return log.get(inetAddress);
    }
}
