package hu.jlaci.jwt.cost;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.Util;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.data.UserRepository;
import hu.jlaci.jwt.user.data.UserRoleEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CostCalculatorService {

    private static final int ITERATIONS = 500;

    private RestTemplate restTemplate = new RestTemplate();
    private UserEntity testUser;

    private UserRepository userRepository;
    private CostCalculatorDAO costCalculatorDAO;

    public CostCalculatorService(UserRepository userRepository, CostCalculatorDAO costCalculatorDAO) {
        this.userRepository = userRepository;
        this.costCalculatorDAO = costCalculatorDAO;
    }

    public CostResponse calculateCosts() {
        testUser = userRepository.findAll().get(0);
        CostResponse response = new CostResponse();
        response.setCi(calculateIssueCost());
        response.setCv(calculateValidationCost());
        response.setCc(calculateCommunicationCost());
        response.setCd(calculateDataAccessCost());
        return response;
    }

    private double calculateIssueCost() {
        double totalIssueCost = 0;
        AuthResponse authResponse = login();

        for (int i = 0; i < ITERATIONS; i++) {
            Instant start = Instant.now();
            authResponse = exchangeRefreshToken(authResponse.getRefreshToken());
            Instant end = Instant.now();

            totalIssueCost += (end.toEpochMilli() - start.toEpochMilli()) / 1000.0;
        }

        double result = totalIssueCost / (double) ITERATIONS;
        log.info("Ci (issue cost): {}", result);
        return result;
    }

    private double calculateValidationCost() {
        String secret = Util.getRandomString(48);
        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim(Constants.JWT_CLAIM_USER_ID, testUser.getId())
                .claim(Constants.JWT_CLAIM_USERNAME, testUser.getUsername())
                .claim(Constants.JWT_CLAIM_EMAIL, testUser.getEmail())
                .claim(Constants.JWT_CLAIM_ROLES, testUser.getRoles().stream().map(UserRoleEntity::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        double totalValidationCost = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            Instant start = Instant.now();
            Jwts.parser().setSigningKey(secret).parse(token);
            Instant end = Instant.now();
            totalValidationCost += (end.toEpochMilli() - start.toEpochMilli()) / 1000.0;
        }

        double result = totalValidationCost / (double) ITERATIONS;
        log.info("Cv (validation cost): {}", result);
        return result;
    }

    private double calculateCommunicationCost() {
        double totalCommunicationCost = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            Instant start = Instant.now();
            ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:808" + (i % 2 == 0 ? "1" : "2") + "/benchmark", String.class); // Balance the requests between foo and bar
            Instant end = Instant.now();
            totalCommunicationCost += (end.toEpochMilli() - start.toEpochMilli()) / 1000.0;

            if (response.getBody() == null || !"OK".equals(response.getBody())) {
                throw new RuntimeException("Bad response for benchamrk!");
            }
        }

        double result = totalCommunicationCost / (double) ITERATIONS;
        log.info("Cc (communication cost): {}", result);
        return result;
    }

    private double calculateDataAccessCost() {
        double totalDataAccessCost = 0;

        for (int i = 0; i < ITERATIONS; i++) {
            Instant start = Instant.now();
            costCalculatorDAO.doDatabaseOperation();
            Instant end = Instant.now();
            totalDataAccessCost += (end.toEpochMilli() - start.toEpochMilli()) / 1000.0;
        }

        double result = totalDataAccessCost / (double) ITERATIONS;
        log.info("Cd (data access cost): {}", result);
        return result;
    }


    private AuthResponse login() {
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("http://localhost:8080/auth/authenticate", new AuthRequest(testUser.getUsername(), "password"), AuthResponse.class);
        return getBody(authResponse);
    }

    private AuthResponse exchangeRefreshToken(String refreshToken) {
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("http://localhost:8080/auth/request-new-token?refreshToken="+ refreshToken, null, AuthResponse.class);
        return getBody(authResponse);
    }


    private <T> T getBody(ResponseEntity<T> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Hibás kérés! Kód: " + responseEntity.getStatusCodeValue());
        }
    }
    
}
