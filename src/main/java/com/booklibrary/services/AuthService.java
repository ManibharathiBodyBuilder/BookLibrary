package com.booklibrary.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.booklibrary.entity.PasswordResetToken;
import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.PasswordResetTokenRepository;
import com.booklibrary.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final String appUrl = "http://localhost:9090"; // change for prod

    public AuthService(UserRepository userRepo,
                       PasswordResetTokenRepository tokenRepo,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public UserEntity register(String fullName, String email, String rawPassword) {

        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already used");
        }

        UserEntity u = new UserEntity();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));

        u.setRole("USER");
        u.setEnabled(true);

        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());

        return userRepo.save(u);
    }



    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        tokenRepo.deleteByUser(user); // remove old
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepo.save(prt);
    }

    public void sendPasswordResetEmail(String username) {
        UserEntity user = userRepo.findByFullName(username)
                .orElseThrow(() -> new RuntimeException("No user found"));
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
        String link = appUrl + "/reset-password?token=" + token;
        String text = "Hello,\n\nClick to reset your password: " + link + "\n\nIf you didn't request, ignore.";
        emailService.sendSimpleMessage(user.getEmail(), "Password Reset Request", text);
    }

    public UserEntity validatePasswordResetToken(String token) {
        PasswordResetToken prt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (prt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }
        return prt.getUser();
    }

    public void changeUserPassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.deleteByUser(user);
    }
    
    /*public Optional<UserEntity> getLoggedUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        String email = auth.getName(); // This should be the username/email

        return userRepo.findByEmail(email);
    }*/
    
    public Optional<UserEntity> getLoggedUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() ||
                "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }

        String username = auth.getName(); // may be email OR fullName

        // Try full name (requires method in UserRepository)
        Optional<UserEntity> byName = userRepo.findByFullName(username);
        if (byName.isPresent()) {
            return byName;
        }
        
        Optional<UserEntity> byEmail = userRepo.findByEmail(username);
        if (byEmail.isPresent()) {
            return byEmail;
        }
        return Optional.empty();
    }



}

