package com.consorcio.api.controller;

import com.consorcio.api.dto.PrizeDTO.PrizeResponseDTO;
import com.consorcio.api.model.UserModel;
import com.consorcio.api.service.PrizeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PrizeController {

    private final PrizeService prizeService;

    public PrizeController(PrizeService prizeService) {
        this.prizeService = prizeService;
    }

    /* ======================================================
       REGISTER PRIZE
       POST /api/groups/{uuid}/prizes
    ====================================================== */
    @PostMapping("/groups/{groupUuid}/prizes")
    public PrizeResponseDTO create(
            @PathVariable UUID groupUuid,
            @RequestParam UUID userUuid,
            @RequestParam LocalDate datePrize,
            @AuthenticationPrincipal UserModel admin
    ) {
        return prizeService.registerPrize(groupUuid, userUuid, datePrize, admin);
    }

    /* ======================================================
       LIST PRIZES (GROUP)
       GET /api/groups/{uuid}/prizes
    ====================================================== */
    @GetMapping("/groups/{groupUuid}/prizes")
    public List<PrizeResponseDTO> list(
            @PathVariable UUID groupUuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return prizeService.listGroupPrizes(groupUuid, user);
    }

    /* ======================================================
       GET PRIZE BY UUID
       GET /api/prizes/{uuid}
    ====================================================== */
    @GetMapping("/prizes/{prizeUuid}")
    public PrizeResponseDTO getByUuid(
            @PathVariable UUID prizeUuid,
            @AuthenticationPrincipal UserModel user
    ) {
        return prizeService.getPrize(prizeUuid, user);
    }
}
