package hu.jlaci.jwt.tester;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
import hu.jlaci.jwt.ServiceResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TestUtilityService {

    private RestTemplate restTemplate;

    public TestUtilityService() {
        restTemplate = new RestTemplate();
    }

    public AuthResponse login(String username, String password) {
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("http://localhost:8080/auth/authenticate", new AuthRequest(username, password), AuthResponse.class);
        return getBody(authResponse);
    }

    public void logout(Long userId, String accessToken) {
        String url = "http://localhost:8080/auth/logout?userId=" + userId;
        if (accessToken != null) {
            url += "&accessToken=" + accessToken;
        }

        ResponseEntity<Void> response = restTemplate.postForEntity(url, null, Void.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Hibás kérés! Kód: " + response.getStatusCodeValue());
        }
    }

    public AuthResponse exchangeRefreshToken(String refreshToken) {
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity("http://localhost:8080/auth/request-new-token?refreshToken="+ refreshToken, null, AuthResponse.class);
        return getBody(authResponse);
    }

    public boolean consumeFooService(String accessToken) {
        return consumeService("http://localhost:8081/foo", accessToken);
    }

    public boolean consumeBarService(String accessToken) {
        return consumeService("http://localhost:8082/bar", accessToken);
    }

    private boolean consumeService(String serviceUrl, String accessToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set("Authorization", accessToken);
        try {
            ResponseEntity<ServiceResponse> serviceResponse = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(headers), ServiceResponse.class);
            return serviceResponse.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    private <T> T getBody(ResponseEntity<T> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Hibás kérés! Kód: " + responseEntity.getStatusCodeValue());
        }
    }
}
