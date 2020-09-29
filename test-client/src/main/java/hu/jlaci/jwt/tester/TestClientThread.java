package hu.jlaci.jwt.tester;

import hu.jlaci.jwt.AuthResponse;
import hu.jlaci.jwt.TestConfiguration;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Slf4j
public class TestClientThread implements Runnable {
    // Client variables
    private long id;
    private TestUtilityService service;
    private TestResultService resultService;
    private boolean stopped;
    private Random random;
    private Instant simulationEnd;
    private TestResults testResults;

    // State variables
    private String accessToken;
    private String refreshToken;
    private Instant logOutTime;
    private Instant lastResourceAccess;


    public TestClientThread(int id, TestUtilityService service, TestResultService resultService, Instant simulationEnd) {
        this.id = id;
        this.service = service;
        this.resultService = resultService;
        this.simulationEnd = simulationEnd;
        this.stopped = false;
        this.testResults = new TestResults();
        setUpAsNewClient();
    }

    private void setUpAsNewClient() {
        this.random = new Random();

        // Get an initial token
        this.testResults.tokenRequested();
        AuthResponse authResponse = service.login("user" + id, "password");
        this.testResults.tokenReceived();
        this.testResults.loggedIn();

        this.accessToken = authResponse.getAccessToken();
        this.refreshToken = authResponse.getRefreshToken();
        this.logOutTime = Instant.now().plus((long) (TestConfiguration.SystemCharacteristics.T_RVK * random.nextDouble() * 2), ChronoUnit.MILLIS);
        this.lastResourceAccess = Instant.now().minus((long)((1 / TestConfiguration.SystemCharacteristics.RESOURCE_ACCESS_FREQUENCY) * random.nextDouble()), ChronoUnit.SECONDS); // So clients dont try to access the reosurces at the same time
    }

    @Override
    public void run() {
        while (!stopped && Instant.now().isBefore(simulationEnd)) {
            if (Instant.now().isAfter(logOutTime)) {
                // Check if we have to logout
                log.info("Client " + id + " logging out");
                service.logout(id, accessToken);
                testResults.loggedOut();
                setUpAsNewClient();
            } else if (timeForResourceAccess()) {
                // Check if we have to consume a resource
                log.info("Client {} trying to access a protected service.", id);
                lastResourceAccess = Instant.now();

                boolean fooConsumed = random.nextBoolean();
                if (fooConsumed) {
                    testResults.fooAccessRequested();
                } else {
                    testResults.barAccessRequested();
                }
                doResourceAccess(fooConsumed);
            }

            // Sleep after finishing all sim tasks
            try {
                Thread.sleep(TestConfiguration.SimulationParameters.TIME_STEP);
            } catch (InterruptedException e) {
                log.warn("Thread sleep interrupted!", e);
            }
        }
        log.info("Thread for client {} stopped", id);
        resultService.addResult(id, testResults);
    }

    public void stop() {
        log.info("Thread for client {} stopping...", id);
        this.stopped = true;
    }

    private boolean timeForResourceAccess() {
        if (lastResourceAccess == null) {
            return true;
        } else {
            // If the elapsed time is more than the resoruce access frequency
            return (Instant.now().toEpochMilli() - lastResourceAccess.toEpochMilli()) / 1000.0 > (1 / TestConfiguration.SystemCharacteristics.RESOURCE_ACCESS_FREQUENCY);
        }
    }

    private void doResourceAccess(boolean fooConsumed) {
        boolean result;
        if (fooConsumed) {
            result = service.consumeFooService(accessToken);
        } else {
            result = service.consumeBarService(accessToken);
        }

        // Check if the consumption succeeded
        if (result) {
            log.info("Client {} has successfully consumed a protected service.", id);
            if (fooConsumed) {
                testResults.fooAccessReceived();
            } else {
                testResults.barAccessReceived();
            }
        } else {
            log.info("Client {} has failed to access a protected service, refreshing token.", id);

            this.testResults.tokenRequested();
            AuthResponse authResponse = service.exchangeRefreshToken(refreshToken);
            this.testResults.tokenReceived();

            this.accessToken = authResponse.getAccessToken();
            this.refreshToken = authResponse.getRefreshToken();
            doResourceAccess(fooConsumed);
        }
    }
}
