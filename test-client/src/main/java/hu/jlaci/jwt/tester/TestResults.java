package hu.jlaci.jwt.tester;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestResults {
    private int logins;
    private int logouts;

    private int fooAccess;
    private long totalFooAccessTime;

    private int barAccess;
    private long totalBarAccessTime;

    private int tokenRequests;
    private long tokenRequestsTime;

    // State variables
    private Instant fooAccessRequested;
    private Instant barAccessRequested;
    private Instant tokenRequestSent;
    private Instant tokenReceived;

    public void loggedIn() {
        this.logins++;
    }

    public void  loggedOut() {
        this.logouts++;
    }

    public void tokenRequested() {
        this.tokenRequests++;
        if (this.tokenRequestSent != null) {
            throw new IllegalStateException("Token request was sent while previous was pending!");
        }
        this.tokenRequestSent = Instant.now();
    }

    public void tokenReceived() {
        Instant tokenReceived = Instant.now();
        if (tokenRequestSent == null) {
            throw new IllegalStateException("Token acquired without request timer set!");
        }
        this.tokenRequestsTime += tokenReceived.toEpochMilli() - tokenRequestSent.toEpochMilli();
        this.tokenRequestSent = null;
    }

    public void fooAccessRequested() {
        this.fooAccess++;
        if (this.fooAccessRequested != null) {
            throw new IllegalStateException("Foo access requested while previous was pending!");
        }
        this.fooAccessRequested = Instant.now();
    }

    public void fooAccessReceived() {
        Instant fooAccessReceived = Instant.now();
        if (fooAccessRequested == null) {
            throw new IllegalStateException("Foo access received without request timer set!");
        }
        this.totalFooAccessTime += fooAccessReceived.toEpochMilli() - fooAccessRequested.toEpochMilli();
        this.fooAccessRequested = null;
    }

    public void barAccessRequested() {
        this.barAccess++;
        if (this.barAccessRequested != null) {
            throw new IllegalStateException("Bar access requested while previous was pending!");
        }
        this.barAccessRequested = Instant.now();
    }

    public void barAccessReceived() {
        Instant barAccessReceived = Instant.now();
        if (barAccessRequested == null) {
            throw new IllegalStateException("Bar access received without request timer set!");
        }
        this.totalBarAccessTime += barAccessReceived.toEpochMilli() - barAccessRequested.toEpochMilli();
        this.barAccessRequested = null;
    }

    public void add(TestResults value) {
        this.logins += value.getLogins();
        this.logouts += value.getLogouts();
        this.fooAccess += value.getFooAccess();
        this.totalFooAccessTime += value.getTotalFooAccessTime();
        this.barAccess += value.getBarAccess();
        this.totalBarAccessTime += value.getTotalBarAccessTime();
        this.tokenRequests += value.getTokenRequests();
        this.tokenRequestsTime += value.getTokenRequestsTime();
    }

    @Override
    public String toString() {
        return "TestResults{" +
                "logins=" + logins +
                ", logouts=" + logouts +
                ", fooAccess=" + fooAccess +
                ", totalFooAccessTime=" + totalFooAccessTime +
                ", barAccess=" + barAccess +
                ", totalBarAccessTime=" + totalBarAccessTime +
                ", tokenRequests=" + tokenRequests +
                ", tokenRequestsTime=" + tokenRequestsTime +
                '}';
    }
}
