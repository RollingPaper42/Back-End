package com.strcat.controller;

import com.strcat.service.ShortUrlService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController {
    private final ShortUrlService shortUrlService;

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    public String getShortUrl(
            @RequestParam("url") String url
    ) throws Exception {
        return shortUrlService.generateUrl(url);
    }
}
