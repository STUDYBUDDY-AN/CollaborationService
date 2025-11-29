package com.studybuddy.collaboration_service.groups.dto.Response;

import java.util.List;
import java.util.UUID;

public record MemberListResponse(List<UUID> members){}
