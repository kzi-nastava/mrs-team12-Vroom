package org.example.vroom.services;

import org.example.vroom.entities.User;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);

        if(user.isEmpty())
            throw new UserNotFoundException("User not found");
        else
            return user.get();
    }
}
