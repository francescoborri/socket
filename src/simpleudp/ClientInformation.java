package simpleudp;

import java.net.InetAddress;
import java.util.Objects;

public class ClientInformation {
    private final InetAddress inetAddress;
    private int totalRequests;
    private int totalRejectedRequests;
    private int rejectedRequests;
    private final long firstRequest;
    private long lastRequest;
    private boolean banned;
    private long banDuration;

    public ClientInformation(InetAddress inetAddress, long firstRequest, long banDuration) {
        this.inetAddress = inetAddress;
        totalRequests = 0;
        totalRejectedRequests = 0;
        rejectedRequests = 0;
        this.firstRequest = firstRequest;
        lastRequest = 0;
        banned = false;
        this.banDuration = banDuration;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void newRequest() {
        totalRequests++;
    }

    public int getTotalRejectedRequests() {
        return totalRejectedRequests;
    }

    public int getRejectedRequests() {
        return rejectedRequests;
    }

    public void newRejectedRequest() {
        totalRequests++;
        totalRejectedRequests++;
        rejectedRequests++;
    }

    public void setRejectedRequests(int rejectedRequests) {
        this.rejectedRequests = rejectedRequests;
    }

    public long getFirstRequest() {
        return firstRequest;
    }

    public long getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(long lastRequest) {
        this.lastRequest = lastRequest;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public long getBanDuration() {
        return banDuration;
    }

    public void setBanDuration(long banDuration) {
        this.banDuration = banDuration;
    }

    public double averageRequestsPerSecond() {
        double averageRequestsPerSecond;
        if (totalRequests == 1) averageRequestsPerSecond = 0.0;
        else averageRequestsPerSecond = (double) (totalRequests) * 1000.0 / (double) (lastRequest - firstRequest);
        return averageRequestsPerSecond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInformation that = (ClientInformation) o;
        return Objects.equals(inetAddress, that.inetAddress);
    }
}
