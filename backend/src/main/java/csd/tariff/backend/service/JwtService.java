package csd.tariff.backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret; 

  @Value("${app.jwt.ttl-seconds:3600}")
  private long ttlSeconds;

  public String generate(String subjectEmail, Collection<? extends GrantedAuthority> authorities) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + ttlSeconds * 1000);
    String roles = authorities == null ? "" :
        authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(subjectEmail)
        .claim("roles", roles)
        .setIssuedAt(now)
        .setExpiration(exp)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parseClaims(String token) throws JwtException {
    Jws<Claims> jws = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parseClaimsJws(token);
    return jws.getBody();
  }

  public String extractEmail(String token) throws JwtException {
    return parseClaims(token).getSubject();
  }

  public boolean isValid(String token, UserDetails user) {
    try {
      if (token == null || user == null || token.trim().isEmpty()) {
        return false;
      }
      Claims c = parseClaims(token);
      boolean notExpired = c.getExpiration() == null || c.getExpiration().after(new Date());
      return notExpired && user.getUsername().equalsIgnoreCase(c.getSubject());
    } catch (JwtException e) {
      return false;
    }
  }
}
