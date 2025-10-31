package com.consorcio.api.Controller;

import com.consorcio.api.Service.PrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("prizes")
public class PrizeController
{
    @Autowired
    private PrizeService prizeService;

    @PostMapping("/sort/{groupId}")
    public ResponseEntity<Map<String, Object>> sortUserForPrize(@PathVariable("groupId") Long groupId)
    {
        return prizeService.sortUserForPrize(groupId);
    }


    // Apenas para Teste

    @GetMapping("/next/{groupId}")
    public ResponseEntity<Object> nextPrizeDate(@PathVariable("groupId") Long groupId)
    {
        return prizeService.findFirstAvailablePrizeByGroupId(groupId);
    }

    @GetMapping("/user/{groupId}")
    public ResponseEntity<Object> usersWithoutPrize(@PathVariable("groupId") Long groupId)
    {
        return prizeService.getUsersWithoutPrizes(groupId);
    }
}
