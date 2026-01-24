package org.example.vroom.security;

import io.jsonwebtoken.Claims;
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
import org.springframework.security.core.Authentication;
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
        //System.out.println("JwtAuthFilter - Authorization header: " + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("==== JWT DEBUG START ====");
        System.out.println("Header: " + request.getHeader("Authorization"));

        try {
            String token = authHeader.substring(7);
            System.out.println("TOKEN RAW: " + token);

            Claims claims = jwtService.extractAllClaims(token);
            System.out.println("CLAIMS: " + claims);
            System.out.println("TYPE from token: " + claims.get("type"));
            System.out.println("EMAIL from token: " + claims.getSubject());
        } catch (Exception e) {
            System.out.println("JWT ERROR: " + e.getMessage());
        }
        System.out.println("==== JWT DEBUG END ====");
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
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        System.out.println("SPRING AUTH USER: " + auth.getName());
                        System.out.println("SPRING AUTHORITIES: " + auth.getAuthorities());
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
        return path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/register/driver")
                || path.startsWith("/api/auth/forgot-password")
                || path.startsWith("/api/auth/reset-password")
                || path.startsWith("/api/admins/")
                || path.startsWith("/api/rides/")
                || path.startsWith("/api/routes/")
                || path.startsWith("/api/main/")
                || path.startsWith("/api/drivers/rides")
                //|| path.startsWith("/api/profile/driver")
               // || path.startsWith("/api/profile/user")
                || path.startsWith("/api/geo");
                //|| path.startsWith("/api/drivers/");
    }
}
