package com.booklibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booklibrary.services.AutoFixService;

@RestController
@RequestMapping("/admin")
public class AutoFixController {

    @Autowired
    private AutoFixService autoFixService;

    @GetMapping("/fix-empty-books")
    public String fixBooks() {
        autoFixService.fixMissingFields();
        return "Auto Fix Completed Successfully!";
    }
}

