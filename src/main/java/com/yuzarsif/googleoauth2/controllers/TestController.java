package com.yuzarsif.googleoauth2.controllers;

import com.yuzarsif.googleoauth2.security.SecurityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final SecurityService securityService;

    public TestController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping
    public String test() {
        return "test";
    }
}
