package com.studybuddy.collaboration_service.common.controller;

import com.studybuddy.collaboration_service.configs.TestingConfig;
import lombok.RequiredArgsConstructor;
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
    private final TestingConfig config;

    public TestingController(Environment environment, TestingConfig config) {
        this.environment = environment;
        this.config = config;
    }

    @GetMapping("/profile")
    public String getActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        String profiles = activeProfiles.length > 0
                ? Arrays.toString(activeProfiles)
                : "default";

        return "Active Profile: " + profiles +
                "\n Profile Name: " + config.getProfileName() +
                "\n DB URL: " + config.getDatasourceUrl();
    }
}

