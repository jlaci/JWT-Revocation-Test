package hu.jlaci.jwt.cost;

import hu.jlaci.jwt.Util;
import hu.jlaci.jwt.auth.data.BlackListEntryEntity;
import hu.jlaci.jwt.auth.data.BlackListEntryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CostCalculatorDAO {

    private BlackListEntryRepository blackListEntryRepository;

    @Transactional
    public void doDatabaseOperation() {
        Optional<BlackListEntryEntity> entity = blackListEntryRepository.findByToken(Util.getRandomString(236));
        if (entity.isPresent()) {
            throw new RuntimeException("Token is blacklisted during cost benchmark, this should not have happened.");
        }
    }
}
