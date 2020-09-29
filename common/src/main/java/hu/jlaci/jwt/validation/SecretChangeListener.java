package hu.jlaci.jwt.validation;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secret-change")
@Profile("SecretChange")
@AllArgsConstructor
public class SecretChangeListener {

    private SecretChangeValidator secretChangeValidator;

    @PostMapping
    public void secretChanged(@RequestBody String newSecret) {
        secretChangeValidator.setSecret(newSecret);
    }
}
