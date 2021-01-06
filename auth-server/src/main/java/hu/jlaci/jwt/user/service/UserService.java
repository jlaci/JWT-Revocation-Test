package hu.jlaci.jwt.user.service;

import hu.jlaci.jwt.TestConfiguration;
import hu.jlaci.jwt.user.data.UserEntity;
import hu.jlaci.jwt.user.data.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserEntity authenticate(String username, String password) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isPresent()) {
            if (passwordEncoder.matches(password, userEntity.get().getPassword())) {
                return userEntity.get();
            } else {
                throw new BadUsernameOrPasswordException();
            }
        } else {
            throw new BadUsernameOrPasswordException();
        }
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < TestConfiguration.SystemCharacteristics.N_CLIENTS; i++) {
            String username = "user" + i;
            if (!userRepository.existsByUsername(username)) {
                log.info("TestUser {} doesn't exist, creating.", username);
                UserEntity userEntity = new UserEntity();
                userEntity.setId((long)i);
                userEntity.setUsername(username);
                userEntity.setPassword(passwordEncoder.encode("password"));
                userEntity.setEmail(username + "@jwt.io");
                userEntity.setRoles(Collections.emptyList());
                userRepository.save(userEntity);
            }
        }
    }


    public static class BadUsernameOrPasswordException extends RuntimeException {

    }
}
