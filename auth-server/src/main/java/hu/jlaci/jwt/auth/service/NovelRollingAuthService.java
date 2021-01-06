package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.TestConfiguration;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.data.UserRoleEntity;
import hu.jlaci.jwt.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("NovelRolling")
@Service
@Slf4j
public class NovelRollingAuthService extends AuthService {

    private final List<String> secrets;

    public NovelRollingAuthService(UserService userService, RefreshTokenService refreshTokenService) {
        super(userService, refreshTokenService);
        this.secrets = new ArrayList<>(TestConfiguration.SystemCharacteristics.K);
        for(int i = 0; i < TestConfiguration.SystemCharacteristics.K; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 48; j++) {
                sb.append("1");
            }
            secrets.add(sb.toString());
        }
    }

    @Override
    public void logout(Long userId, String accessToken) {
        refreshTokenService.deleteRefreshTokens(userId);
        regenerateSecret((int) (userId % TestConfiguration.SystemCharacteristics.K));
    }

    private void regenerateSecret(int clientGroup) {
        log.info("Secret changed for group {}.", clientGroup);
        BigInteger currentSecret = new BigInteger(secrets.get(clientGroup));
        secrets.set(clientGroup,  currentSecret.add(BigInteger.ONE).toString());
    }


    protected String buildAccessToken(UserEntity user) {
        int clientGroup = (int) (user.getId() % TestConfiguration.SystemCharacteristics.K);
        log.info("User {} assigned to group {}", user, clientGroup);
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim(Constants.JWT_CLAIM_USER_ID, user.getId())
                .claim(Constants.JWT_CLAIM_USERNAME, user.getUsername())
                .claim(Constants.JWT_CLAIM_EMAIL, user.getEmail())
                .claim(Constants.JWT_CLAIM_ROLES, user.getRoles().stream().map(UserRoleEntity::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, secrets.get(clientGroup))
                .compact();
    }
}
