package hu.jlaci.jwt.validation;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Profile("BlackList")
@Service
public class BlackListTokenValidator implements TokenValidator {

    @Value("${jwt.black-list.secret}")
    private String secret;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parse(token);
            ResponseEntity<Boolean> blacklistResponse = restTemplate.postForEntity("http://localhost:8080/auth/is-blacklisted?accessToken="+ token, null, Boolean.class);
            if (blacklistResponse.getBody() == null) {
                throw new RuntimeException("Wrong blacklist response!");
            }

            return !blacklistResponse.getBody();
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String getSigningKey() {
        return secret;
    }
}
