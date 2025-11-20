package com.booklibrary.controller;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booklibrary.entity.UserEntity;
import com.booklibrary.repository.UserRepository;
import com.booklibrary.services.AuthService;

@Controller
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepo;

    public AuthController(AuthService authService, UserRepository userRepo) {
        this.authService = authService;
        this.userRepo = userRepo;
    }

    // register form
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userDto", new HashMap<String,String>());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String password,
                             Model model) {
        try {
            authService.register(fullName, email, password);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // login mapping (login page)
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/login";
    }



    // forgot password form
    @GetMapping("/forgot-password")
    public String forgotForm() { return "forgot-password"; }

    @PostMapping("/forgot-password")
    public String doForgot(@RequestParam String email, Model model) {
        try {
            authService.sendPasswordResetEmail(email);
            model.addAttribute("message", "Password reset link sent if email exists.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "forgot-password";
    }

    // token link comes here
    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        try {
            UserEntity user = authService.validatePasswordResetToken(token);
            model.addAttribute("token", token);
            return "reset-password";
        } catch(Exception e) {
            model.addAttribute("error", e.getMessage());
            return "forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String doReset(@RequestParam String token,
                          @RequestParam String password,
                          Model model) {
        try {
            UserEntity user = authService.validatePasswordResetToken(token);
            authService.changeUserPassword(user, password);
            return "redirect:/login?resetSuccess";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "reset-password";
        }
    }
}

