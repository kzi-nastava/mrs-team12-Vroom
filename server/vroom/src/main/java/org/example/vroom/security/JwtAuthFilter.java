package org.example.vroom.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.vroom.entities.User;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        Long id = null;
        String type = null;


        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if(token != null){
            email = jwtService.extractEmail(token);
            id = jwtService.extractUserId(token);
            type = jwtService.extractUserType(token);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent() && email != null && id != null && type != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userOptional.get();

            if(jwtService.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                  user,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + type))
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // change this to ignore auth + main + route estimation endpoints
        return path.startsWith("/api/auth/")
                || path.startsWith("/api/admins/")
                || path.startsWith("/api/rides/")
                || path.startsWith("/api/routes/")
                || path.startsWith("/api/main/")
                || path.startsWith("/api/profile/driver")
                || path.startsWith("/api/profile/user")
                || path.startsWith("/api/drivers/");
    }
}
