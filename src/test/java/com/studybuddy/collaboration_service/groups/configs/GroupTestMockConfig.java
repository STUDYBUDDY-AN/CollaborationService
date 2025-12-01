package com.studybuddy.collaboration_service.groups.configs;

import com.studybuddy.collaboration_service.groups.service.GroupService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GroupTestMockConfig {
    @Bean
    GroupService groupService() {
        return Mockito.mock(GroupService.class);
    }
}
