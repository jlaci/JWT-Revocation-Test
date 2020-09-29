package hu.jlaci.jwt.validation;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/secret-change")
@Profile("Novel")
@AllArgsConstructor
public class NovelSecretChangeListener {

    private NovelSecretChangeValidator validator;

    @PostMapping
    public void secretChanged(@RequestParam int group, @RequestBody String newSecret) {
        validator.setSecret(group, newSecret);
    }
}
