package com.train.payment.security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class JWTUtil {

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    public String extractUsername(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, String givenusername) throws Exception {
        final String username = extractUsername(token);
        return (username.equals(givenusername)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws Exception {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws Exception {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) throws Exception {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private PublicKey getSignInPublicKey() throws Exception {
        // Read the public key from the file path
        String pathStrToUse = publicKeyPath.replace("file:", "");
        Path path = Paths.get(pathStrToUse);
        String publicKey = Files.readString(path);

        String key = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
