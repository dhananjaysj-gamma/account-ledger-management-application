package tech.zeta.account_ledger_management_app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JWTService {
    private String secretKey;

    public JWTService() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        SecretKey sKey = keyGenerator.generateKey();
        secretKey  = Base64.getEncoder().encodeToString(sKey.getEncoded());
    }

    public String generateToken(String username)
    {

        Map<String, Object> claims1 = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims1)
                .subject(username)
                .issuedAt(new Date(System
                        .currentTimeMillis()))
                .expiration(new Date(System
                        .currentTimeMillis()
                        +60*60*30))
                .and()
                .signWith(getKey())
                .compact();
    }
    private SecretKey getKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes) ;
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);

    }

    private <T> T  extractClaim(String token, Function<Claims,T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);

    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token); // Extracts expiration date from the token
        return expirationDate.before(new Date()); // Returns true if expired, false if valid
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
