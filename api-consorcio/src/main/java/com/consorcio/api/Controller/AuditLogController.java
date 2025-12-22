package com.consorcio.api.controller;

import com.consorcio.api.dto.GroupDTO.AuditLogResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.AuditLogService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /* ======================================================
       LIST AUDIT LOGS
       GET /api/groups/{uuid}/audit-logs
    ====================================================== */
    @GetMapping("/{groupUuid}/audit-logs")
    public List<AuditLogResponseDTO> list(
            @PathVariable UUID groupUuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return auditLogService.listLogs(groupUuid, user);
    }
}
