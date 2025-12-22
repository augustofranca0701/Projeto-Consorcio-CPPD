package com.consorcio.api.controller;

import com.consorcio.api.dto.GroupDTO.*;
import com.consorcio.api.model.GroupModel;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.GroupService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.consorcio.api.dto.GroupDTO.TransferAdminRequestDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /* ======================================================
       CREATE
    ====================================================== */
    @PostMapping
    public GroupModel create(
            @RequestBody CreateGroupDTO dto,
            @AuthenticationPrincipal UserModel user
    ) {
        return groupService.create(dto, user);
    }

    /* ======================================================
       LIST PUBLIC GROUPS
    ====================================================== */
    @GetMapping
    public List<GroupPublicResponseDTO> listPublic() {
        return groupService.listPublicGroups();
    }

    /* ======================================================
       LIST MY GROUPS
    ====================================================== */
    @GetMapping("/mine")
    public List<MyGroupResponseDTO> mine(
            @AuthenticationPrincipal UserModel user
    ) {
        return groupService.listMyGroups(user);
    }

    /* ======================================================
       DETAIL
    ====================================================== */
    @GetMapping("/{uuid}")
    public GroupDetailResponseDTO detail(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return groupService.getDetail(uuid, user);
    }

    /* ======================================================
       UPDATE (ACTIVATE / PRIVATE)
    ====================================================== */
    @PatchMapping("/{uuid}")
    public GroupModel update(
            @PathVariable UUID uuid,
            @RequestBody UpdateGroupDTO dto,
            @AuthenticationPrincipal UserModel user
    ) {
        return groupService.update(uuid, dto, user);
    }

    /* ======================================================
       CANCEL
    ====================================================== */
    @DeleteMapping("/{uuid}")
    public GroupModel cancel(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return groupService.cancel(uuid, user);
    }

    /* ======================================================
       FINALIZE
    ====================================================== */
    @PostMapping("/{uuid}/finalize")
    public GroupModel finalizeGroup(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return groupService.finalizeGroup(uuid, user);
    }

    /* ======================================================
   TRANSFER ADMIN
   POST /api/groups/{uuid}/transfer-admin
====================================================== */
@PostMapping("/{uuid}/transfer-admin")
public void transferAdmin(
        @PathVariable UUID uuid,
        @RequestBody TransferAdminRequestDTO dto,
        @AuthenticationPrincipal UserModel user
) {
    groupService.transferAdmin(uuid, dto.getNewAdminUuid(), user);
}

}
