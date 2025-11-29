package com.studybuddy.collaboration_service.groups.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StudyGroupDto {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;
}
