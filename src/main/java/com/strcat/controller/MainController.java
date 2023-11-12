package com.strcat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/")
    public String helloSpring() {
        return "Hello spring!!!";
    }

    @GetMapping("/login/success")
    public String loginSuccess() {
        return "login Success";
    }

    @GetMapping("/login/guard")
    public String guard() {
        return "guard OK";
    }
}
