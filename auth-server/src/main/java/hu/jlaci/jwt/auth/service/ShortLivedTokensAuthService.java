package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.data.UserRoleEntity;
import hu.jlaci.jwt.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("ShortLived")
@Service
public class ShortLivedTokensAuthService extends AuthService {

    @Value("${jwt.short-lived.ttl}")
    private int ttl;

    @Value("${jwt.short-lived.secret}")
    private String secret;

    public ShortLivedTokensAuthService(UserService userService, RefreshTokenService refreshTokenService) {
        super(userService, refreshTokenService);
    }

    @Override
    public void logout(Long userId, String accessToken) {
        refreshTokenService.deleteRefreshTokens(userId);
    }

    @Override
    public String buildAccessToken(UserEntity user) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(new Date().getTime() + ttl * 1000))
                .claim(Constants.JWT_CLAIM_USER_ID, user.getId())
                .claim(Constants.JWT_CLAIM_USERNAME, user.getUsername())
                .claim(Constants.JWT_CLAIM_EMAIL, user.getEmail())
                .claim(Constants.JWT_CLAIM_ROLES, user.getRoles().stream().map(UserRoleEntity::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}
