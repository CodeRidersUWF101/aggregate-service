package com.coderiders.AggregateService.config;

import com.coderiders.AggregateService.models.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JwtDecodingFilter extends OncePerRequestFilter {

    @Value("${keys.jwt}")
    private String secretKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/generateToken");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("No token provided");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String jwt = authorizationHeader.substring(7);
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(jwt)
                    .getBody();

            String clerkId = claims.get("userId").toString();
            List<String> roles = (List<String>) claims.get("roles");
            String firstName = (String) claims.get("firstName");
            String lastName = (String) claims.get("lastName");
            String username = (String) claims.get("username");
            Map<String, String> userOrganizations = (Map<String, String>) claims.get("userOrganizations");
            String imageUrl = (String) claims.get("imageUrl");

            UserContext.create(clerkId, roles, firstName, lastName, username, userOrganizations, imageUrl);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.warn("Invalid Token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            UserContext.clearCurrentUserContext();
        }
    }
}
