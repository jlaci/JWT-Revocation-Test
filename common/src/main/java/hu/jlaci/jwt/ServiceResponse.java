package hu.jlaci.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {

    private static final int DATA_LENGTH = 1000;
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

    private String data;

    public static ServiceResponse randomResponse() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < DATA_LENGTH; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }

        return new ServiceResponse(sb.toString());
    }
}
