package com.kote.banking.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {
    private SecretKey secretKey;
    @Value("${jwt.password}")
    private  String password;
    @Value("${jwt.expiration}")
    private long expiration;

    @PostConstruct
    private void init(){
        this.secretKey = Keys.hmacShaKeyFor(password.getBytes());
    }


    public String generateToken(UserDetails userDetails, String tenantId){
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);
        claims.put("tenantId", tenantId);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    public Claims getAllClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public<T> T getClaim(String token, Function<Claims, T> claimsSFunction){
        Claims allClaims = getAllClaims(token);
        return claimsSFunction.apply(allClaims);
    }

    public Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    public String getUsername(String token){
        return getClaim(token, Claims::getSubject);
    }

    public Object getTenant(String token){
        return getClaim(token, claims -> claims.get("tenantId"));
    }
    public boolean validateToken(String token, UserDetails userDetails){
        Claims allClaims = getAllClaims(token);
        if (allClaims.getSubject().equals(userDetails.getUsername()) && allClaims.getExpiration().after(new Date())){
            return true;
        }
        return false;
    }
}
