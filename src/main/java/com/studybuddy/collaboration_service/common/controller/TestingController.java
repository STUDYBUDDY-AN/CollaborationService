package com.studybuddy.collaboration_service.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/test")
public class TestingController {

    private final Environment environment;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${testing.profile.name}")
    private String profileName;

    public TestingController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/profile")
    public String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        String profiles = activeProfiles.length > 0 ? Arrays.toString(activeProfiles) : "default";
        return "Active Profile: " + profiles + "\n Profile Name: " + profileName + "\n DB URL: " + datasourceUrl;
    }
}
