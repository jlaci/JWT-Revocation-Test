package hu.jlaci.jwt;

import java.util.Random;

public class Util {

    public static String getRandomString(int length) {
        Random random = new Random();
        String acceptedChars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(acceptedChars.charAt(random.nextInt(acceptedChars.length())));
        }

        return sb.toString();
    }

}
