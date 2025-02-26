package com.kcb.recon.tool.authentication.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final static String SECRETKEY = "2b7e151628aed2a6abf7158809cf4f3c2b7e151628aed2a6abf7158809cf4f3c";

    //30 Minutes
    private static final long tokenExpirationTime = 1000 * 60 * 15;
    //2 Hours
    private static final long refreshTokenExpirationTime = 2 * 60 * 60 * 1000;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateLightweightToken(String userName) {
        return createToken(new HashMap<>(), userName, tokenExpirationTime);
    }

    public String generateRefreshToken(String userName) {
        return createToken(new HashMap<>(), userName, refreshTokenExpirationTime);
    }

    public String generateFullToken(String userName, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userName, tokenExpirationTime);
    }

    private String createToken(Map<String, Object> claims, String userName, Long duration) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + duration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().
                setSigningKey(getSignInKey()).
                build().parseClaimsJws(token).
                getBody();
    }

    private Key getSignInKey() {
        byte keyBytes[] = Decoders.BASE64.decode(SECRETKEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
