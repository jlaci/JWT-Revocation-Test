package hu.jlaci.jwt.validation;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ShortLivedTokenValidator implements TokenValidator {

    @Value("${jwt.short-lived.secret}")
    private String secret;


    @Override
    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String getSigningKey() {
        return secret;
    }
}
