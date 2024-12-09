package com.library.utils;

import com.library.exception.InvalidUserException;
import com.library.model.AuthenticationRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKeyString;
    @Value("${jwt.token.expirationinms}")
    private long keyExpiration;

    @Value("${spring.security.user.name}")
    private String userName;

    @Value("${spring.security.user.password}")
    private String password;

    private final SecretKey secretKey;

    public String generateToken(final AuthenticationRequest authRequest) throws InvalidUserException {
        if (isEmpty(authRequest.getUserName()) || isEmpty(authRequest.getPassword())) {
            throw new InvalidUserException("Please provide user credentials");
        }

        if (!isValidUser(authRequest)) {
            throw new InvalidUserException("Invalid Credentials");
        }

        return Jwts.builder()
                .setSubject(authRequest.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + keyExpiration)) // 10 hours validity
                .signWith(Keys.hmacShaKeyFor(secretKeyString.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(final String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }

        final String token = authorizationHeader.substring(7);
        if (!StringUtils.hasLength(token)) {
            return false;
        }

        return (userName.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    public String extractUsername(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKeyString.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isValidUser(final AuthenticationRequest authRequest) {
        return userName.equalsIgnoreCase(authRequest.getUserName()) && password.equalsIgnoreCase(authRequest.getPassword());
    }

    private boolean isTokenExpired(final String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKeyString.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
    private boolean isEmpty(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return false;
    }

}
