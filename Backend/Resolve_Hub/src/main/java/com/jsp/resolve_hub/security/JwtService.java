package com.jsp.resolve_hub.security;

import com.jsp.resolve_hub.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * Creates and reads JWT tokens.
 * A token = header.payload.signature. The payload holds the email (subject),
 * the role, the issue time and the expiry time. The signature proves the
 * token was created by this server and has not been edited.
 */
@Service
public class JwtService {

    // Both values come from application.properties, so no secret lives in code.
    @Value(value = "${jwt.secret}")
    private String jwt_secret ;

    @Value("${jwt.expiration}")
    private Long expriyTime;   // milliseconds

    /** Decodes the Base64 secret into the key used to sign and verify tokens. */
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor( Base64.getDecoder().decode(jwt_secret));
    }

    /** Used by AuthService to tell the client how long the token lives. */
    public Long getExpiryTime() {
        return expriyTime;
    }

    /** Builds a signed token for a user after a successful login. */
    public String generateToken(User user){
        Map<String , Object> claims=new HashMap<>();
        claims.put("role",user.getRoles().name());

        Date now=new Date();
        Date expiry=new Date(now.getTime()+expriyTime);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())     // who the token belongs to
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** true only if the signature is valid AND the token has not expired. */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(signingKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Returns the email stored in the token. */
    public String extractUserName(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /** Returns the role stored in the token (informational: the DB role is what authorization uses). */
    public String extractRoles(String token){
        Claims claims= Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role").toString();
    }
}