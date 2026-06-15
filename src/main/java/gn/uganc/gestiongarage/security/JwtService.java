package gn.uganc.gestiongarage.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();

    private final String secret;
    private final long expirationSeconds;

    public JwtService(@Value("${app.security.jwt.secret:change-this-secret-key-for-production}") String secret,
                      @Value("${app.security.jwt.expiration-seconds:86400}") long expirationSeconds) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("""
                {"sub":"%s","roles":"%s","iat":%d,"exp":%d}
                """.formatted(escapeJson(userDetails.getUsername()), escapeJson(authorities),
                now.getEpochSecond(), now.plusSeconds(expirationSeconds).getEpochSecond()).trim());

        return header + "." + payload + "." + sign(header + "." + payload);
    }

    public Optional<String> extractUsername(String token) {
        try {
            Map<String, String> claims = parseAndValidate(token);
            return Optional.ofNullable(claims.get("sub"));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Map<String, String> claims = parseAndValidate(token);
            return userDetails.getUsername().equals(claims.get("sub"));
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private Map<String, String> parseAndValidate(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Token JWT invalide");
        }

        String expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new IllegalArgumentException("Signature JWT invalide");
        }

        String payload = new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        Map<String, String> claims = parseFlatJson(payload);
        long exp = Long.parseLong(claims.getOrDefault("exp", "0"));
        if (Instant.now().getEpochSecond() >= exp) {
            throw new IllegalArgumentException("Token JWT expiré");
        }
        return claims;
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return URL_ENCODER.encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Impossible de signer le token JWT", ex);
        }
    }

    private String encode(String value) {
        return URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private boolean constantTimeEquals(String left, String right) {
        return MessageDigestSafeEquals.equals(left.getBytes(StandardCharsets.UTF_8), right.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, String> parseFlatJson(String json) {
        return java.util.Arrays.stream(json.replace("{", "").replace("}", "").split(","))
                .map(entry -> entry.split(":", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> unquote(parts[0].trim()),
                        parts -> unquote(parts[1].trim())
                ));
    }

    private String unquote(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1)
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        return trimmed;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static final class MessageDigestSafeEquals {
        private static boolean equals(byte[] left, byte[] right) {
            if (left.length != right.length) {
                return false;
            }
            int result = 0;
            for (int i = 0; i < left.length; i++) {
                result |= left[i] ^ right[i];
            }
            return result == 0;
        }
    }
}
