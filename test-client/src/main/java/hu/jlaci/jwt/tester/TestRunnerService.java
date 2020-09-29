package hu.jlaci.jwt.tester;

import hu.jlaci.jwt.TestConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class TestRunnerService {
    private TestUtilityService service;
    private TestResultService resultService;
    private final List<TestClientThread> simulationThreads = new ArrayList<>();

    public TestRunnerService(TestUtilityService service, TestResultService resultService) {
        this.service = service;
        this.resultService = resultService;
    }

    @PostConstruct
    public void runTest() {
        Instant simulationEnd = Instant.now().plus(TestConfiguration.SimulationParameters.SIMULATION_LENGTH, ChronoUnit.MILLIS);

        for (int i = 0; i < TestConfiguration.SystemCharacteristics.N_CLIENTS; i++) {
            TestClientThread testClient = new TestClientThread(i, service, resultService, simulationEnd);
            Thread t = new Thread(testClient);
            t.start();
        }
    }

    @PreDestroy
    public void preDestroy() {
        for(TestClientThread testClientThread : simulationThreads) {
            testClientThread.stop();
        }
    }
}
