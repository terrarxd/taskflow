package ru.terrarXD.max_bot;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AuthVerifier {

    public static boolean verify(String initData, String botToken) {
        return verify(initData, botToken, 86400);
    }

    public static boolean verify(String initData, String botToken, int maxAgeSeconds) {
        if (initData == null || botToken == null || initData.trim().isEmpty()) {
            return false;
        }
        try {
            String decodedInitData;
            try {
                decodedInitData = URLDecoder.decode(initData, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                return false;
            }
            Map<String, String> data = new TreeMap<>();
            String receivedHash = null;
            long authDate = 0;
            String[] pairs = decodedInitData.split("&");
            for (String pair : pairs) {
                if (pair.isEmpty() || !pair.contains("=")) continue;
                String[] parts = pair.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();
                data.put(key, value);

                if ("hash".equals(key)) {
                    receivedHash = value;
                }
                if ("auth_date".equals(key)) {
                    authDate = Long.parseLong(value);
                }
            }
            if (receivedHash == null || data.isEmpty()) {
                return false;
            }
            long now = System.currentTimeMillis() / 1000;
            if (authDate == 0 || now - authDate > maxAgeSeconds) {
                return false;
            }
            StringBuilder checkString = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : data.entrySet()) {
                if ("hash".equals(entry.getKey())) continue;
                if (!first) {
                    checkString.append("\n");
                }
                checkString.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            String dataCheckString = checkString.toString();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] secretBytes = mac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            byte[] computedBytes = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String computedHash = bytesToHex(computedBytes);
            boolean valid = MessageDigest.isEqual(computedHash.getBytes(StandardCharsets.UTF_8), receivedHash.getBytes(StandardCharsets.UTF_8));
            return valid;

        } catch (NumberFormatException e) {
            return false;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}