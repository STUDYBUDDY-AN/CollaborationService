package com.studybuddy.collaboration_service.groups.controller;

import com.studybuddy.collaboration_service.groups.dto.Request.CreateGroupRequest;
import com.studybuddy.collaboration_service.groups.dto.Response.CreateGroupResponse;
import com.studybuddy.collaboration_service.groups.dto.Response.GroupResponse;
import com.studybuddy.collaboration_service.groups.dto.Response.MemberListResponse;
import com.studybuddy.collaboration_service.groups.entities.GroupMember;
import com.studybuddy.collaboration_service.groups.entities.StudyGroup;
import com.studybuddy.collaboration_service.groups.service.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@AllArgsConstructor
public class GroupController {

    private final GroupService groupService;

    //----------------------------------------------
    // Create Group
    //----------------------------------------------
    @PostMapping
    public ResponseEntity<CreateGroupResponse> createGroup(
            @RequestBody CreateGroupRequest request,
            Authentication authentication
    ) {
        UUID ownerId = UUID.fromString(authentication.getName());

        UUID groupId = groupService.createGroup(
                request.getName(),
                request.getDescription(),
                ownerId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new CreateGroupResponse(groupId));
    }

    //----------------------------------------------
    // Join Group
    //----------------------------------------------
    @PostMapping("/{groupId}/join")
    public ResponseEntity<String> joinGroup(
            @PathVariable UUID groupId,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName( ));

        groupService.addMemberToGroup(groupId, userId);
        return ResponseEntity.ok("Group joined successfully");
    }

    //----------------------------------------------
    // Get Group
    //----------------------------------------------
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(
            @PathVariable UUID groupId
    ) {
        StudyGroup group = groupService.getGroup(groupId);

        return ResponseEntity.ok(new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getOwnerId()
        ));
    }

    //----------------------------------------------
    // Get My Groups
    //----------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<List<GroupResponse>> getMyGroups(
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());
        var groups = groupService.getAllGroupsOfUser(userId);

        var response = groups.stream()
                .map(group -> new GroupResponse(
                        group.getId(),
                        group.getName(),
                        group.getDescription(),
                        group.getOwnerId()
                )).toList();

        return ResponseEntity.ok(response);
    }

    //----------------------------------------------
    // Get Group Members
    //----------------------------------------------
    @GetMapping("/{groupId}/members")
    public ResponseEntity<MemberListResponse> getMembers(
            @PathVariable UUID groupId,
            Authentication authentication
    ) {
        List<UUID> members = groupService.getMembers(groupId);
        return ResponseEntity.ok(new MemberListResponse(members));
    }

    //----------------------------------------------
    // Get All Groups
    //----------------------------------------------
    @GetMapping("/all")
    public ResponseEntity<List<GroupResponse>> getAllGroups() {

        var groups = groupService.getAllGroups();
        var response = groups.stream()
                .map(group -> new GroupResponse(
                        group.getId(),
                        group.getName(),
                        group.getDescription(),
                        group.getOwnerId()
                )).toList();

        return ResponseEntity.ok(response);
    }
}
