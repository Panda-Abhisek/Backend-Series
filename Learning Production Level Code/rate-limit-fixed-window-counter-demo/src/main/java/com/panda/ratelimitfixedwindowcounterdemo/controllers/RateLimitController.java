package com.panda.ratelimitfixedwindowcounterdemo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RateLimitController {
    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("message", "Welcome to the Rate Limit Demo!");
        return "hello";
    }
}
