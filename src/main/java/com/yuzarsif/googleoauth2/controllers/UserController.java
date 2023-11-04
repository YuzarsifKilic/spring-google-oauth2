package com.yuzarsif.googleoauth2.controllers;

import com.yuzarsif.googleoauth2.security.SecurityService;
import com.yuzarsif.googleoauth2.security.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final SecurityService securityService;

    public UserController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping
    public String getUser(@AuthenticationPrincipal SecurityUser securityUser) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityUser.getEmail();
    }
}
