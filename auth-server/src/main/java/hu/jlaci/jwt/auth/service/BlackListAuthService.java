package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
import hu.jlaci.jwt.Constants;
import hu.jlaci.jwt.auth.data.BlackListEntryEntity;
import hu.jlaci.jwt.auth.data.BlackListEntryRepository;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.data.UserRoleEntity;
import hu.jlaci.jwt.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Profile("BlackList")
public class BlackListAuthService implements AuthService {

    @Value("${jwt.black-list.secret}")
    private String secret;

    private UserService userService;
    private RefreshTokenService refreshTokenService;
    private BlackListEntryRepository blackListEntryRepository;

    public BlackListAuthService(UserService userService, RefreshTokenService refreshTokenService, BlackListEntryRepository blackListEntryRepository) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.blackListEntryRepository = blackListEntryRepository;
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
    public void logout(Long userId, String accessToken) {
        blackListEntryRepository.save(new BlackListEntryEntity(accessToken));
    }

    private String buildToken(UserEntity user) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim(Constants.JWT_CLAIM_USER_ID, user.getId())
                .claim(Constants.JWT_CLAIM_USERNAME, user.getUsername())
                .claim(Constants.JWT_CLAIM_EMAIL, user.getEmail())
                .claim(Constants.JWT_CLAIM_ROLES, user.getRoles().stream().map(UserRoleEntity::getName).collect(Collectors.toList()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean isBlacklisted(String accessToken) {
        return blackListEntryRepository.findByToken(accessToken).isPresent();
    }
}
