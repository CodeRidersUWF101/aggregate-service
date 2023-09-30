package com.coderiders.AggregateService.config;

import com.coderiders.AggregateService.models.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JwtDecodingFilter extends OncePerRequestFilter {

    @Value("${keys.jwt}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String jwt = authorizationHeader.substring(7);
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(jwt)
                    .getBody();

            String clerkId = claims.get("clerk_id").toString();
            List<String> roles = (List<String>) claims.get("roles");
            String firstName = (String) claims.get("first_name");
            String lastName = (String) claims.get("last_name");
            String username = (String) claims.get("username");
            Map<String, String> userOrganizations = (Map<String, String>) claims.get("userOrganizations");

            UserContext.create(clerkId, roles, firstName, lastName, username, userOrganizations);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            UserContext.clearCurrentUserContext();
        }
    }
}