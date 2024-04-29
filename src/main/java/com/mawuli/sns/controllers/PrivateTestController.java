package com.mawuli.sns.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrivateTestController {

    @GetMapping("/private")
    public String privateTest(@AuthenticationPrincipal OAuth2User user, Model model) {
        model.addAttribute("body", user.getAttribute("name"));
        return "private";
    }
}
