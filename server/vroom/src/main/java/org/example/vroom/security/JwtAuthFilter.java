package org.example.vroom.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.vroom.entities.User;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private UserDetailsService userDetailsService;

    protected final Log LOGGER = LogFactory.getLog(getClass());

    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }



    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String email = null;
        String token = jwtService.getToken(request);

        try{
            if(token != null && !token.equals("")){
                email = jwtService.extractEmail(token);

                if(email != null && !email.equals("")){
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    if(jwtService.validateToken(token, userDetails)){
                        JWTBasedAuthentication authentication = new JWTBasedAuthentication(userDetails);
                        authentication.setToken(token);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }catch(ExpiredJwtException e){
            LOGGER.debug("Token expired");
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
                //|| path.startsWith("/api/profile/driver")
                //|| path.startsWith("/api/profile/user")
                || path.startsWith("/api/geo")
                || path.startsWith("/api/drivers/");
    }
}
