package com.storemate.JWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.google.common.base.Function;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Service
public class JwtUtil {
    
    private String secret = "kartik@2000";


    public String extractUserName(String token) {
        return extractClamis(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClamis(token, Claims::getExpiration);
    }

    public <T> T extractClamis(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }
    
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

   public String genrateToken(String userName,String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims,userName);
   }

   public String generateToken(String userName,String role) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims,userName);
   }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(SignatureAlgorithm.HS256, secret).compact();
        
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
