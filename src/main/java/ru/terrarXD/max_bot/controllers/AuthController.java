package ru.terrarXD.max_bot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.terrarXD.max_bot.MaxBotApplication;
import ru.terrarXD.max_bot.AuthVerifier;
import ru.terrarXD.max_bot.data.structures.User;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/max")
    public ResponseEntity<?> authFromMax(@RequestBody Map<String, Object> payload,
                                         HttpServletResponse response) {
        String initData = (String) payload.get("initData");
        if (initData == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "missing initData"));
        }
        String botToken = MaxBotApplication.instance.TOKEN;
        if (!AuthVerifier.verify(initData, botToken)) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid initData"));
        }
        try {
            String decodedInitData = URLDecoder.decode(initData, StandardCharsets.UTF_8.toString());
            Map<String, String> data = new TreeMap<>();
            String[] pairs = decodedInitData.split("&");
            for (String pair : pairs) {
                if (pair.isEmpty() || !pair.contains("=")) continue;
                String[] parts = pair.split("=", 2);
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (!"hash".equals(key)) {
                    data.put(key, value);
                }
            }
            String userJson = data.get("user");
            if (userJson == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "missing user data"));
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> userMap = objectMapper.readValue(userJson, Map.class);
            Number idObj = (Number) userMap.get("id");
            if (idObj == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "missing id in user data"));
            }
            long maxId = idObj.longValue();
            String username = (String) userMap.get("username");

            User user = MaxBotApplication.instance.dataManager.getUserByID(maxId);
            if (user == null) {
                user = MaxBotApplication.instance.dataManager.createUser(maxId);
            }
            ResponseCookie cookie = ResponseCookie.from("token", user.auth())
                    .httpOnly(false)
                    .path("/")
                    .maxAge(Duration.ofHours(8))
                    .sameSite("None")
                    .secure(true)
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid data format"));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid user data format"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "auth failed"));
        }
    }
}