package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.service.UserService;

import java.util.Optional;

public abstract class AuthService {

    protected UserService userService;
    protected RefreshTokenService refreshTokenService;

    public AuthService(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    public Optional<AuthResponse> authenticate(AuthRequest authRequest) {
        try {
            UserEntity user = userService.authenticate(authRequest.getUsername(), authRequest.getPassword());
            String accessToken = buildAccessToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(user);
            return Optional.of(new AuthResponse(accessToken, refreshToken));
        } catch (UserService.BadUsernameOrPasswordException e) {
            return Optional.empty();
        }
    }

    public Optional<AuthResponse> requestNewToken(String refreshToken) {
        try {
            UserEntity user = refreshTokenService.validateRefreshToken(refreshToken);
            String accessToken = buildAccessToken(user);
            String newRefreshToken = refreshTokenService.createRefreshToken(user);
            return Optional.of(new AuthResponse(accessToken, newRefreshToken));
        } catch (RefreshTokenService.InvalidTokenException e) {
            return Optional.empty();
        }
    }

    public abstract void logout(Long userId, String accessToken);

    protected abstract String buildAccessToken(UserEntity userEntity);

}
