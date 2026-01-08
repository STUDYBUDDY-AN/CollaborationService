package com.studybuddy.collaboration_service.groups.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studybuddy.collaboration_service.groups.configs.GroupTestMockConfig;
import com.studybuddy.collaboration_service.groups.dto.Request.CreateGroupRequest;
import com.studybuddy.collaboration_service.groups.entities.StudyGroup;
import com.studybuddy.collaboration_service.groups.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
@Import(GroupTestMockConfig.class)
class GroupControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    GroupService groupService;

    private CreateGroupRequest req;
    private StudyGroup group;

    @BeforeEach
    void setup() {
        req = new CreateGroupRequest();
        req.setName("New Group");
        req.setDescription("Group Description");

        group = new StudyGroup();
        group.setId(UUID.randomUUID());
        group.setName("Test Group");
        group.setDescription("Description");
        group.setOwnerId(UUID.randomUUID());
    }

    // =====================================================================
    // 🔵 POSITIVE TESTS
    // =====================================================================
    @Nested
    @DisplayName("Positive Cases")
    class PositiveCases {

        @Test
        void createGroup_shouldReturnCreated() throws Exception {
            String userId = "11111111-1111-1111-1111-111111111111";

            when(groupService.createGroup(any(), any(), any()))
                    .thenReturn(group.getId());

            mockMvc.perform(post("/api/v1/groups")
                            .header("X-User-Id", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.groupId").value(group.getId().toString()));

            ArgumentCaptor<String> nameCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> descCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<UUID> ownerCap = ArgumentCaptor.forClass(UUID.class);

            verify(groupService).createGroup(nameCap.capture(), descCap.capture(), ownerCap.capture());

            assert nameCap.getValue().equals("New Group");
            assert descCap.getValue().equals("Group Description");
            assert ownerCap.getValue().toString().equals(userId);
        }

        @Test
        void joinGroup_shouldReturnOk() throws Exception {
            UUID id = UUID.randomUUID();
            String userId = "11111111-1111-1111-2222-111111111111";

            mockMvc.perform(post("/api/v1/groups/{id}/join", id)
                            .header("X-User-Id", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Group joined successfully"));

            verify(groupService).addMemberToGroup(eq(id), eq(UUID.fromString(userId)));
        }

        @Test
        void getGroup_shouldReturnExpectedJson() throws Exception {
            UUID id = UUID.randomUUID();
            group.setId(id);

            when(groupService.getGroup(id)).thenReturn(group);

            mockMvc.perform(get("/api/v1/groups/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.name").value("Test Group"))
                    .andExpect(jsonPath("$.description").value("Description"))
                    .andExpect(jsonPath("$.ownerId").value(group.getOwnerId().toString()));
        }

        @Test
        void getMyGroups_shouldReturnList() throws Exception {
            String userId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

            when(groupService.getAllGroupsOfUser(any()))
                    .thenReturn(List.of(group));

            mockMvc.perform(get("/api/v1/groups/me")
                            .header("X-User-Id", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Test Group"));

            verify(groupService).getAllGroupsOfUser(UUID.fromString(userId));
        }

        @Test
        void getMembers_shouldReturnMemberList() throws Exception {
            UUID id = UUID.randomUUID();
            List<UUID> members = List.of(UUID.randomUUID(), UUID.randomUUID());

            when(groupService.getMembers(id)).thenReturn(members);

            mockMvc.perform(get("/api/v1/groups/{id}/members", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.members.length()").value(2));

            verify(groupService).getMembers(id);
        }

        @Test
        void getAllGroups_shouldReturnList() throws Exception {
            when(groupService.getAllGroups()).thenReturn(List.of(group));

            mockMvc.perform(get("/api/v1/groups/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Test Group"));
        }

        @Test
        void getAllGroups_shouldReturnEmpty() throws Exception {
            when(groupService.getAllGroups()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/groups/all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // =====================================================================
    // 🔴 NEGATIVE / ERROR TESTS
    // =====================================================================
    @Nested
    @DisplayName("Negative Cases")
    class NegativeCases {

        @Test
        void joinGroup_shouldFailWhenServiceThrows() throws Exception {
            UUID id = UUID.randomUUID();
            String userId = UUID.randomUUID().toString();

            doThrow(new RuntimeException("failed"))
                    .when(groupService).addMemberToGroup(eq(id), any());

            mockMvc.perform(post("/api/v1/groups/{id}/join", id)
                            .header("X-User-Id", userId))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        void getGroup_shouldReturn500_whenServiceFails() throws Exception {
            UUID id = UUID.randomUUID();

            when(groupService.getGroup(id))
                    .thenThrow(new RuntimeException("not found"));

            mockMvc.perform(get("/api/v1/groups/{id}", id))
                    .andExpect(status().isInternalServerError());
        }
    }
}
