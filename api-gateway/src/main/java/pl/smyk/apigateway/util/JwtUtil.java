package pl.smyk.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
public class JwtUtil {
  private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

  public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

  public void validateToken(final String token) {
    Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts
      .parserBuilder()
      .setSigningKey(getSignInKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public boolean hasRole(String token, String role) {
    Claims claims = Jwts.parser().setSigningKey(getSignInKey()).parseClaimsJws(token).getBody();
    return claims.get("roles", List.class).contains(role);
  }
}
