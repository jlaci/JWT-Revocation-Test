package hu.jlaci.jwt.validation;

public interface TokenValidator {
    boolean isValid(String token);
}
