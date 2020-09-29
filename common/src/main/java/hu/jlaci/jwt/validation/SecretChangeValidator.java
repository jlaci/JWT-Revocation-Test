package hu.jlaci.jwt.validation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("SecretChange")
public class SecretChangeValidator implements TokenValidator {

    @Setter
    private String secret;

    @Override
    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).parse(token);
        } catch (SignatureException e) {
            return false;
        }
        return true;
    }

    public String getSigningKey() {
        if (secret == null) {
            throw new IllegalStateException("Secret not set!");
        }
        return secret;
    }
}
