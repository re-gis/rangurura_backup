package com.backend.proj.Services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    public String extractUserEmail(String token);
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    public Claims extractAllClaims(String token);
    public Key getSigninKey();
    public String generateToken(UserDetails userDetails);
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
    public Boolean isTokenValid(String token, UserDetails userDetails);
    public Boolean isTokenExpired(String token);
    public Date extractExpiration(String token);
}
