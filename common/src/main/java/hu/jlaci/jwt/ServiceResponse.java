package hu.jlaci.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {

    private static final int DATA_LENGTH = 1000;
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";

    private String data;

    public static ServiceResponse randomResponse() {
        return new ServiceResponse(Util.getRandomString(DATA_LENGTH));
    }
}
