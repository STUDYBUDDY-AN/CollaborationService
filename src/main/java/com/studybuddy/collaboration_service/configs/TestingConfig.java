package com.studybuddy.collaboration_service.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class TestingConfig {

    @Value("${testing.profile.name}")
    private String profileName;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    public String getProfileName() {
        return profileName;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }
}
