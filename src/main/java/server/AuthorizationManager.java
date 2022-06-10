package server;

import server.exceptions.CommunicatingException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface AuthorizationManager {
    String defaultSalt = "default";

    boolean isDone();
    boolean login(String login, String password);
    boolean register(String login, String password);

    static String shorterString(String string, int limit ) {
        return string.length()>limit ? string.substring(0, limit) : string;
    }

    static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CommunicatingException(e.getMessage());
        }
    }
}
