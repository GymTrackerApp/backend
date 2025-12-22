package com.gymtracker.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymtracker.app.dto.response.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RequestMatcher publicEndpoints;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.publicEndpoints = new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/auth/sign-up"),
                PathPatternRequestMatcher.withDefaults().matcher("/auth/sign-in"),
                PathPatternRequestMatcher.withDefaults().matcher("/v3/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
                PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/exercises")
        );
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return publicEndpoints.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            ErrorResponse errorResponse = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Make sure that the authorization header has been included and that it contains JWT.");
            writeJsonErrorResponse(response, errorResponse);
            return;
        }

        String jwt = authorizationHeader.substring(7);
        String userId;

        try {
            userId = jwtService.extractSubject(jwt);
        } catch (JwtException e) {
            writeJsonErrorResponse(response, new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token."));
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,  userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (UsernameNotFoundException e) {
                writeJsonErrorResponse(response, new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: Invalid credentials or user not found."));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeJsonErrorResponse(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorResponse.statusCode());

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
