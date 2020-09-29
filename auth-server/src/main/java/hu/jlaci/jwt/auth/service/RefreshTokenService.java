package hu.jlaci.jwt.auth.service;

import hu.jlaci.jwt.Util;
import hu.jlaci.jwt.auth.data.RefreshTokenEntity;
import hu.jlaci.jwt.auth.data.RefreshTokenRepository;
import hu.jlaci.jwt.user.data.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private static final int REFRESH_TOKEN_LENGTH = 64;
    private static final int REFRESH_TOKEN_TTL_HOURS = 24;

    private RefreshTokenRepository refreshTokenRepository;

    public UserEntity validateRefreshToken(String rawRefreshToken) {
        Optional<RefreshTokenEntity> token = refreshTokenRepository.findByToken(rawRefreshToken);
        if (token.isPresent() && token.get().getValidUntil().isAfter(Instant.now())) {
            UserEntity user = token.get().getUser();
            refreshTokenRepository.delete(token.get());
            return user;
        } else {
            throw new InvalidTokenException();
        }
    }

    public String createRefreshToken(UserEntity userEntity) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setToken(Util.getRandomString(64));
        refreshTokenEntity.setUser(userEntity);
        refreshTokenEntity.setValidUntil(Instant.now().plus(REFRESH_TOKEN_TTL_HOURS, ChronoUnit.HOURS));
        return refreshTokenRepository.save(refreshTokenEntity).getToken();
    }

    @Transactional
    public void deleteRefreshTokens(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidTokenException extends RuntimeException {

    }

}
