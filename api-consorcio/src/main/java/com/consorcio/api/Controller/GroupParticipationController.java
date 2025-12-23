package com.consorcio.api.controller;

import com.consorcio.api.dto.GroupDTO.*;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.GroupParticipationService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupParticipationController {

    private final GroupParticipationService service;

    public GroupParticipationController(GroupParticipationService service) {
        this.service = service;
    }

    @PostMapping("/{uuid}/join")
    public void joinPublic(@PathVariable UUID uuid) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.joinPublic(uuid, user);
    }

    @PostMapping("/{uuid}/join-requests")
    public void requestJoin(@PathVariable UUID uuid) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.requestJoin(uuid, user);
    }

    @GetMapping("/{uuid}/join-requests")
    public List<JoinRequestResponseDTO> listRequests(@PathVariable UUID uuid) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.listJoinRequests(uuid, user);
    }

    @PostMapping("/{uuid}/join-requests/{requestUuid}/approve")
    public void approve(
            @PathVariable UUID uuid,
            @PathVariable UUID requestUuid
    ) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.approve(uuid, requestUuid, user);
    }

    @DeleteMapping("/{uuid}/join-requests/{requestUuid}")
    public void reject(
            @PathVariable UUID uuid,
            @PathVariable UUID requestUuid
    ) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.reject(uuid, requestUuid, user);
    }

    @DeleteMapping("/{uuid}/leave")
    public void leave(@PathVariable UUID uuid) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        service.leave(uuid, user);
    }

    @GetMapping("/{uuid}/users")
    public List<GroupUserResponseDTO> listUsers(@PathVariable UUID uuid) {

        UserModel user = (UserModel) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.listUsers(uuid, user);
    }
}
