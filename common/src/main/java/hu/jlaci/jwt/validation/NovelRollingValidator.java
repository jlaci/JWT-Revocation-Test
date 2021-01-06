package hu.jlaci.jwt.validation;

import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.TestConfiguration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("NovelRolling")
public class NovelRollingValidator implements TokenValidator {
    private final int MAX_NEXT_SECRET_CHECK = 10;
    private final List<String> secrets = new ArrayList<>(TestConfiguration.SystemCharacteristics.K);

    public NovelRollingValidator() {
        for(int i = 0; i < TestConfiguration.SystemCharacteristics.K; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 48; j++) {
                sb.append("1");
            }
            secrets.add(sb.toString());
        }
    }

    @Override
    public boolean isValid(String token) {
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        int clientGroup = (int) (Jwts.parser().parseClaimsJwt(unsignedToken).getBody().get(Constants.JWT_CLAIM_USER_ID, Long.class) % TestConfiguration.SystemCharacteristics.K);


        String secret = secrets.get(clientGroup);
        for(int tries = 0; tries < MAX_NEXT_SECRET_CHECK; tries++) {
            if(isValid(token, secret)) {
                secrets.set(clientGroup, secret);
                return true;
            } else {
                System.out.println("Secret" + secret + " nem valid, következő ellenőrzése.");
                secret = new BigInteger(secret).add(BigInteger.ONE).toString();
            }
        }

        return false;
    }

    private boolean isValid(String token, String secret) {
        try {
            Jwts.parser().setSigningKey(secret).parse(token);
        } catch (SignatureException e) {
            return false;
        }
        return true;
    }

    public void setSecret(int group, String newSecret) {
        secrets.set(group, newSecret);
    }
}
