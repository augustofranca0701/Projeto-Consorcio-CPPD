package com.consorcio.api.controller;

import com.consorcio.api.dto.GroupDTO.InviteResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.GroupInviteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupInviteController {

    private final GroupInviteService inviteService;

    public GroupInviteController(GroupInviteService inviteService) {
        this.inviteService = inviteService;
    }

    /* ======================================================
       CREATE INVITE
       POST /api/groups/{uuid}/invites
    ====================================================== */
    @PostMapping("/{groupUuid}/invites")
    public InviteResponseDTO createInvite(
            @PathVariable UUID groupUuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return inviteService.createInvite(groupUuid, user);
    }

    /* ======================================================
       JOIN BY INVITE
       POST /api/groups/join-by-invite
    ====================================================== */
    @PostMapping("/join-by-invite")
    public void joinByInvite(
            @RequestBody JoinByInviteRequest request,
            @AuthenticationPrincipal UserModel user
    ) {
        inviteService.joinByInvite(request.getInviteToken(), user);
    }

    /* DTO interno simples */
    static class JoinByInviteRequest {
        private String inviteToken;

        public String getInviteToken() {
            return inviteToken;
        }

        public void setInviteToken(String inviteToken) {
            this.inviteToken = inviteToken;
        }
    }
}
