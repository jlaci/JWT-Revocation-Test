package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.AuthRequest;
import hu.jlaci.jwt.AuthResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Profile("Novel")
@Service
public class NovelAuthService implements AuthService {
    @Override
    public Optional<AuthResponse> authenticate(AuthRequest authRequest) {
        return Optional.empty();
    }

    @Override
    public Optional<AuthResponse> requestNewToken(String refreshToken) {
        return Optional.empty();
    }

    @Override
    public void logout(Long userId) {

    }
}
