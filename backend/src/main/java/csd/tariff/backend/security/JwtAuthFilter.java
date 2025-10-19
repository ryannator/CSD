package csd.tariff.backend.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import csd.tariff.backend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwt;
  private final UserDetailsService uds;

  public JwtAuthFilter(JwtService jwt, UserDetailsService uds) {
    this.jwt = jwt;
    this.uds = uds;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getDispatcherType() == DispatcherType.ERROR
        || "OPTIONS".equalsIgnoreCase(request.getMethod());
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain)
      throws ServletException, IOException {

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      chain.doFilter(req, res);
      return;
    }

    String header = req.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        String email = jwt.extractEmail(token);
        UserDetails user = uds.loadUserByUsername(email);

        if (jwt.isValid(token, user)) {
          var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(auth);
          System.out.println("[JWT] OK subject=" + email + " authorities=" + user.getAuthorities());
        } else {
          System.out.println("[JWT] invalid: signature mismatch or username mismatch");
          res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
          return;
        }
      } catch (ExpiredJwtException e) {
        System.out.println("[JWT] invalid: " + e.getMessage());
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT expired");
        return;
      } catch (JwtException e) {
        System.out.println("[JWT] invalid: " + e.getMessage());
        // leave unauthenticated
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        return;
      }
    }

    chain.doFilter(req, res);
  }
}
