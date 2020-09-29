package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;

import java.util.Optional;

public interface AuthService {

    Optional<AuthResponse> authenticate(AuthRequest authRequest);

    Optional<AuthResponse> requestNewToken(String refreshToken);

    void logout(Long userId, String accessToken);

}
