package hu.jlaci.jwt.tester;

import hu.jlaci.jwt.TestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TestResultService {

    private Map<Long, TestResults> results = new HashMap<>();

    public void addResult(long id, TestResults testResults) {
        results.put(id, testResults);
        if (results.size() >= TestConfiguration.SystemCharacteristics.N_CLIENTS) {
            log.info("Simulation ended, results:");
            TestResults aggregated = new TestResults();

            for (Map.Entry<Long, TestResults> resultEntry : results.entrySet()) {
                log.info("Client {} result {}", resultEntry.getKey(), resultEntry.getValue());
                aggregated.add(resultEntry.getValue());
            }

            log.info("Aggregated result {}", aggregated);
        }
    }
}
