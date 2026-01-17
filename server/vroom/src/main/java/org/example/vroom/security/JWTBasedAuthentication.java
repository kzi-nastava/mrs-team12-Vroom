package org.example.vroom.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public class JWTBasedAuthentication extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String token;

    private final UserDetails principle;

    public JWTBasedAuthentication(UserDetails principle) {
        super(principle.getAuthorities());
        this.principle = principle;
    }


    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials(){
        return this.token;
    }

    @Override
    public UserDetails getPrincipal() {
        return principle;
    }
}
