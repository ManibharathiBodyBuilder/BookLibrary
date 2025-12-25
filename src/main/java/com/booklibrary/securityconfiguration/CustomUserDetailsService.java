package com.booklibrary.securityconfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found in DB"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}

