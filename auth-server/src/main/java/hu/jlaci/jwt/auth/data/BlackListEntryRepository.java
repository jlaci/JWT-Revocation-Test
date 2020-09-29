package hu.jlaci.jwt.auth.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListEntryRepository  extends JpaRepository<BlackListEntryEntity, Long> {

    Optional<BlackListEntryEntity> findByToken(String token);

}

