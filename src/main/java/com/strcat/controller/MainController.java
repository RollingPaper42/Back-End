package com.strcat.controller;

import com.strcat.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final PictureRepository pictureRepository;

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
