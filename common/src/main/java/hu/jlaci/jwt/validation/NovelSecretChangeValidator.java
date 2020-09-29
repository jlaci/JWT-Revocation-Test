package hu.jlaci.jwt.validation;

import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.TestConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("Novel")
public class NovelSecretChangeValidator implements TokenValidator {

    private final List<String> secrets = new ArrayList<>(TestConfiguration.SystemCharacteristics.K);

    public NovelSecretChangeValidator() {
        for(int i = 0; i < TestConfiguration.SystemCharacteristics.K; i++) {
            secrets.add("");
        }
    }

    @Override
    public boolean isValid(String token) {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        int clientGroup = (int) (Jwts.parser().parseClaimsJwt(unsignedToken).getBody().get(Constants.JWT_CLAIM_USER_ID, Long.class) % TestConfiguration.SystemCharacteristics.K);

        try {
            Jwts.parser().setSigningKey(secrets.get(clientGroup)).parse(token);
        } catch (SignatureException e) {
            return false;
        }
        return true;
    }

    public void setSecret(int group, String newSecret) {
        secrets.set(group, newSecret);
    }
}
