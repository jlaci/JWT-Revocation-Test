package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("ShortLived")
@Service
public class ShortLivedTokensAuthService implements AuthService {

    @Value("${jwt.short-lived.ttl}")
    private int ttl;

    @Value("${jwt.short-lived.secret}")
    private String secret;

    private UserService userService;
    private RefreshTokenService refreshTokenService;

    public ShortLivedTokensAuthService(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Optional<AuthResponse> authenticate(AuthRequest authRequest) {
        try {
            UserEntity user = userService.authenticate(authRequest.getUsername(), authRequest.getPassword());
            String accessToken = buildToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user);
            return Optional.of(new AuthResponse(accessToken, refreshToken));
        } catch (UserService.BadUsernameOrPasswordException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AuthResponse> requestNewToken(String refreshToken) {
        try {
            UserEntity user = refreshTokenService.validateRefreshToken(refreshToken);
            String accessToken = buildToken(user);
            String newRefreshToken = refreshTokenService.createRefreshToken(user);
            return Optional.of(new AuthResponse(accessToken, newRefreshToken));
        } catch (RefreshTokenService.InvalidTokenException e) {
            return Optional.empty();
        }
    }

    @Override
    public void logout(Long userId) {
        refreshTokenService.deleteRefreshTokens(userId);
    }

    private String buildToken(UserEntity user) {
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
